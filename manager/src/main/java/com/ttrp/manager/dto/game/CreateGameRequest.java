package com.ttrp.manager.dto.game;

import jakarta.validation.constraints.NotBlank;

public record CreateGameRequest(
        @NotBlank(message = "Game name is required")
        String name
) {
}
