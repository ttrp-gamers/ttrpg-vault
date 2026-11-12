package com.ttrp.manager.dto.map.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MapObjectResponse (
        @NotNull
        Long mapId,
        @NotNull
        Long assetId,
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



