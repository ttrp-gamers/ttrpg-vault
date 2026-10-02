package com.ttrp.manager.mapper.EntityToDto;

import com.ttrp.manager.dto.map.MapObjectResponse;
import com.ttrp.manager.entity.MapObjects;
import org.springframework.stereotype.Component;

@Component
public class MapObjectMapObjectResponseMapper {
    public MapObjectResponse toMapObjectResponse(MapObjects mapObj){
        return new MapObjectResponse(mapObj.getMapId(), mapObj.getAssetPath(), mapObj.getGridX(), mapObj.getGridY(), mapObj.getWidth(), mapObj.getHeight());
    }
}
