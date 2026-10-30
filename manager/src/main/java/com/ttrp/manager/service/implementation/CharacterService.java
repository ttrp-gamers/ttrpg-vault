package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.character.CharacterRequest;
import com.ttrp.manager.dto.character.CharacterResponse;
import com.ttrp.manager.dto.character.CharacterCalculationRequest;
import com.ttrp.manager.dto.character.CharacterCalculationResponse;
import com.ttrp.manager.entity.Character;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.mapper.CharacterMapper;
import com.ttrp.manager.repository.CharacterRepository;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.service.declaration.ICharacterService;
import com.ttrp.manager.service.declaration.IRulesetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CharacterService implements ICharacterService {

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final CharacterMapper characterMapper;
    private final IRulesetService rulesetService;

    @Override
    @Transactional
    public CharacterResponse createCharacter(Long userId, CharacterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Character character = characterMapper.toEntity(request);
        character.setUser(user);
        character.setAttributes(copyAttributes(request.attributes()));
        applyCalculatedValues(character);

        Instant now = Instant.now();
        character.setCreatedAt(now);
        character.setUpdatedAt(now);

        // New characters start at full health when current HP is omitted.
        if (character.getCurrentHp() == null) {
            character.setCurrentHp(character.getMaxHp());
        }

        Character saved = characterRepository.save(character);
        return characterMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterResponse> getCharactersByUser(Long userId) {
        return characterRepository.findAllByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(characterMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterResponse getCharacterById(Long userId, Long characterId) {
        Character character = characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found"));
        return characterMapper.toResponse(character);
    }

    @Override
    @Transactional
    public CharacterResponse updateCharacter(Long userId, Long characterId, CharacterRequest request) {
        Character character = characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found"));

        characterMapper.updateEntity(character, request);
        if (request.attributes() != null) {
            character.setAttributes(copyAttributes(request.attributes()));
        }
        applyCalculatedValues(character);
        character.setUpdatedAt(Instant.now());
        Character saved = characterRepository.save(character);
        return characterMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCharacter(Long userId, Long characterId) {
        Character character = characterRepository.findByIdAndUserId(characterId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found"));
        characterRepository.delete(character);
    }

    private HashMap<String, Object> copyAttributes(java.util.Map<String, Object> attributes) {
        // Keep request data separate from the entity's mutable attributes map.
        return attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    private void applyCalculatedValues(Character character) {
        Map<String, Object> attributes = character.getAttributes();
        CharacterCalculationResponse calculation = rulesetService.calculate(new CharacterCalculationRequest(
                stringValue(attributes, "rulesetKey"),
                mapValue(attributes, "rulesetSchema"),
                mapValue(attributes, "values"),
                character.getLevel(),
                integerValue(attributes, "hitDie")
        ));
        if (calculation == null) return;
        if (calculation.calculatedMaxHp() == null) return;

        character.setMaxHp(calculation.calculatedMaxHp());
        if (Boolean.TRUE.equals(attributes.get("autoCalculateHp"))) {
            character.setCurrentHp(calculation.calculatedMaxHp());
        } else if (character.getCurrentHp() == null) {
            character.setCurrentHp(calculation.calculatedMaxHp());
        }
        attributes.put("derivedStats", calculation.derivedStats());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapValue(Map<String, Object> attributes, String key) {
        return attributes.get(key) instanceof Map<?, ?> value ? (Map<String, Object>) value : null;
    }

    private String stringValue(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : String.valueOf(attributes.get(key));
    }

    private Integer integerValue(Map<String, Object> attributes, String key) {
        return attributes.get(key) instanceof Number value ? value.intValue() : null;
    }
}
