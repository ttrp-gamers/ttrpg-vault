package com.ttrp.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        String jwtSecret,
        long jwtAccessExpirationMs,
        long jwtRefreshExpirationMs,
        List<String> corsAllowedOrigins
) {}
