package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.map.request.MapObjectItemDto;
import com.ttrp.manager.dto.map.request.MapObjectRequest;
import com.ttrp.manager.dto.map.request.MapRequest;
import com.ttrp.manager.dto.map.response.MapStateResponse;
import com.ttrp.manager.dto.map.response.MapSummaryResponse;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.map.Asset;
import com.ttrp.manager.entity.map.Map;
import com.ttrp.manager.entity.map.MapObject;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.mapper.EntityToDto.MapObjectMapper;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.repository.map.AssetRepository;
import com.ttrp.manager.repository.map.MapObjectRepository;
import com.ttrp.manager.repository.map.MapRepository;
import com.ttrp.manager.service.declaration.IMapService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MapService implements IMapService {
    private final MapObjectRepository mapObjectRepository;
    private final UserRepository userRepository;
    private final MapRepository mapRepository;
    private final AssetRepository assetRepository;
    private final MapObjectMapper mapObjectMapper;
    @Value("${app.storage.location:./storage}")
    private String storageLocation;

    @Override
    public void createMap(MapRequest mapRequest, UserIdentity activeUser) {
        User user = userRepository.findById(activeUser.id())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found - ID: " + activeUser.id())
                );
        String fileName = mapRequest.backgroundName().trim();
        Path physicalPath = Paths.get(storageLocation, "maps", fileName);
        if (!Files.exists(physicalPath) || Files.isDirectory(physicalPath)) {
            throw new IllegalArgumentException("Background image '" + fileName + "' does not exist in storage/maps.");
        }
        String webBackgroundPath = "/files/maps/" + fileName;
        Map map = Map.builder()
                .name(mapRequest.mapName())
                .backgroundPath(webBackgroundPath)
                .owner(user)
                .build();
        this.mapRepository.save(map);
    }

    @Override
    @Transactional
    public List<MapSummaryResponse> getMaps(UserIdentity activeUser)
    {
        User user = userRepository.findById(activeUser.id())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found - ID: " + activeUser.id())
                );
        List<Map> userMaps = mapRepository.getMapsByOwner(user);
        return userMaps.stream()
                .map(map -> new MapSummaryResponse(
                        map.getId(),
                        map.getName(),
                        map.getBackgroundPath(),
                        map.getColumns(),
                        map.getRows(),
                        map.getGridSize()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void saveMapObjectsState(MapObjectRequest mapObjectReq, UserIdentity userIdentity){

        Map map = mapRepository.findById(mapObjectReq.mapId())
                .orElseThrow(() -> new RuntimeException("Map not found with id: " + mapObjectReq.mapId()));
        if (!map.getOwner().getId().equals(userIdentity.id())) {
            throw new SecurityException("User is not authorized to edit this map.");
        }
        mapObjectRepository.deleteByMapId(map.getId());
        List<MapObject> mapObjectsToSave = new ArrayList<>();
        // Should be by name not asset id
        for(MapObjectItemDto item : mapObjectReq.mapObjects()){
            Asset asset = assetRepository.findById(item.assetId())
                    .orElseThrow(() -> new RuntimeException("Asset not found with id: " + item.assetId()));
            MapObject mapObject = MapObject.builder()
                    .map(map)
                    .asset(asset)
                    .gridX(item.gridX())
                    .gridY(item.gridY())
                    .build();

            mapObjectsToSave.add(mapObject);
        }
        mapObjectRepository.saveAll(mapObjectsToSave);

    }

    @Override
    public Optional<MapStateResponse> retrieveMapState(String mapName, UserIdentity activeUser) {
        return mapRepository.findByNameAndOwnerId(mapName, activeUser.id()).map(mapObjectMapper::toMapStateResponse);
    }

    @Override
    public List<Asset> retrieveAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public void importMapBackground(MultipartFile background, String displayName, UserIdentity activeUser) {
        saveAssetFile(background, displayName,"maps" , "BACKGROUND", activeUser);
    }

    @Override
    public void importMapObject(MultipartFile background, String displayName, UserIdentity activeUser) {
        saveAssetFile(background, displayName,"assets" , "OBJECT", activeUser);
    }


    public void saveAssetFile(MultipartFile background, String displayName, String subDir, String category, UserIdentity activeUser) {
        String contentType = background.getContentType();
        String fileName = background.getName();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files (PNG, JPG, WebP) are allowed.");
        }

        Path targetLocation = Paths.get(storageLocation, subDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(targetLocation);

            Path targetFilePath = targetLocation.resolve(fileName);

            try (InputStream inputStream = background.getInputStream()) {
                Files.copy(inputStream, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            String webPath = "/files/" + subDir + fileName;
            Asset asset = Asset.builder()
                    .name(displayName)
                    .assetPath(webPath)
                    .category(category)
                    .build();
            this.assetRepository.save(asset);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store background image file.", e);
        }
    }
}
