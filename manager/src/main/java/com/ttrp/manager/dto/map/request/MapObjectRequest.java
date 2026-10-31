package com.ttrp.manager.dto.map.request;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MapObjectRequest (
    @NotBlank
    Long mapId,
    List<MapObjectItemDto> mapObjects
){}

