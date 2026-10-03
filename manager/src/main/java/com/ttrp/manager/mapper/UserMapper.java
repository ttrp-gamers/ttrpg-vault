package com.ttrp.manager.mapper;


import com.ttrp.manager.dto.admin.AdminUserPatchRequest;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {


    UserDTO toResponse(User user);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUser(@MappingTarget User user, AdminUserPatchRequest patch);
}
