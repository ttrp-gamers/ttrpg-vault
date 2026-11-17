package com.ttrp.manager.dto.game;

import com.ttrp.manager.entity.type.GameRole;

public record GameResponse(Long id, String name, GameRole role) {
}
