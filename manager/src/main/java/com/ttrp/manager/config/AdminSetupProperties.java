package com.ttrp.manager.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminSetupProperties (
        String adminUsername,
        String adminPassword,
        String adminEmail
){}
