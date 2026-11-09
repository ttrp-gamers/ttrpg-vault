package com.ttrp.manager.controller;

import com.ttrp.manager.dto.character.CharacterCalculationRequest;
import com.ttrp.manager.dto.character.CharacterCalculationResponse;
import com.ttrp.manager.service.declaration.IRulesetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rulesets")
@RequiredArgsConstructor
public class RulesetController {
    private final IRulesetService rulesetService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listRulesets() {
        return ResponseEntity.ok(rulesetService.getRulesets());
    }

    @PostMapping("/calculate")
    public ResponseEntity<CharacterCalculationResponse> calculate(
            @RequestBody CharacterCalculationRequest request
    ) {
        return ResponseEntity.ok(rulesetService.calculate(request));
    }
}
