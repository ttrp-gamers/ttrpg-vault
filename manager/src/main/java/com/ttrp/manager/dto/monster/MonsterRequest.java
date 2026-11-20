package com.ttrp.manager.dto.monster;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record MonsterRequest(
        @NotBlank(message = "Monster name is required")
        String name,
        String version,
        String source,
        String monsterType,
        String challengeRating,
        @NotNull(message = "Maximum HP is required")
        @Min(value = 1, message = "Maximum HP must be at least 1")
        Integer maxHp,
        @NotNull(message = "Current HP is required")
        @Min(value = 0, message = "Current HP cannot be negative")
        Integer currentHp,
        Map<String, Object> statblock
) {
    @AssertTrue(message = "Current HP cannot exceed maximum HP")
    public boolean isCurrentHpWithinMaximum() {
        return currentHp == null || maxHp == null || currentHp <= maxHp;
    }
}
