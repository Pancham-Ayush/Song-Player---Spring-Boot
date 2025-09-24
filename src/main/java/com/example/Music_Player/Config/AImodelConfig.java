package com.example.Music_Player.Config;

import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AImodelConfig {
    @Value("${spring.ai.mistralai.api-key}")
    private String apiKey;
    @Bean
    public MistralAiEmbeddingModel mistralAiEmbeddingModel() {
        return new MistralAiEmbeddingModel(new MistralAiApi(apiKey));
    }
}
