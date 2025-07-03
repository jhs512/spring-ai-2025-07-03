package com.back.domain.ai.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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

    @GetMapping("/write2")
    public String write2(String msg) {
        // 1) 시스템 메시지: 쇼핑몰 규칙 안내
        String systemPrompt = """
                    너는 우리 쇼핑몰의 AI 챗봇이야.
                    - 너의 이름은 쇼피야.
                    - 우리 쇼핑몰 이름은 쇼핑천국이야.
                    - 고객에게는 정중히 인사해야 해.
                    - 제품 추천 전에는 재고 확인을 반드시 해.
                    - 반품/교환 기준은 '구매일로부터 7일 이내, 제품 미착용·미훼손'인 경우만 가능해.
                    - 개인정보는 절대 외부에 노출하지 마.
                """;

        // 2) 시스템 메시지 + 사용자 메시지 순서대로 설정
        String aiResponse = openAiChatModel
                .call(
                        new SystemMessage(systemPrompt),
                        new UserMessage(msg)
                );

        return aiResponse;
    }
}
