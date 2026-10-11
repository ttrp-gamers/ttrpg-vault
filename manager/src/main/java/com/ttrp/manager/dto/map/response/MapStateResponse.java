package com.ttrp.manager.dto.map.response;

import java.util.List;

public record MapStateResponse(
        Long id,
        String name,
        String backgroundPath,
        Integer columns,
        Integer rows,
        Integer gridSize,
        List<MapObjectResponse> objects
) {}