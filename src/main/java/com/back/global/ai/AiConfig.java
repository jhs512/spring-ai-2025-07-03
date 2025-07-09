package com.back.global.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AiConfig {
    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .build()
                )
                .build();
    }

    @Bean
    public JdbcChatMemoryRepository jdbcChatMemoryRepository(JdbcTemplate jdbcTemplate) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new MysqlChatMemoryRepositoryDialect())
                .build();
    }

    @Bean
    public ChatMemory dbChatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient dbChatClient(ChatModel chatModel, ChatMemory dbChatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(dbChatMemory)
                                .build()
                )
                .build();
    }
}
