package com.ttrp.manager.mapper;

import com.ttrp.manager.entity.User;
import com.ttrp.manager.helper.authentication.UserIdentity;
import org.springframework.stereotype.Component;


@Component
public class UserIdentityMapper {

    public UserIdentity toUserIdentity(User user){
        return new UserIdentity(
                user.getId(),
                user.getEmail(),
                user.getAccountStatus(),
                user.getAuthorities());
    }
}
