package com.ttrp.manager.controller;


import com.ttrp.manager.dto.admin.AdminUserPatchRequest;
import com.ttrp.manager.dto.login.LoginResponse;
import com.ttrp.manager.dto.user.UserDTO;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.helper.authentication.annotation.IsAdmin;
import com.ttrp.manager.service.implementation.UserManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@IsAdmin
@AllArgsConstructor
public class AdminController {

    private final UserManagementService userManagementService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUserList(){
        List<UserDTO> userList = userManagementService.getUserList();
        return ResponseEntity.ok().body(userList);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<Void> updateUserAccount(
            @PathVariable Long userId,
            @RequestBody AdminUserPatchRequest userPatch
    ){
        userManagementService.updateUser(userId, userPatch);
        return ResponseEntity.noContent().build();
    }



}
