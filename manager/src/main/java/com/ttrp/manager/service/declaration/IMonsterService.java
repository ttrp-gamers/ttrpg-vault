package com.ttrp.manager.service.declaration;

import com.ttrp.manager.dto.monster.MonsterRequest;
import com.ttrp.manager.dto.monster.MonsterResponse;

import java.util.List;

public interface IMonsterService {
    MonsterResponse createMonster(Long userId, MonsterRequest request);
    List<MonsterResponse> getMonstersByUser(Long userId);
    MonsterResponse updateMonster(Long userId, Long monsterId, MonsterRequest request);
    void deleteMonster(Long userId, Long monsterId);
}
