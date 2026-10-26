package com.ttrp.manager.dto.friendRequest;

import com.ttrp.manager.entity.type.PlayerStatus;

import java.time.Instant;

public record FriendshipResponse(
        Long friendshipId,
        Long friendUserId,
        String friendUsername,
        PlayerStatus friendStatus,
        Instant createdAt
) {}
