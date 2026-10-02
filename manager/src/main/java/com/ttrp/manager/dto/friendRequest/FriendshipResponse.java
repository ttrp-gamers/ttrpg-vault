package com.ttrp.manager.dto.friendRequest;

import java.time.Instant;

public record FriendshipResponse(
        Long friendshipId,
        Long friendUserId,
        String friendUsername,
        Instant createdAt
) {}
