package com.ttrp.manager.controller;

import com.ttrp.manager.dto.map.MapObjectRequest;
import com.ttrp.manager.dto.map.MapObjectResponse;
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


    @GetMapping("/mepObject")
    public ResponseEntity<?> requestObjectsForMap(@RequestBody MapObjectRequest mapObjectRequest,
                                                                  @CurrentUser UserIdentity activeUser
    ){
        return ResponseEntity.ok(this.mapService.retrieveMapState(mapObjectRequest));
    }

    @PostMapping("/mepObject")
    public ResponseEntity<?> saveObjectsForMap( @RequestBody MapObjectRequest mapObjectRequest,
                                                   @CurrentUser UserIdentity activeUser
    ){
        this.mapService.saveMapState(mapObjectRequest);
        return ResponseEntity.ok("saved map");
    }
}
