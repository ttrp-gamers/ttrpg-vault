package com.ttrp.manager.dto.monster;

import java.time.Instant;
import java.util.Map;

public record MonsterResponse(
        Long id,
        Long userId,
        String name,
        String version,
        String source,
        String monsterType,
        String challengeRating,
        Integer maxHp,
        Integer currentHp,
        Map<String, Object> statblock,
        Instant createdAt,
        Instant updatedAt
) {}
