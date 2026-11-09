package com.ttrp.manager.controller;

import com.ttrp.manager.dto.user.UserDeleteRequest;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.service.implementation.AuthService;
import com.ttrp.manager.service.implementation.UserService;
import net.bytebuddy.implementation.ToStringMethod;
import org.apache.coyote.Response;
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
import org.springframework.security.authentication.BadCredentialsException;
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
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    private UserIdentity testIdentity;
    private final String refreshToken = "mock-refreshToken";

    @BeforeEach
    void setUp() {
        testIdentity = new UserIdentity(
                1L,
                "test@email.com",
                AccountStatus.ACTIVE,
                Collections.emptyList()
        );
    }

    /*@Nested
    @DisplayName("Account Deactivation Tests")
    class AccountDeactivationTests {

        @Test
        void requestAccountDeletion_Success() { //-Jakub
            //### ACT
            UserDeleteRequest request = new UserDeleteRequest("password123", refreshToken);

            when(userService.deactivateProfile(eq(testIdentity.id()), any(UserDeleteRequest.class)))
                    .thenReturn("Profile deleted successfully");
            when(authService.logoutUser(eq(testIdentity), eq(refreshToken)))
                    .thenReturn(true);

            //### ACT
            ResponseEntity<?> response = userController.requestAccountDeletion(testIdentity, request, refreshToken);

            //### ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("Profile deleted successfully");

            verify(userService).deactivateProfile(testIdentity.id(), request);;
            verify(authService).logoutUser(testIdentity, refreshToken);
        }

        @Test
        void requestAccountDeletion_wrongPassword() {
            //### ACT
            UserDeleteRequest request = new UserDeleteRequest("wrongPassword",  refreshToken);

            when(userService.deactivateProfile(eq(testIdentity.id()), any(UserDeleteRequest.class)))
                    .thenThrow(new BadCredentialsException("Wrong Password"));

            //### ACT & ASSERT
            assertThrows(BadCredentialsException.class, () ->
                    userController.requestAccountDeletion(testIdentity, request, refreshToken));

            verify(userService).deactivateProfile(testIdentity.id(), request);
            verify(authService, never()).logoutUser(any(), any());
        }

        @Test
        void requestAccountDeletion_UserNotFound() {
            //### ACT
            UserDeleteRequest request = new UserDeleteRequest("password123", refreshToken);

            when(userService.deactivateProfile(eq(testIdentity.id()), any(UserDeleteRequest.class)))
                    .thenThrow(new UsernameNotFoundException("User not found"));

            //### ACT & ASSERT
            assertThrows(UsernameNotFoundException.class, () ->
                    userController.requestAccountDeletion(testIdentity, request, refreshToken));

            verify(userService).deactivateProfile(testIdentity.id(), request);
            verify(authService, never()).logoutUser(any(), any());
        }
    }*/
}