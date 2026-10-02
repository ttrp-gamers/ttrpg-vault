package com.ttrp.manager.dto.friendRequest;

import jakarta.validation.constraints.NotNull;

public record FriendRequestRequest(
        @NotNull Long receiverId
) {}
