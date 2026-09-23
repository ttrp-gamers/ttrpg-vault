package com.ttrp.manager.service.implementation;

import com.ttrp.manager.config.SecurityProperties;
import com.ttrp.manager.helper.authentication.TokenCredentials;
import com.ttrp.manager.helper.authentication.UserIdentity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {


    private final SecurityProperties securityProperties;


    public JwtService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }


    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(securityProperties.jwtSecret()));
    }


    public Optional<TokenCredentials> extractIfValidToken(String token){
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(new TokenCredentials(claims.get("rti", Long.class), claims.getSubject()));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public String generateToken(UserIdentity user, Long refreshTokenID){

        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("rti", refreshTokenID);

        return Jwts.builder()
                .subject(user.email())
                .claims(tokenClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ securityProperties.jwtAccessExpirationMs()))
                .signWith(signingKey())
                .compact();
    }
}
