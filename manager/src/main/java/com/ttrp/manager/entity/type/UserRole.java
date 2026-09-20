package com.ttrp.manager.entity.type;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN,
    USER,
    TEMP_USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
