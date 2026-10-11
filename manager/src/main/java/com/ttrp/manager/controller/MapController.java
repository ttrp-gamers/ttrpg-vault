package com.ttrp.manager.controller;

import com.ttrp.manager.dto.map.request.MapObjectRequest;
import com.ttrp.manager.dto.map.request.MapRequest;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.implementation.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/map")
@IsRegisteredUser
public class MapController {
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }


    @GetMapping("/mapObject")
    public ResponseEntity<?> requestObjectsForMap(@RequestBody MapObjectRequest mapObjectRequest,
                                                                  @CurrentUser UserIdentity activeUser
    ){
        return ResponseEntity.ok(this.mapService.retrieveMapState(mapObjectRequest));
    }

    @PostMapping("/mapObject")
    public ResponseEntity<?> saveObjectsForMap( @RequestBody MapObjectRequest mapObjectRequest,
                                                   @CurrentUser UserIdentity activeUser
    ){
        this.mapService.saveMapState(mapObjectRequest);
        return ResponseEntity.ok("saved map");
    }

    @PostMapping("/map")
    public ResponseEntity<?> createMap(@RequestBody MapRequest mapRequest, @CurrentUser UserIdentity activeUser
    ){
        this.mapService.createMap(mapRequest, activeUser);
        return ResponseEntity.ok("created map");
    }
    @GetMapping("/map")
    public ResponseEntity<?> getMaps(@CurrentUser UserIdentity activeUser
    ){
        return ResponseEntity.ok(this.mapService.getMaps(activeUser));
    }

}
