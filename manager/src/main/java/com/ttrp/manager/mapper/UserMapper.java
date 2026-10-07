package com.ttrp.manager.mapper;

import com.ttrp.manager.dto.admin.AdminUserPatchRequest;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.dto.user.UserSummary;
import com.ttrp.manager.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {


    @Mapping(target = "role", source = "userRole")
    UserDTO toResponse(User user);


    UserSummary toUserSummary(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUser(@MappingTarget User user, AdminUserPatchRequest patch);
}
