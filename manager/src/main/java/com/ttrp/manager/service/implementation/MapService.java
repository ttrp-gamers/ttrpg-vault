package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.map.MapObjectRequest;
import com.ttrp.manager.dto.map.MapObjectResponse;
import com.ttrp.manager.entity.MapObjects;
import com.ttrp.manager.mapper.EntityToDto.MapObjectMapObjectResponseMapper;
import com.ttrp.manager.repository.MapRepository;
import com.ttrp.manager.service.declaration.IMapService;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MapService implements IMapService {
    private final MapRepository mapRepository;
    private final MapObjectMapObjectResponseMapper mapObjectMapObjectResponseMapper;


    public MapService(MapRepository mapRepository, MapObjectMapObjectResponseMapper mapObjectMapObjectResponseMapper){
        this.mapRepository = mapRepository;
        this.mapObjectMapObjectResponseMapper = mapObjectMapObjectResponseMapper;
    }
    public Boolean saveMapState(MapObjectRequest mapObjectReq){
        MapObjects mapObject = MapObjects.builder().mapId(mapObjectReq.mapId()).assetPath(mapObjectReq.assetPath()).gridX(mapObjectReq.gridX()).gridY(mapObjectReq.gridY()).width(mapObjectReq.gridY()).height(mapObjectReq.height()).build();
        this.mapRepository.save(mapObject);
        return true;
    }
    public Optional<MapObjectResponse> retrieveMapState(MapObjectRequest mapObject) {
        MapObjects entity = (MapObjects) this.mapRepository.findFirstByMapId(mapObject.mapId());

        return Optional.ofNullable(entity)
                .map(mapObjectMapObjectResponseMapper::toMapObjectResponse);
    }
}
