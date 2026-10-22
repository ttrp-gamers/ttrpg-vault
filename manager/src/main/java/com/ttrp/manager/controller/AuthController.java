package com.ttrp.manager.controller;

import com.ttrp.manager.dto.login.LoginRequest;
import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.register.RegisterRequest;

import com.ttrp.manager.dto.register.RegisterResponse;
import com.ttrp.manager.dto.tokenRefresh.RefreshTokenRequest;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.service.implementation.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> requestLogin(
            @Valid @RequestBody LoginRequest loginCredentials
    ){
        LoginResponse loginResponse = authService.loginUser(loginCredentials);
        return ResponseEntity.ok().body(loginResponse);
    }


    @PostMapping("/register")
    public ResponseEntity<UserDTO> requestRegistration(
            @Valid @RequestBody RegisterRequest registerCredentials
    ){
        UserDTO registerResponse = authService.registerNewUser(registerCredentials);
        return ResponseEntity.ok().body(registerResponse);
    }


    @PostMapping("/token-refresh")
    public ResponseEntity<?> requestTokenRefresh(
            @Valid @RequestBody RefreshTokenRequest refreshToken
    ){
        Optional<LoginResponse> refreshTokenResponse = authService.refreshJwt(refreshToken);

        return refreshTokenResponse.isPresent() ?
                ResponseEntity.ok().body(refreshTokenResponse)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logged out");
    }



}
