package com.ttrp.manager.mapper;

import com.ttrp.manager.dto.game.GameMemberResponse;
import com.ttrp.manager.dto.game.GameResponse;
import com.ttrp.manager.entity.GameMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    // A game is always shown from one member's point of view, so the role comes along with it.
    @Mapping(target = "id", source = "game.id")
    @Mapping(target = "name", source = "game.name")
    GameResponse toGameResponse(GameMember member);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    GameMemberResponse toMemberResponse(GameMember member);
}
