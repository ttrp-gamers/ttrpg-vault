package com.ttrp.manager.controller;

import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.implementation.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friendships")
@IsRegisteredUser
@AllArgsConstructor
public class FriendshipController {
    private final FriendRequestService friendRequestService;

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriendship(@CurrentUser UserIdentity activeUser, @PathVariable Long friendId) {

        friendRequestService.removeFriend(activeUser.id(), friendId);
        return ResponseEntity.noContent().build();
    }

}
