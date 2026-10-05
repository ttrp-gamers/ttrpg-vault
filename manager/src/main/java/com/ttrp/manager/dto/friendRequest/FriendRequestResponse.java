package com.ttrp.manager.dto.friendRequest;

import com.ttrp.manager.dto.user.UserSummary;
import com.ttrp.manager.entity.type.FriendRequestStatus;

import java.time.Instant;

public record FriendRequestResponse(
        Long id,
        UserSummary sender,
        UserSummary receiver,
        FriendRequestStatus status,
        Instant createdAt,
        Instant respondedAt
) {}
