package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.user.ProfileResponse;
import com.ttrp.manager.dto.user.UpdateProfileRequest;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SessionService sessionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
    }

    public ProfileResponse getProfile(Long userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findUser(userId);

        if (req.username() != null && !req.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.username())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
            }
            user.setUsername(req.username());
        }
        if (req.avatarUrl() != null) user.setAvatarUrl(req.avatarUrl());
        if (req.bio() != null) user.setBio(req.bio());

        if (req.newPassword() != null) {
            if (req.currentPassword() == null
                    || !passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(req.newPassword()));
            sessionService.revokeAllUserToken(userId);
        }

        return toResponse(userRepository.save(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private ProfileResponse toResponse(User u) {
        return new ProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getAvatarUrl(), u.getBio());
    }
}