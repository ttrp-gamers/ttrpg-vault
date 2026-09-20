package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.register.RegisterRequest;
import com.ttrp.manager.dto.register.RegisterResponse;
import com.ttrp.manager.entity.RefreshToken;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.mapper.EntityToDto.LoginResponseMapper;
import com.ttrp.manager.mapper.UserIdentityMapper;
import com.ttrp.manager.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionService sessionService;

    @Mock
    private JwtService jwtService;

    private final UserIdentityMapper userIdentityMapper = new UserIdentityMapper();
    private final LoginResponseMapper loginResponseMapper = new LoginResponseMapper();

    private AuthService authService;

    private User testUser;
    private UserIdentity testIdentity;
    private RefreshToken testRefreshToken;


    @BeforeEach
    void setUp(){
        authService  = new AuthService(
                userRepository,
                passwordEncoder,
                sessionService,
                jwtService,
                loginResponseMapper,
                userIdentityMapper
        );

        testUser = new User(
                1L,
                "testuser",
                "test@test.com",
                "encoded_password",
                UserRole.USER,
                AccountStatus.ACTIVE
        );

        testIdentity = new UserIdentity(
                1L,
                "test@test.com",
                AccountStatus.ACTIVE,
                Collections.emptyList()
        );

        testRefreshToken = new RefreshToken(
                100L,
                "test_refresh_token",
                Instant.now().plusMillis(200000),
                false,
                testUser
        );
    }


    @Nested
    @DisplayName("Registration Tests")
    class RegisterTests{

        @Test
        void registerSuccess(){
            RegisterRequest registerRequest = new RegisterRequest(
                    "testuser",
                    "test@test.com",
                    "plain-text-password"
            );

            RegisterResponse expectedResponse = new RegisterResponse(
                    1L,
                    "testuser",
                    "test@test.com"
            );

            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.password())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User saveUser = invocation.getArgument(0);
                return User.builder()
                        .id(1L)
                        .username(saveUser.getUsername())
                        .email(saveUser.getEmail())
                        .password(saveUser.getPassword())
                        .userRole(saveUser.getUserRole())
                        .accountStatus(saveUser.getAccountStatus())
                        .build();
            });

            Optional<RegisterResponse> response = authService.registerNewUser(registerRequest);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo(testUser.getPassword());

            assertThat(response).isPresent().contains(expectedResponse);

        }



    }


    @Nested
    @DisplayName("Login Tests")
    class LoginTests{





    }




    @Nested
    @DisplayName("Registration Tests")
    class RefreshTokenTests{





    }






}