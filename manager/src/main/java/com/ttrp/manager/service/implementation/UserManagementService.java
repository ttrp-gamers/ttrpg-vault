package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.admin.AdminUserPatchRequest;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.mapper.UserMapper;
import com.ttrp.manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;



    @Transactional(readOnly = true)
    public List<UserDTO> getUserList() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }


    @Transactional
    public void updateUser(Long userId, AdminUserPatchRequest patch){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found - ID: " + userId)
                );
        userMapper.patchUser(user, patch);
        userRepository.save(user);
    }




}
