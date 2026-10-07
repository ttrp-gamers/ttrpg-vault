package com.ttrp.manager.mapper;


import com.ttrp.manager.dto.friendRequest.FriendRequestResponse;
import com.ttrp.manager.dto.friendRequest.FriendshipResponse;
import com.ttrp.manager.entity.FriendRequest;
import com.ttrp.manager.entity.Friendship;
import com.ttrp.manager.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface FriendRequestMapper {

    FriendRequestResponse toResponse(FriendRequest friendRequest);

    default FriendshipResponse toFriendshipResponse(Friendship friendship, Long viewerUserId) {
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
