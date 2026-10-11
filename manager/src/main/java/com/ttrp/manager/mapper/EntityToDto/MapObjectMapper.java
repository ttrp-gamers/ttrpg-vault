package com.ttrp.manager.mapper.EntityToDto;

import com.ttrp.manager.dto.map.response.MapObjectResponse;
import com.ttrp.manager.entity.map.MapObject;
import org.springframework.stereotype.Component;

@Component
public class MapObjectMapper {
    public MapObjectResponse toMapObjectResponse(MapObject mapObj){
       // return new MapObjectResponse(mapObj.getMapId(), mapObj.getAssetPath(), mapObj.getGridX(), mapObj.getGridY(), mapObj.getWidth(), mapObj.getHeight());
        throw new NullPointerException();
        }
}
