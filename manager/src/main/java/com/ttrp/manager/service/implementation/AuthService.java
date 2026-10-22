package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.login.LoginRequest;
import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.register.RegisterRequest;
import com.ttrp.manager.dto.register.RegisterResponse;
import com.ttrp.manager.dto.tokenRefresh.RefreshTokenRequest;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.entity.RefreshToken;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.exception.LoginFailedException;
import com.ttrp.manager.exception.UserCredentialsAlreadyInUseException;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.mapper.EntityToDto.LoginResponseMapper;
import com.ttrp.manager.mapper.UserIdentityMapper;
import com.ttrp.manager.mapper.UserMapper;
import com.ttrp.manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;



import java.util.Optional;


@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final LoginResponseMapper loginResponseMapper;
    private final UserIdentityMapper userIdentityMapper;
    private final UserMapper userMapper;


    @Transactional
    public UserDTO registerNewUser(
            @Valid RegisterRequest registerCredentials
    ) {

        boolean existsUsername = userRepository.existsByUsername(registerCredentials.username());
        boolean existsEmail = userRepository.existsByEmail(registerCredentials.email());

        if(existsUsername || existsEmail){
            throw new UserCredentialsAlreadyInUseException(
                "Username or Email already in use", existsUsername, existsEmail);
        }

        UserRole requested = registerCredentials.role();
        UserRole role = (requested == UserRole.USER) ? UserRole.USER : UserRole.PLAYER;

        User newUser = User.builder()
                .username(registerCredentials.username())
                .email(registerCredentials.email())
                .password(passwordEncoder.encode(registerCredentials.password()))
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponse(savedUser);

    }

    @Transactional
    public LoginResponse loginUser(
            @Valid LoginRequest loginCredentials
    ) {
        User user = userRepository.findByEmail(loginCredentials.email())
                .filter(u -> passwordEncoder.matches(loginCredentials.password(), u.getPassword()))
                .filter(User::isEnabled)
                .orElseThrow(() -> new LoginFailedException("Usercredentials invalid"));

        RefreshToken refreshToken = sessionService.createRefreshToken(user.getId());
        UserIdentity identity = userIdentityMapper.toUserIdentity(user);
        String jwt = jwtService.generateToken(identity, refreshToken.getId());
        return loginResponseMapper.toLoginResponse(user, jwt, refreshToken.getToken());
    }


    @Transactional
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
