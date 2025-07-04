package com.back.domain.ai.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/chat")
@RequiredArgsConstructor
public class ApiV1AiChatController {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @GetMapping("/write")
    public String write(String msg) {
        String conversationId = "default";

        List<Message> memories = chatMemory.get(conversationId);

        String response = chatClient
                .prompt()
                .messages(memories)
                .user(msg)
                .call()
                .content();

        if (response == null || response.isEmpty()) {
            return "죄송합니다. 응답을 생성할 수 없습니다.";
        }

        return response;
    }
}
