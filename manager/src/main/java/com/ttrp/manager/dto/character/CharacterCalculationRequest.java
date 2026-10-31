package com.ttrp.manager.dto.character;

import java.util.Map;

public record CharacterCalculationRequest(
        String rulesetKey,
        Map<String, Object> rulesetSchema,
        Map<String, Object> values,
        Integer level,
        Integer hitDie
) {}
