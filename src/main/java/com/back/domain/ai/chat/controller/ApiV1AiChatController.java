package com.back.domain.ai.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai/chat")
@RequiredArgsConstructor
public class ApiV1AiChatController {
    private final ChatModel chatModel;

    @GetMapping("/write")
    public String write(String msg) {
        String response = chatModel.call(msg);

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
        String aiResponse = chatModel
                .call(
                        new SystemMessage(systemPrompt),
                        new UserMessage(msg)
                );

        return aiResponse;
    }

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
            @RequestParam(defaultValue = "") String msg,
            @RequestParam(defaultValue = "") String oldMsg
    ) {
        final String USER_PREFIX = "사용자 : ";
        final String AI_PREFIX = "LLM 답변 : ";

        if (msg.isBlank()) return """
                채팅방 %s에 오신 것을 환영합니다.
                <form>
                    <input required type="text" name="msg" placeholder="메시지를 입력하세요." autofocus>
                    <br>
                    <button type="submit">전송</button>
                """.formatted(chatRoomCode);

        oldMsg = oldMsg.trim();

        String systemPrompt = """
                대답을 길게하지마
                """;

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        // 이전 대화 파싱
        parseOldMessages(oldMsg, messages, USER_PREFIX, AI_PREFIX);

        // 현재 사용자 메시지 추가
        messages.add(new UserMessage(msg));

        // LLM 응답 생성
        String response = chatModel.call(messages.toArray(Message[]::new));

        // oldMsg 누적 저장
        StringBuilder updatedOldMsg = new StringBuilder(oldMsg);
        updatedOldMsg
                .append("\n\n").append(USER_PREFIX).append(msg)
                .append("\n\n").append(AI_PREFIX).append(response);

        return """
                <form>
                    <textarea name="oldMsg" rows="30" cols="50">%s</textarea>
                    <br>
                    <input required type="text" name="msg" placeholder="메시지를 입력하세요." autofocus>
                    <br>
                    <button type="submit">전송</button>
                </form>
                """.formatted(updatedOldMsg.toString().trim());
    }

    // 이전 메시지를 파싱하여 리스트에 추가하는 유틸 메서드
    private void parseOldMessages(String oldMsg, List<Message> messages, String userPrefix, String aiPrefix) {
        for (String part : oldMsg.split("\\n\\n")) {
            part = part.trim();

            if (part.startsWith(userPrefix)) {
                messages.add(new UserMessage(part.replaceFirst("^" + userPrefix, "")));
            } else if (part.startsWith(aiPrefix)) {
                messages.add(new AssistantMessage(part.replaceFirst("^" + aiPrefix, "")));
            }
        }
    }
}
