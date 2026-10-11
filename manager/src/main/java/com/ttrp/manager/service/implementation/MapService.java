package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.map.request.MapObjectRequest;
import com.ttrp.manager.dto.map.request.MapRequest;
import com.ttrp.manager.dto.map.response.MapObjectResponse;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.map.Asset;
import com.ttrp.manager.entity.map.Map;
import com.ttrp.manager.entity.map.MapObject;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.mapper.EntityToDto.MapObjectMapper;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.repository.map.MapObjectRepository;
import com.ttrp.manager.repository.map.MapRepository;
import com.ttrp.manager.service.declaration.IMapService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class MapService implements IMapService {
    private final MapObjectRepository mapObjectRepository;
    private final MapRepository mapRepository;
    private final UserRepository userRepository;
    private final MapObjectMapper mapObjectMapObjectResponseMapper;


    public MapService(MapObjectRepository mapObjectRepository, MapRepository mapRepository, UserRepository userRepository, MapObjectMapper mapObjectMapObjectResponseMapper){
        this.mapObjectRepository = mapObjectRepository;
        this.mapRepository = mapRepository;
        this.userRepository = userRepository;
        this.mapObjectMapObjectResponseMapper = mapObjectMapObjectResponseMapper;
    }

    @Override
    public void createMap(MapRequest mapRequest, UserIdentity activeUser) {
        User user = userRepository.findById(activeUser.id())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found - ID: " + activeUser.id())
                );

        Map map = Map.builder().name(mapRequest.name()).backgroundPath(null).owner(user).build();
        this.mapRepository.save(map);
    }

    @Override
    public List<Map> getMaps(UserIdentity activeUser)
    {
        User user = this.userRepository.getReferenceById(activeUser.id());
        return this.mapRepository.getMapsByOwner(user);
    }

    @Override
    public Boolean saveMapObjectsState(MapObjectRequest mapObjectReq, UserIdentity userIdentity){
        Map map = this.mapRepository.getReferenceById(mapObjectReq.mapId());
        User user = this.userRepository.getReferenceById();
        for (int i = 0; i < mapObjectReq.mapObjects().size(); i ++){
            Asset asset = Asset.builder().owner().assetPath().build()
            MapObject mapObject = MapObject.builder().map(map).asset().gridX().gridY().width().height().build();
        }
        //MapObjects mapObject = MapObjects.builder().mapId(mapObjectReq.mapId()).assetPath(mapObjectReq.assetPath()).gridX(mapObjectReq.gridX()).gridY(mapObjectReq.gridY()).width(mapObjectReq.gridY()).height(mapObjectReq.height()).build();
        //this.mapRepository.save(mapObject);
        return true;
    }

    @Override
    public Optional<MapObjectResponse> retrieveMapState(MapObjectRequest mapObject) {
        MapObject entity = (MapObject) this.mapObjectRepository.findFirstByMapId(mapObject.mapId());

        return Optional.ofNullable(entity)
                .map(mapObjectMapObjectResponseMapper::toMapObjectResponse);
    }
}
