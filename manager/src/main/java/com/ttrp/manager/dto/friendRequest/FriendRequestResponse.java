package com.ttrp.manager.dto.friendRequest;

import com.ttrp.manager.entity.type.FriendRequestStatus;

import java.time.Instant;

public record FriendRequestResponse(
        Long id,
        Long senderId,
        String senderUsername,
        Long receiverId,
        String receiverUsername,
        FriendRequestStatus status,
        Instant createdAt,
        Instant respondedAt
) {}
