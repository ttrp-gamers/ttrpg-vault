package com.ttrp.manager.service.declaration;

import com.ttrp.manager.dto.game.AddGameMemberRequest;
import com.ttrp.manager.dto.game.CreateGameRequest;
import com.ttrp.manager.dto.game.GameMemberResponse;
import com.ttrp.manager.dto.game.GameResponse;

import java.util.List;

public interface IGameService {
    GameResponse createGame(Long userId, CreateGameRequest request);
    List<GameResponse> listGames(Long userId);
    GameMemberResponse addMember(Long callerId, Long gameId, AddGameMemberRequest request);
}
