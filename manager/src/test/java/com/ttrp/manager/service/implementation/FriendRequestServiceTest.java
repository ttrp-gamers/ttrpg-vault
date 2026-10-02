package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.friendRequest.FriendRequestResponse;
import com.ttrp.manager.dto.friendRequest.FriendshipResponse;
import com.ttrp.manager.entity.FriendRequest;
import com.ttrp.manager.entity.Friendship;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.FriendRequestStatus;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.exception.FriendRequestException;
import com.ttrp.manager.mapper.FriendRequestMapper;
import com.ttrp.manager.repository.FriendRequestRepository;
import com.ttrp.manager.repository.FriendshipRepository;
import com.ttrp.manager.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    private final FriendRequestMapper friendRequestMapper = new FriendRequestMapper();

    private FriendRequestService friendRequestService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        friendRequestService = new FriendRequestService(
                friendRequestRepository,
                friendshipRepository,
                userRepository,
                friendRequestMapper
        );

        sender = User.builder()
                .id(1L)
                .username("sender")
                .email("sender@test.com")
                .password("encoded_password")
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        receiver = User.builder()
                .id(2L)
                .username("receiver")
                .email("receiver@test.com")
                .password("encoded_password")
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }


    @Nested
    @DisplayName("Send Friend Request Tests")
    class SendFriendRequestTests {

        @Test
        void sendSuccess() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(friendshipRepository.existsBetween(1L, 2L)).thenReturn(false);
            when(friendRequestRepository.existsPendingBetween(1L, 2L)).thenReturn(false);
            when(friendRequestRepository.save(any(FriendRequest.class))).thenAnswer(invocation -> {
                FriendRequest toSave = invocation.getArgument(0);
                toSave.setId(10L);
                return toSave;
            });

            FriendRequestResponse response = friendRequestService.sendFriendRequest(1L, 2L);

            ArgumentCaptor<FriendRequest> captor = ArgumentCaptor.forClass(FriendRequest.class);
            verify(friendRequestRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(FriendRequestStatus.PENDING);

            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.senderId()).isEqualTo(1L);
            assertThat(response.receiverId()).isEqualTo(2L);
            assertThat(response.status()).isEqualTo(FriendRequestStatus.PENDING);
        }

        @Test
        void sendBlocksSelfRequest() {
            FriendRequestException ex = Assertions.assertThrows(
                    FriendRequestException.class,
                    () -> friendRequestService.sendFriendRequest(1L, 1L)
            );
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void sendBlocksWhenAlreadyFriends() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(friendshipRepository.existsBetween(1L, 2L)).thenReturn(true);

            FriendRequestException ex = Assertions.assertThrows(
                    FriendRequestException.class,
                    () -> friendRequestService.sendFriendRequest(1L, 2L)
            );
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        void sendBlocksDuplicatePendingRequest() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(friendshipRepository.existsBetween(1L, 2L)).thenReturn(false);
            when(friendRequestRepository.existsPendingBetween(1L, 2L)).thenReturn(true);

            FriendRequestException ex = Assertions.assertThrows(
                    FriendRequestException.class,
                    () -> friendRequestService.sendFriendRequest(1L, 2L)
            );
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        }
    }


    @Nested
    @DisplayName("Accept Friend Request Tests")
    class AcceptFriendRequestTests {

        @Test
        void acceptSuccessCreatesFriendshipWithCanonicalOrdering() {
            FriendRequest pendingRequest = FriendRequest.builder()
                    .id(10L)
                    .sender(sender)
                    .receiver(receiver)
                    .status(FriendRequestStatus.PENDING)
                    .build();

            when(friendRequestRepository.findByIdAndReceiverIdAndStatus(10L, 2L, FriendRequestStatus.PENDING))
                    .thenReturn(Optional.of(pendingRequest));
            when(friendshipRepository.save(any(Friendship.class))).thenAnswer(invocation -> {
                Friendship toSave = invocation.getArgument(0);
                toSave.setId(100L);
                return toSave;
            });

            Optional<FriendshipResponse> response = friendRequestService.acceptFriendRequest(10L, 2L);

            assertThat(pendingRequest.getStatus()).isEqualTo(FriendRequestStatus.ACCEPTED);
            assertThat(pendingRequest.getRespondedAt()).isNotNull();

            ArgumentCaptor<Friendship> captor = ArgumentCaptor.forClass(Friendship.class);
            verify(friendshipRepository).save(captor.capture());
            Friendship savedFriendship = captor.getValue();
            assertThat(savedFriendship.getUserA().getId()).isEqualTo(1L);
            assertThat(savedFriendship.getUserB().getId()).isEqualTo(2L);

            assertThat(response).isPresent();
            assertThat(response.get().friendUserId()).isEqualTo(sender.getId());
        }

        @Test
        void acceptReturnsEmptyWhenRequestNotFound() {
            when(friendRequestRepository.findByIdAndReceiverIdAndStatus(10L, 2L, FriendRequestStatus.PENDING))
                    .thenReturn(Optional.empty());

            Optional<FriendshipResponse> response = friendRequestService.acceptFriendRequest(10L, 2L);

            assertThat(response).isEmpty();
        }
    }


    @Nested
    @DisplayName("Deny Friend Request Tests")
    class DenyFriendRequestTests {

        @Test
        void denySuccess() {
            FriendRequest pendingRequest = FriendRequest.builder()
                    .id(10L)
                    .sender(sender)
                    .receiver(receiver)
                    .status(FriendRequestStatus.PENDING)
                    .build();

            when(friendRequestRepository.findByIdAndReceiverIdAndStatus(10L, 2L, FriendRequestStatus.PENDING))
                    .thenReturn(Optional.of(pendingRequest));
            when(friendRequestRepository.save(any(FriendRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<FriendRequestResponse> response = friendRequestService.denyFriendRequest(10L, 2L);

            assertThat(pendingRequest.getStatus()).isEqualTo(FriendRequestStatus.DENIED);
            assertThat(response).isPresent();
            assertThat(response.get().status()).isEqualTo(FriendRequestStatus.DENIED);
        }
    }


    @Nested
    @DisplayName("Cancel Friend Request Tests")
    class CancelFriendRequestTests {

        @Test
        void cancelSuccess() {
            FriendRequest pendingRequest = FriendRequest.builder()
                    .id(10L)
                    .sender(sender)
                    .receiver(receiver)
                    .status(FriendRequestStatus.PENDING)
                    .build();

            when(friendRequestRepository.findByIdAndSenderIdAndStatus(10L, 1L, FriendRequestStatus.PENDING))
                    .thenReturn(Optional.of(pendingRequest));
            when(friendRequestRepository.save(any(FriendRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<FriendRequestResponse> response = friendRequestService.cancelFriendRequest(10L, 1L);

            assertThat(pendingRequest.getStatus()).isEqualTo(FriendRequestStatus.CANCELED);
            assertThat(response).isPresent();
            assertThat(response.get().status()).isEqualTo(FriendRequestStatus.CANCELED);
        }

        @Test
        void cancelReturnsEmptyWhenNotOwnedBySender() {
            when(friendRequestRepository.findByIdAndSenderIdAndStatus(10L, 2L, FriendRequestStatus.PENDING))
                    .thenReturn(Optional.empty());

            Optional<FriendRequestResponse> response = friendRequestService.cancelFriendRequest(10L, 2L);

            assertThat(response).isEmpty();
        }
    }
}
