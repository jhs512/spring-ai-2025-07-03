package com.back.domain.ai.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai/memory/chat")
@RequiredArgsConstructor
public class ApiV1AiInMemoryChatController {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @GetMapping("/room")
    public ResponseEntity<Void> makeRoom() {
        String chatRoomCode = UUID.randomUUID().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("./room/" + chatRoomCode));

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .headers(headers)
                .build();
    }

    @GetMapping("/room/{chatRoomCode}")
    public String room(
            @PathVariable String chatRoomCode,
            @RequestParam(defaultValue = "") String msg
    ) {
        if (msg.isBlank()) {
            return "메시지(msg)를 입력해주세요.";
        }

        String response = chatClient
                .prompt()
                .advisors(
                        advisor -> advisor
                                .param(ChatMemory.CONVERSATION_ID, chatRoomCode)
                )
                .user(msg)
                .call()
                .content();

        if (response == null || response.isEmpty()) {
            return "죄송합니다. 응답을 생성할 수 없습니다.";
        }

        return response;
    }
}
