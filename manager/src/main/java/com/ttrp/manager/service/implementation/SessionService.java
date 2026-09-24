package com.ttrp.manager.service.implementation;


import com.ttrp.manager.config.SecurityProperties;
import com.ttrp.manager.entity.RefreshToken;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.repository.RefreshTokenRepository;
import com.ttrp.manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final SecurityProperties securityProperties;

    public SessionService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, SecurityProperties securityProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }


    @Transactional
    public Optional<UserIdentity> validateSessionViaJwtAndGetUser(
            Long refreshTokenId, String expectedUserEmail
    ){
        return refreshTokenRepository.findByIdWithUser(refreshTokenId)
                .filter(token -> !token.isExpired() && !token.isRevoked())
                .map(RefreshToken::getUser)
                .filter(user -> user.getEmail().equalsIgnoreCase(expectedUserEmail))
                .map(user -> new UserIdentity(
                        user.getId(),
                        user.getEmail(),
                        user.getAccountStatus(),
                        user.getAuthorities()
                ));
    }


    @Transactional
    public Optional<RefreshToken> rotateRefreshToken(
            String tokenString
    ){
        return refreshTokenRepository.findByToken(tokenString)
                .filter(token -> !token.isExpired() && !token.isRevoked())
                .map(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    return createRefreshToken(token.getUser().getId());
                });
    }


    @Transactional
    public RefreshToken createRefreshToken(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(securityProperties.jwtRefreshExpirationMs()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    @Transactional
    public void revokeRefreshToken(String tokenId) {
        refreshTokenRepository.findByToken(tokenId)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }


    @Transactional
    public void revokeAllUserToken(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }



}
