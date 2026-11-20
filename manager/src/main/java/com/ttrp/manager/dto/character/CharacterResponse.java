package com.ttrp.manager.dto.character;

import java.time.Instant;
import java.util.Map;

public record CharacterResponse(
        Long id,
        Long userId,
        String name,
        String characterClass,
        String species,
        String background,
        String alignment,
        String notes,
        Integer level,
        String ruleset,
        Integer maxHp,
        Integer currentHp,
        Map<String, Object> attributes,
        Instant createdAt,
        Instant updatedAt
) {}
