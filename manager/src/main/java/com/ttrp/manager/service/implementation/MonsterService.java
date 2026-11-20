package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.monster.MonsterRequest;
import com.ttrp.manager.dto.monster.MonsterResponse;
import com.ttrp.manager.entity.Monster;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.repository.MonsterRepository;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.service.declaration.IMonsterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonsterService implements IMonsterService {
    private final MonsterRepository monsterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MonsterResponse createMonster(Long userId, MonsterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Monster monster = new Monster();
        monster.setUser(user);
        apply(monster, request);
        Instant now = Instant.now();
        monster.setCreatedAt(now);
        monster.setUpdatedAt(now);
        return toResponse(monsterRepository.save(monster));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonsterResponse> getMonstersByUser(Long userId) {
        return monsterRepository.findAllByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MonsterResponse updateMonster(Long userId, Long monsterId, MonsterRequest request) {
        Monster monster = monsterRepository.findByIdAndUserId(monsterId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monster not found"));
        apply(monster, request);
        monster.setUpdatedAt(Instant.now());
        return toResponse(monsterRepository.save(monster));
    }

    @Override
    @Transactional
    public void deleteMonster(Long userId, Long monsterId) {
        Monster monster = monsterRepository.findByIdAndUserId(monsterId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monster not found"));
        monsterRepository.delete(monster);
    }

    private void apply(Monster monster, MonsterRequest request) {
        monster.setName(request.name().trim());
        monster.setVersion(request.version() == null ? "2014" : request.version());
        monster.setSource(request.source());
        monster.setMonsterType(request.monsterType());
        monster.setChallengeRating(request.challengeRating());
        monster.setMaxHp(request.maxHp());
        monster.setCurrentHp(request.currentHp());
        monster.setStatblock(request.statblock() == null ? new HashMap<>() : new HashMap<>(request.statblock()));
    }

    private MonsterResponse toResponse(Monster monster) {
        return new MonsterResponse(monster.getId(), monster.getUser().getId(), monster.getName(),
                monster.getVersion(), monster.getSource(), monster.getMonsterType(),
                monster.getChallengeRating(), monster.getMaxHp(), monster.getCurrentHp(),
                monster.getStatblock(), monster.getCreatedAt(), monster.getUpdatedAt());
    }
}
