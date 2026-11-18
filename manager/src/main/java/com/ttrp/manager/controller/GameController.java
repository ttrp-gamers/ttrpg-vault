package com.ttrp.manager.controller;

import com.ttrp.manager.dto.game.AddGameMemberRequest;
import com.ttrp.manager.dto.game.CreateGameRequest;
import com.ttrp.manager.dto.game.GameMemberResponse;
import com.ttrp.manager.dto.game.GameResponse;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.declaration.IGameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@IsRegisteredUser
@RequiredArgsConstructor
public class GameController {

    private final IGameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> createGame(
            @CurrentUser UserIdentity activeUser,
            @Valid @RequestBody CreateGameRequest request
    ) {
        GameResponse response = gameService.createGame(activeUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> listGames(
            @CurrentUser UserIdentity activeUser
    ) {
        return ResponseEntity.ok(gameService.listGames(activeUser.id()));
    }

    @PostMapping("/{gameId}/members")
    public ResponseEntity<GameMemberResponse> addMember(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long gameId,
            @Valid @RequestBody AddGameMemberRequest request
    ) {
        GameMemberResponse response = gameService.addMember(activeUser.id(), gameId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
