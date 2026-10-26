package com.ttrp.manager.controller;

import com.ttrp.manager.dto.user.UserDeleteRequest;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class  UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserIdentity testIdentity;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .password("encodedPassword")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        testIdentity = new UserIdentity(
                1L,
                "test@email.com",
                AccountStatus.ACTIVE,
                Collections.emptyList()
        );
    }

    @Nested
    @DisplayName("Account Deactivation Tests")
    class AccountDeactivationTests {

        @Test
        void requestAccountDeletion_Success() { //-Jakub
            //Given
            UserDeleteRequest request = new UserDeleteRequest("password123");

            when(userRepository.findById(testIdentity.id())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(request.AccountPassword(), testUser.getPassword())).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            //When:
            ResponseEntity<?> response =userController.requestAccountDeletion(testIdentity, request);

            //Then:
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("Profile deleted successfully");
            assertThat(testUser.getAccountStatus()).isEqualTo(AccountStatus.DEACTIVATED);
            verify(userRepository).save(testUser);
        }

        @Test
        void requestAccountDeletion_wrongPassword() {
            //Given
            UserDeleteRequest request = new UserDeleteRequest("wrongPassword");

            when(userRepository.findById(testIdentity.id())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(request.AccountPassword(), testUser.getPassword())).thenReturn(false);

            //When
            ResponseEntity<?> response = userController.requestAccountDeletion(testIdentity, request);

            //Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isEqualTo("Wrong password.");
            assertThat(testUser.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void requestAccountDeletion_UserNotFound() {
            //Given
            UserDeleteRequest request = new UserDeleteRequest("password123");

            when(userRepository.findById(testIdentity.id())).thenReturn(Optional.empty());

            //When & then together
            assertThrows(UsernameNotFoundException.class, () ->
                    userController.requestAccountDeletion(testIdentity, request));
            verify(userRepository, never()).save(any(User.class));
        }
    }
}