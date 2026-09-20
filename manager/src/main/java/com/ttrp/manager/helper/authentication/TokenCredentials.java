package com.ttrp.manager.helper.authentication;

import jakarta.validation.constraints.NotNull;

public record TokenCredentials(
        @NotNull Long refreshTokenId,
        @NotNull String userEmail
) {}
