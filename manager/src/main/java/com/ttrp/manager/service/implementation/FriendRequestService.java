package com.ttrp.manager.service.implementation;


import com.ttrp.manager.dto.friendRequest.FriendRequestResponse;
import com.ttrp.manager.dto.friendRequest.FriendshipResponse;
import com.ttrp.manager.entity.FriendRequest;
import com.ttrp.manager.entity.Friendship;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.FriendRequestStatus;
import com.ttrp.manager.exception.FriendRequestException;
import com.ttrp.manager.mapper.FriendRequestMapper;
import com.ttrp.manager.repository.FriendRequestRepository;
import com.ttrp.manager.repository.FriendshipRepository;
import com.ttrp.manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendRequestMapper friendRequestMapper;

    public FriendRequestService(
            FriendRequestRepository friendRequestRepository,
            FriendshipRepository friendshipRepository,
            UserRepository userRepository,
            FriendRequestMapper friendRequestMapper) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.friendRequestMapper = friendRequestMapper;
    }


    @Transactional
    public FriendRequestResponse sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new FriendRequestException(HttpStatus.BAD_REQUEST, "Cannot send a friend request to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new FriendRequestException(HttpStatus.NOT_FOUND, "Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new FriendRequestException(HttpStatus.NOT_FOUND, "Receiver not found"));

        if (friendshipRepository.existsBetween(senderId, receiverId)) {
            throw new FriendRequestException(HttpStatus.CONFLICT, "You are already friends with this user");
        }

        if (friendRequestRepository.existsPendingBetween(senderId, receiverId)) {
            throw new FriendRequestException(HttpStatus.CONFLICT, "A pending friend request already exists between these users");
        }

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        return friendRequestMapper.toResponse(friendRequestRepository.save(friendRequest));
    }


    @Transactional
    public Optional<FriendshipResponse> acceptFriendRequest(Long requestId, Long currentUserId) {
        return friendRequestRepository.findByIdAndReceiverIdAndStatus(requestId, currentUserId, FriendRequestStatus.PENDING)
                .map(friendRequest -> {
                    friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
                    friendRequest.setRespondedAt(Instant.now());
                    friendRequestRepository.save(friendRequest);

                    User sender = friendRequest.getSender();
                    User receiver = friendRequest.getReceiver();
                    boolean senderIsLower = sender.getId() < receiver.getId();

                    Friendship friendship = Friendship.builder()
                            .userA(senderIsLower ? sender : receiver)
                            .userB(senderIsLower ? receiver : sender)
                            .createdAt(Instant.now())
                            .build();

                    Friendship savedFriendship = friendshipRepository.save(friendship);
                    return friendRequestMapper.toFriendshipResponse(savedFriendship, currentUserId);
                });
    }


    @Transactional
    public Optional<FriendRequestResponse> denyFriendRequest(Long requestId, Long currentUserId) {
        return friendRequestRepository.findByIdAndReceiverIdAndStatus(requestId, currentUserId, FriendRequestStatus.PENDING)
                .map(friendRequest -> {
                    friendRequest.setStatus(FriendRequestStatus.DENIED);
                    friendRequest.setRespondedAt(Instant.now());
                    return friendRequestMapper.toResponse(friendRequestRepository.save(friendRequest));
                });
    }


    @Transactional
    public Optional<FriendRequestResponse> cancelFriendRequest(Long requestId, Long currentUserId) {
        return friendRequestRepository.findByIdAndSenderIdAndStatus(requestId, currentUserId, FriendRequestStatus.PENDING)
                .map(friendRequest -> {
                    friendRequest.setStatus(FriendRequestStatus.CANCELED);
                    friendRequest.setRespondedAt(Instant.now());
                    return friendRequestMapper.toResponse(friendRequestRepository.save(friendRequest));
                });
    }


    @Transactional
    public List<FriendRequestResponse> listIncoming(Long userId) {
        return friendRequestRepository.findByReceiverIdAndStatus(userId, FriendRequestStatus.PENDING)
                .stream()
                .map(friendRequestMapper::toResponse)
                .toList();
    }


    @Transactional
    public List<FriendRequestResponse> listOutgoing(Long userId) {
        return friendRequestRepository.findBySenderIdAndStatus(userId, FriendRequestStatus.PENDING)
                .stream()
                .map(friendRequestMapper::toResponse)
                .toList();
    }
}
