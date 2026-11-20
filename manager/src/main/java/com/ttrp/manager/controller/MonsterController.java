package com.ttrp.manager.controller;

import com.ttrp.manager.dto.monster.MonsterRequest;
import com.ttrp.manager.dto.monster.MonsterResponse;
import com.ttrp.manager.helper.CurrentUser;
import com.ttrp.manager.helper.authentication.UserIdentity;
import com.ttrp.manager.helper.authentication.annotation.IsRegisteredUser;
import com.ttrp.manager.service.declaration.IMonsterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monsters")
@IsRegisteredUser
@RequiredArgsConstructor
public class MonsterController {
    private final IMonsterService monsterService;

    @PostMapping
    public ResponseEntity<MonsterResponse> create(@CurrentUser UserIdentity user,
                                                   @Valid @RequestBody MonsterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(monsterService.createMonster(user.id(), request));
    }

    @GetMapping
    public ResponseEntity<List<MonsterResponse>> list(@CurrentUser UserIdentity user) {
        return ResponseEntity.ok(monsterService.getMonstersByUser(user.id()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonsterResponse> update(@CurrentUser UserIdentity user, @PathVariable Long id,
                                                   @Valid @RequestBody MonsterRequest request) {
        return ResponseEntity.ok(monsterService.updateMonster(user.id(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUser UserIdentity user, @PathVariable Long id) {
        monsterService.deleteMonster(user.id(), id);
        return ResponseEntity.noContent().build();
    }
}
