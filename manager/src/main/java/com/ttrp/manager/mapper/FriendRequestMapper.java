package com.ttrp.manager.mapper;


import com.ttrp.manager.dto.friendRequest.FriendRequestResponse;
import com.ttrp.manager.dto.friendRequest.FriendshipResponse;
import com.ttrp.manager.entity.FriendRequest;
import com.ttrp.manager.entity.Friendship;
import com.ttrp.manager.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestMapper {

    public FriendRequestResponse toResponse(FriendRequest friendRequest) {
        return new FriendRequestResponse(
                friendRequest.getId(),
                friendRequest.getSender().getId(),
                friendRequest.getSender().getUsername(),
                friendRequest.getReceiver().getId(),
                friendRequest.getReceiver().getUsername(),
                friendRequest.getStatus(),
                friendRequest.getCreatedAt(),
                friendRequest.getRespondedAt()
        );
    }

    public FriendshipResponse toFriendshipResponse(Friendship friendship, Long viewerUserId) {
        User friend = friendship.getUserA().getId().equals(viewerUserId)
                ? friendship.getUserB()
                : friendship.getUserA();

        return new FriendshipResponse(
                friendship.getId(),
                friend.getId(),
                friend.getUsername(),
                friendship.getCreatedAt()
        );
    }
}
