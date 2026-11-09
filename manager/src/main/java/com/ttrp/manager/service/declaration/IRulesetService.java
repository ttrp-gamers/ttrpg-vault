package com.ttrp.manager.service.declaration;

import com.ttrp.manager.dto.character.CharacterCalculationRequest;
import com.ttrp.manager.dto.character.CharacterCalculationResponse;

import java.util.List;
import java.util.Map;

public interface IRulesetService {
    List<Map<String, Object>> getRulesets();
    CharacterCalculationResponse calculate(CharacterCalculationRequest request);
}
