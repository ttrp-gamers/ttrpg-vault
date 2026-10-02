package com.ttrp.manager.controller;


import com.ttrp.manager.dto.friendRequest.FriendRequestRequest;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.implementation.FriendRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend-requests")
@IsRegisteredUser
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }


    @PostMapping
    public ResponseEntity<?> sendFriendRequest(
            @CurrentUser UserIdentity activeUser,
            @Valid @RequestBody FriendRequestRequest requestBody
    ) {
        var response = friendRequestService.sendFriendRequest(activeUser.id(), requestBody.receiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long id
    ) {
        return friendRequestService.acceptFriendRequest(id, activeUser.id())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found"));
    }


    @PostMapping("/{id}/deny")
    public ResponseEntity<?> denyFriendRequest(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long id
    ) {
        return friendRequestService.denyFriendRequest(id, activeUser.id())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelFriendRequest(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long id
    ) {
        return friendRequestService.cancelFriendRequest(id, activeUser.id())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found"));
    }


    @GetMapping("/incoming")
    public ResponseEntity<?> listIncoming(
            @CurrentUser UserIdentity activeUser
    ) {
        return ResponseEntity.ok(friendRequestService.listIncoming(activeUser.id()));
    }


    @GetMapping("/outgoing")
    public ResponseEntity<?> listOutgoing(
            @CurrentUser UserIdentity activeUser
    ) {
        return ResponseEntity.ok(friendRequestService.listOutgoing(activeUser.id()));
    }
}
