package com.example.Orchestrator.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatClientService {

    final JdbcChatMemoryRepository chatMemoryRepository;
    private final ChatClient chatClient;

    public ChatClientService(ChatClient.Builder builder, JdbcChatMemoryRepository chatMemoryRepository) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(12)
                .build();

        this.chatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
        this.chatMemoryRepository = chatMemoryRepository;
    }

    public Flux<String> aiResponse(String ques, String id) {
        return chatClient
                .prompt()
                .user(ques)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                .stream()
                .content();
    }

}
