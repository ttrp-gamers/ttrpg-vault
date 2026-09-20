package com.ttrp.manager.dto.register;

public record RegisterResponse(
        Long id,
        String username,
        String email
) {
}
