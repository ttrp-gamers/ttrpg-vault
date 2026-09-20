package com.ttrp.manager.mapper.EntityToDto;


import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseMapper {

    public LoginResponse toLoginResponse(User user, String jwt, String refreshToken){
        return new LoginResponse(
                jwt,
                refreshToken,
                new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getUserRole(),
                        user.getAccountStatus()));
    }



}
