package com.ttrp.manager.service.declaration;

import com.ttrp.manager.dto.character.CharacterRequest;
import com.ttrp.manager.dto.character.CharacterResponse;

import java.util.List;

public interface ICharacterService {
    CharacterResponse createCharacter(Long userId, CharacterRequest request);
    List<CharacterResponse> getCharactersByUser(Long userId);
    CharacterResponse getCharacterById(Long userId, Long characterId);
    CharacterResponse updateCharacter(Long userId, Long characterId, CharacterRequest request);
    void deleteCharacter(Long userId, Long characterId);
}

