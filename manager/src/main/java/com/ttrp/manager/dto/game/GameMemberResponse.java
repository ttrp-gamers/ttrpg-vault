package com.ttrp.manager.dto.game;

import com.ttrp.manager.entity.type.GameRole;

public record GameMemberResponse(Long userId, String username, GameRole role) {
}
