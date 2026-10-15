package com.ttrp.manager.dto.map.response;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MapStateResponse(
        @NotBlank
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String backgroundPath,
        Integer columns,
        Integer rows,
        Integer gridSize,
        List<MapObjectResponse> objects
) {}