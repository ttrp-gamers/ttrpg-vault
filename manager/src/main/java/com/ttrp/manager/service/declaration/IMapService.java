package com.ttrp.manager.service.declaration;
import com.ttrp.manager.dto.map.request.MapObjectRequest;
import com.ttrp.manager.dto.map.request.MapRequest;
import com.ttrp.manager.dto.map.response.MapObjectResponse;
import com.ttrp.manager.dto.map.response.MapStateResponse;
import com.ttrp.manager.dto.map.response.MapSummaryResponse;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.map.Asset;
import com.ttrp.manager.helper.authentication.UserIdentity;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;

public interface IMapService {
    void createMap(MapRequest mapRequest, UserIdentity activeUser);
    List<MapSummaryResponse> getMaps(UserIdentity activeUser);
    void saveMapObjectsState(MapObjectRequest mapObject, UserIdentity activeUser);
    Optional<MapStateResponse> retrieveMapState(String mapName, UserIdentity activeUser);
    List<Asset> retrieveAllAssets();
    void importMapBackground(MultipartFile background, String displayName, UserIdentity activeUser);
    void importMapObject(MultipartFile background, String displayName, UserIdentity activeUser);
}
