package com.example.Orchestrator.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean("gemini")
    public ChatClient geminiChatClient(
            @Qualifier("gemini-cli") ChatClient.Builder builder,
            ChatMemory chatMemory
    ) {
        return builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Bean("openai")
    public ChatClient openaiChatClient(
            @Qualifier("openai-cli") ChatClient.Builder builder,
            ChatMemory chatMemory
    ) {
        return builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}
