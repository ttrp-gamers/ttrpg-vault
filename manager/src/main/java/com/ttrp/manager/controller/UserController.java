package com.ttrp.manager.controller;


import com.ttrp.manager.dto.tokenRefresh.RefreshTokenRequest;
import com.ttrp.manager.dto.user.UserDeleteRequest;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.implementation.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ttrp.manager.dto.user.ProfileResponse;
import com.ttrp.manager.dto.user.UpdateProfileRequest;
import com.ttrp.manager.service.implementation.UserService;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
@IsRegisteredUser
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> requestOwnProfile(
            @CurrentUser UserIdentity activeUser
    ){
        return ResponseEntity.ok(userService.getProfile(activeUser.id()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> requestProfileUpdate(
            @CurrentUser UserIdentity activeUser,
            @Valid @RequestBody UpdateProfileRequest request
    ){
        return ResponseEntity.ok(userService.updateProfile(activeUser.id(), request));
    }

    @DeleteMapping("/delete-me")
    public ResponseEntity<?> requestAccountDeletion(
            @CurrentUser UserIdentity activeUser,
            @Valid @RequestBody UserDeleteRequest deleteAuthorisation
    ){
        // Possible Delete-Workflow:
        // This requires the user to enter a password to delete the account

        return ResponseEntity.ok().body("Profile deleted");
    }


    @PostMapping("/logout")
    public ResponseEntity<?> requestLogout(
            @CurrentUser UserIdentity activeUser,
            @Valid @RequestBody RefreshTokenRequest logOutToken
    ){
        return authService.logoutUser(activeUser, logOutToken.refreshToken()) ?
                ResponseEntity.ok().body("Successfully logged out")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logged out");
    }




}
