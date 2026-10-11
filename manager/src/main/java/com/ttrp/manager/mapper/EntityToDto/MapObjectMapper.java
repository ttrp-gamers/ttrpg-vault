package com.ttrp.manager.mapper.EntityToDto;

import com.ttrp.manager.dto.map.response.MapObjectResponse;
import com.ttrp.manager.dto.map.response.MapStateResponse;
import com.ttrp.manager.dto.map.response.MapSummaryResponse;
import com.ttrp.manager.entity.map.Map;
import com.ttrp.manager.entity.map.MapObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapObjectMapper {
    public MapStateResponse toMapStateResponse(Map map){
        List<MapObjectResponse> mapObjectResponseList = new ArrayList<>();
        for(MapObject mapObject : map.getObjects()){
            mapObjectResponseList.add(new MapObjectResponse(map.getId(),mapObject.getAsset().getAssetPath(), mapObject.getGridX(), mapObject.getGridY(), mapObject.getWidth(), mapObject.getHeight()));
        }
       return new MapStateResponse(map.getId(), map.getName(), map.getBackgroundPath(), map.getColumns(), map.getRows(), map.getGridSize(), mapObjectResponseList);
    }
}
