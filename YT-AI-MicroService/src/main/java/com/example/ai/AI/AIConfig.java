package com.example.ai.AI;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Value("${spring.ai.mistralai.api-key}")
    String apiKey;

    @Bean({"myMistralChatModel"})
    public ChatClient mistralAiChatModel() {
        return ChatClient.create(MistralAiChatModel.builder().mistralAiApi(new MistralAiApi(this.apiKey)).build());
    }

    @Bean
    public MistralAiEmbeddingModel mistralAiEmbeddingModel() {
        return new MistralAiEmbeddingModel(new MistralAiApi(this.apiKey));
    }
}
