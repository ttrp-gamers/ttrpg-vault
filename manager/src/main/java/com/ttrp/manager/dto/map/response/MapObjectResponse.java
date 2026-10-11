package com.ttrp.manager.dto.map.response;

import jakarta.validation.constraints.NotBlank;

public record MapObjectResponse (
        @NotBlank
        Long mapId,
        @NotBlank
        String assetPath,
        @NotBlank
        Integer gridX,
        @NotBlank
        Integer gridY,
        @NotBlank
        Integer width,
        @NotBlank
        Integer height
){}



