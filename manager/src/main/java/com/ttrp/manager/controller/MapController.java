package com.ttrp.manager.controller;

import com.ttrp.manager.dto.map.request.MapObjectRequest;
import com.ttrp.manager.dto.map.request.MapRequest;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.declaration.IMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/map")
@IsRegisteredUser
public class MapController {
    private final IMapService mapService;

    public MapController(IMapService mapService) {
        this.mapService = mapService;
    }

    @PostMapping()
    public ResponseEntity<?> createMap(@RequestBody MapRequest mapRequest, @CurrentUser UserIdentity activeUser
    ){
        this.mapService.createMap(mapRequest, activeUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/backgrounds")
    public ResponseEntity<?> getBackgrounds(){
        return ResponseEntity.ok(this.mapService.getBackgrounds());
    }
    @GetMapping("/objects")
    public ResponseEntity<?> getObjects(){
        return ResponseEntity.ok(this.mapService.getObjects());
    }

    @GetMapping()
    public ResponseEntity<?> getMaps(@CurrentUser UserIdentity activeUser
    ){
        return ResponseEntity.ok(this.mapService.getMaps(activeUser));
    }

    @GetMapping("/state/{mapName}")
    public ResponseEntity<?> requestStateForMap(@CurrentUser UserIdentity activeUser, @PathVariable String mapName
    ){
        return mapService.retrieveMapState(mapName, activeUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/state")
    public ResponseEntity<?> saveStateForMap( @RequestBody MapObjectRequest mapObjectRequest,
                                                   @CurrentUser UserIdentity activeUser
    ){
        this.mapService.saveMapObjectsState(mapObjectRequest, activeUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/asset")
        public  ResponseEntity<?> getAllAssets(@CurrentUser UserIdentity activeUser){
        return ResponseEntity.ok(this.mapService.retrieveAllAssets());
    }

    @PostMapping("/import/bg")
    public ResponseEntity<?> importBackground(@CurrentUser UserIdentity activeUser, @RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "name", required = false) String name
    ){
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty.");
        }
        mapService.importMapBackground(file, name ,activeUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import/obj")
    public ResponseEntity<?> importObject(@CurrentUser UserIdentity activeUser, @RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "name", required = false) String name
    ){
        mapService.importMapObject(file, name ,activeUser);
        return ResponseEntity.noContent().build();
    }

}
