package com.ttrp.manager.service.declaration;

import com.ttrp.manager.dto.login.LoginRequest;
import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.register.RegisterRequest;
import com.ttrp.manager.dto.register.RegisterResponse;
import com.ttrp.manager.dto.user.UserDTO;

import java.util.Optional;

public interface IAuthService {

    public LoginResponse loginUser(LoginRequest loginCredentials);
    public UserDTO registerNewUser(RegisterRequest registerCredentials);

}
