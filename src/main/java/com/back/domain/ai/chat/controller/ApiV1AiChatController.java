package com.back.domain.ai.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/chat")
@RequiredArgsConstructor
public class ApiV1AiChatController {
    private final OpenAiChatModel openAiChatModel;

    @GetMapping("/write")
    public String write(String msg) {
        String response = openAiChatModel.call(msg);

        return response;
    }
}
