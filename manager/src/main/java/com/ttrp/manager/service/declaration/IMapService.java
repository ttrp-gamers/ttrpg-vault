package com.ttrp.manager.service.declaration;
import com.ttrp.manager.dto.map.MapObjectRequest;
import com.ttrp.manager.dto.map.MapObjectResponse;


import java.util.Optional;

public interface IMapService {
    Boolean saveMapState(MapObjectRequest mapObject);
    Optional<MapObjectResponse> retrieveMapState(MapObjectRequest mapObject);

}
