package com.ttrp.manager.service.declaration;
import com.ttrp.manager.dto.map.request.MapObjectRequest;
import com.ttrp.manager.dto.map.request.MapRequest;
import com.ttrp.manager.dto.map.response.MapObjectResponse;
import com.ttrp.manager.entity.map.Map;
import com.ttrp.manager.helper.authentication.UserIdentity;


import java.util.List;
import java.util.Optional;

public interface IMapService {
    void createMap(MapRequest mapRequest, UserIdentity activeUser);
    List<Map> getMaps(UserIdentity activeUser);
    Boolean saveMapObjectsState(MapObjectRequest mapObject, UserIdentity activeUser);
    Optional<MapObjectResponse> retrieveMapState(MapObjectRequest mapObject);

}
