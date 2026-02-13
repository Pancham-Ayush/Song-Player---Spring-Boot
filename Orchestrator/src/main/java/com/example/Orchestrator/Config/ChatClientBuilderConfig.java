package com.example.Orchestrator.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientBuilderConfig {

    @Bean("gemini-cli")
    public ChatClient.Builder geminiChatClientBuilder(GoogleGenAiChatModel model) {
        return ChatClient.builder(model);
    }

    @Bean("openai-cli")
    public ChatClient.Builder openaiChatClientBuilder(OpenAiChatModel model) {
        return ChatClient.builder(model);
    }

}
