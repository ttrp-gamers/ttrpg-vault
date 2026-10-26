package com.ttrp.manager.controller;


import com.ttrp.manager.dto.tokenRefresh.RefreshTokenRequest;
import com.ttrp.manager.dto.user.UserDeleteRequest;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.service.implementation.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(AuthService authService,
                          UserService userService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) { //cleaned up the format -Jakub
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        User user = userRepository.findById(activeUser.id())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!passwordEncoder.matches(deleteAuthorisation.AccountPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password.");
        }
        //else:
        user.setAccountStatus(AccountStatus.DEACTIVATED);
        userRepository.save(user);

        return ResponseEntity.ok().body("Profile deleted successfully");
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
