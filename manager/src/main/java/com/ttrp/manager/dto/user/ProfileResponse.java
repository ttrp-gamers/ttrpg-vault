package com.ttrp.manager.dto.user;

public record ProfileResponse(
        Long id,
        String username,
        String email,
        String avatarUrl,
        String bio
) {}