package com.ttrp.manager.dto.map.response;


public record MapSummaryResponse(
        Long id,
        String name,
        String backgroundPath,
        Integer columns,
        Integer rows,
        Integer gridSize
) {}
