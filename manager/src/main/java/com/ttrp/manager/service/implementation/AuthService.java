package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.login.LoginRequest;
import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.register.RegisterRequest;
import com.ttrp.manager.dto.register.RegisterResponse;
import com.ttrp.manager.dto.tokenRefresh.RefreshTokenRequest;
import com.ttrp.manager.entity.RefreshToken;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.mapper.EntityToDto.LoginResponseMapper;
import com.ttrp.manager.mapper.UserIdentityMapper;
import com.ttrp.manager.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;



import java.util.Optional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final LoginResponseMapper loginResponseMapper;
    private final UserIdentityMapper userIdentityMapper;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SessionService sessionService,
                       JwtService jwtService,
                       LoginResponseMapper loginResponseMapper,
                       UserIdentityMapper userIdentityMapper) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.jwtService = jwtService;
        this.loginResponseMapper = loginResponseMapper;
        this.userIdentityMapper = userIdentityMapper;
    }


    public Optional<RegisterResponse> registerNewUser(
            @Valid RegisterRequest registerCredentials
    ) {
        if(userRepository.existsByEmail(registerCredentials.email())){
            return Optional.empty();
        }

        User newUser = User.builder()
                .username(registerCredentials.username())
                .email(registerCredentials.email())
                .password(passwordEncoder.encode(registerCredentials.password()))
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);

        return Optional.of(new RegisterResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail()));
    }



    public Optional<LoginResponse> loginUser(
            @Valid LoginRequest loginCredentials
    ) {
        return userRepository.findByEmail(loginCredentials.email())
                .filter(user -> passwordEncoder.matches(loginCredentials.password(), user.getPassword()))
                .filter(User::isEnabled)
                .map(user -> {
                    RefreshToken refreshToken = sessionService.createRefreshToken(user.getId());
                    UserIdentity identity = userIdentityMapper.toUserIdentity(user);
                    String jwt = jwtService.generateToken(identity, refreshToken.getId());
                    return loginResponseMapper.toLoginResponse(user, jwt, refreshToken.getToken());

                });
    }


    public Optional<LoginResponse> refreshJwt(RefreshTokenRequest refreshToken){
            return sessionService.rotateRefreshToken(refreshToken.refreshToken())
                    .map(newToken -> {
                        User user = newToken.getUser();
                        UserIdentity uid = userIdentityMapper.toUserIdentity(user);
                        String jwt = jwtService.generateToken(uid, newToken.getId());
                        return loginResponseMapper.toLoginResponse(user, jwt, newToken.getToken());
                    });

    }

    public Boolean logoutUser(
            @CurrentUser UserIdentity activeUser,
            String refreshToken
    ) {
        sessionService.revokeRefreshToken(refreshToken);
        return true;
    }






}
