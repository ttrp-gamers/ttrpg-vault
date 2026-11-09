package com.ttrp.manager.dto.user;

import com.ttrp.manager.entity.type.PlayerStatus;

public record ProfileResponse(
        Long id,
        String username,
        String email,
        String avatarUrl,
        String bio,
        PlayerStatus playerStatus
) {}
