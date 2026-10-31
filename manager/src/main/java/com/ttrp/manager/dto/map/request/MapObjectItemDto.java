package com.ttrp.manager.dto.map.request;

public record MapObjectItemDto(
        Long assetId,
        Integer gridX,
        Integer gridY,
        Integer widthInCells,
        Integer heightInCells
) {}