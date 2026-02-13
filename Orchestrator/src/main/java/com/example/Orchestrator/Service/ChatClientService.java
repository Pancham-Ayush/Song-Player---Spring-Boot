package com.example.Orchestrator.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatClientService {

    ChatClient chatClient;

    ChatMemory chatMemory;


    private ChatClientService(@Qualifier("openai") ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    public Flux<String> aiResponse(String ques, String id) {

        return chatClient
                .prompt()
                .user(ques)
                .advisors(a -> {
                    a.param(ChatMemory.CONVERSATION_ID, id);
                })
                .stream()
                .content();

    }

}
