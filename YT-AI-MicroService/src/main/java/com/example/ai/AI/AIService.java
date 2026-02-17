package com.example.ai.AI;

import com.example.ai.DTO.SONG_YT_DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AIService {

    private final ChatClient mistralChatClient;
    //  String Template
    @Value("classpath:prompt/verification.st")
    private Resource verificationPrompt;

    @Value("classpath:prompt/pojo-prompt.st")
    private Resource pojoPrompt;

    public AIService(@Qualifier("myMistralChatModel") ChatClient mistralChatClient) {
        this.mistralChatClient = mistralChatClient;
    }

    public boolean aiSongVerification(String message) {
        try {
            res response = mistralChatClient
                    .prompt()
                    .user(u -> u.text(verificationPrompt)
                            .params(Map.of("message", message)))
                    .call()
                    .entity(res.class);

            return response != null && response.verify();
        } catch (Exception e) {
            return false;
        }
    }

    public SONG_YT_DTO aiSongMapping(String data, String email) {
        try {
            return mistralChatClient
                    .prompt()
                    .user(u -> u.text(pojoPrompt)
                            .params(Map.of("data", data, "email", email)))
                    .call()
                    .entity(SONG_YT_DTO.class);
        } catch (Exception e) {
            log.info("aiSongMapping error" + e.getMessage());
            return null;
        }
    }

    /**
     * LLM structured output DTO for verification
     */
    public record res(boolean verify) {
    }
}
