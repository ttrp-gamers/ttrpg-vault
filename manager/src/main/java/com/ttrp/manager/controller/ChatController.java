package com.ttrp.manager.controller;

import com.ttrp.manager.dto.chat.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage send(@DestinationVariable String roomId, ChatMessage message) {
        return new ChatMessage(message.sender(), message.content(), Instant.now());
    }
}
