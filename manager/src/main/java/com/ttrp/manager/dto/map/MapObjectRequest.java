package com.ttrp.manager.dto.map;
import jakarta.validation.constraints.NotBlank;

public record MapObjectRequest (
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



