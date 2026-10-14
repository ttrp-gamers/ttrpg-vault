package com.ttrp.manager.controller;

import com.ttrp.manager.dto.character.CharacterRequest;
import com.ttrp.manager.dto.character.CharacterResponse;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.declaration.ICharacterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
@IsRegisteredUser
@RequiredArgsConstructor
public class CharacterController {

    private final ICharacterService characterService;

    @PostMapping
    public ResponseEntity<CharacterResponse> createCharacter(
            @CurrentUser UserIdentity activeUser,
            @Valid @RequestBody CharacterRequest request
    ) {
        CharacterResponse response = characterService.createCharacter(activeUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CharacterResponse>> listCharacters(
            @CurrentUser UserIdentity activeUser
    ) {
        return ResponseEntity.ok(characterService.getCharactersByUser(activeUser.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacter(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(characterService.getCharacterById(activeUser.id(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterResponse> updateCharacter(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long id,
            @Valid @RequestBody CharacterRequest request
    ) {
        return ResponseEntity.ok(characterService.updateCharacter(activeUser.id(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(
            @CurrentUser UserIdentity activeUser,
            @PathVariable Long id
    ) {
        characterService.deleteCharacter(activeUser.id(), id);
        return ResponseEntity.noContent().build();
    }
}

