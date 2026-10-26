package com.ttrp.manager.dto.user;

import com.ttrp.manager.entity.type.PlayerStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePlayerStatusRequest(
        @NotNull PlayerStatus playerStatus
) {
}
