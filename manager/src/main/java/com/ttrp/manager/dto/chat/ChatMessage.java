package com.ttrp.manager.dto.chat;

import java.time.Instant;

public record ChatMessage(
        String sender,
        String content,
        Instant sentAt
) {
}
