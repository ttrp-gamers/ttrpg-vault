package com.ttrp.manager.dto.login;

import com.ttrp.manager.dto.user.UserDTO;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserDTO user
) {}
