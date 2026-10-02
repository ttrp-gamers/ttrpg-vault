package com.ttrp.manager.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 3, max = 30) String username,
        @Size(max = 500) String avatarUrl,
        @Size(max = 500) String bio,
        String currentPassword,
        @Size(min = 6, max = 100) String newPassword
) {}