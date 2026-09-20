package com.ttrp.manager.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserDeleteRequest(
        @NotNull String AccountPassword
) {}
