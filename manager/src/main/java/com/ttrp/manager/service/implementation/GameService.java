package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.game.AddGameMemberRequest;
import com.ttrp.manager.dto.game.CreateGameRequest;
import com.ttrp.manager.dto.game.GameMemberResponse;
import com.ttrp.manager.dto.game.GameResponse;
import com.ttrp.manager.entity.Game;
import com.ttrp.manager.entity.GameMember;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.GameRole;
import com.ttrp.manager.mapper.GameMapper;
import com.ttrp.manager.repository.FriendshipRepository;
import com.ttrp.manager.repository.GameMemberRepository;
import com.ttrp.manager.repository.GameRepository;
import com.ttrp.manager.repository.UserRepository;
import com.ttrp.manager.service.declaration.IGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService implements IGameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameMemberRepository gameMemberRepository;
    private final FriendshipRepository friendshipRepository;
    private final GameMapper gameMapper;

    @Override
    @Transactional
    public GameResponse createGame(Long userId, CreateGameRequest request) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Game game = gameRepository.save(Game.builder()
                .name(request.name())
                .gameOwner(currentUser)
                .build());

        // The owner is also a member, so membership checks never need an owner special case.
        GameMember ownerMembership = gameMemberRepository.save(GameMember.builder()
                .game(game)
                .user(currentUser)
                .role(GameRole.GAME_MASTER)
                .build());

        return gameMapper.toGameResponse(ownerMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameResponse> listGames(Long userId) {
        return gameMemberRepository.findAllByUserId(userId).stream()
                .map(gameMapper::toGameResponse)
                .toList();
    }

    @Override
    @Transactional
    public GameMemberResponse addMember(Long callerId, Long gameId, AddGameMemberRequest request) {
        GameMember callerMembership = gameMemberRepository.findByGameIdAndUserId(gameId, callerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (callerMembership.getRole() != GameRole.GAME_MASTER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the game master can add members");
        }

        Long newMemberId = request.userId();

        if (gameMemberRepository.existsByGameIdAndUserId(gameId, newMemberId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a member of this game");
        }

        if (!friendshipRepository.existsBetween(callerId, newMemberId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only add friends to a game");
        }

        User newMember = userRepository.findById(newMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GameMember membership = gameMemberRepository.save(GameMember.builder()
                .game(callerMembership.getGame())
                .user(newMember)
                .role(GameRole.PLAYER)
                .build());

        return gameMapper.toMemberResponse(membership);
    }
}
