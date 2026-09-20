package com.ttrp.manager.dto.user;

import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;

public record UserDTO(
        long id,
        String username,
        String email,
        UserRole role,
        AccountStatus accountStatus
) {}
