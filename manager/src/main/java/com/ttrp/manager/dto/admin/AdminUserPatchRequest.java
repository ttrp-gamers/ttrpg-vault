package com.ttrp.manager.dto.admin;

import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;

public record AdminUserPatchRequest(
        AccountStatus accountStatus,
        UserRole userRole
) {}
