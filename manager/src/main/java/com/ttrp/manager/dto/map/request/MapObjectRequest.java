package com.ttrp.manager.dto.map.request;
import com.ttrp.manager.entity.map.MapObject;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MapObjectRequest (
    @NotBlank
    Long mapId,
    List<MapObject> mapObjects
){}

