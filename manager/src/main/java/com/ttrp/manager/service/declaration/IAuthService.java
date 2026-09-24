package com.ttrp.manager.service.declaration;

import com.ttrp.manager.dto.login.LoginRequest;
import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.register.RegisterRequest;
import com.ttrp.manager.dto.register.RegisterResponse;

import java.util.Optional;

public interface IAuthService {

    public Optional<LoginResponse> loginUser(LoginRequest loginCredentials);
    public Optional<RegisterResponse> registerNewUser(RegisterRequest registerCredentials);

}
