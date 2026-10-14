package com.ttrp.manager.dto.character;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record CharacterRequest(
        @NotBlank(message = "Character name is required")
        String name,
        String characterClass,
        @NotNull(message = "Level is required")
        @Min(value = 1, message = "Level must be at least 1")
        Integer level,
        String ruleset,
        @NotNull(message = "Maximum HP is required")
        @Min(value = 1, message = "Maximum HP must be at least 1")
        Integer maxHp,
        @NotNull(message = "Current HP is required")
        @Min(value = 0, message = "Current HP cannot be negative")
        Integer currentHp,
        Map<String, Object> attributes
) {
    // Cross-field validation is needed because maxHp and currentHp are valid individually.
    @AssertTrue(message = "Current HP cannot exceed maximum HP")
    public boolean isCurrentHpWithinMaximum() {
        return currentHp == null || maxHp == null || currentHp <= maxHp;
    }
}
