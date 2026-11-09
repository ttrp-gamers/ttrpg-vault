package com.ttrp.manager.dto.character;

import java.util.Map;

public record CharacterCalculationResponse(
        Map<String, Object> derivedStats,
        Integer calculatedMaxHp
) {}
