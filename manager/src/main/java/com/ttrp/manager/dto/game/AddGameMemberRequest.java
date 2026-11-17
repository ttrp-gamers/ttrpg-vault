package com.ttrp.manager.dto.game;

import jakarta.validation.constraints.NotNull;

public record AddGameMemberRequest(
        @NotNull(message = "User id is required")
        Long userId
) {
}
