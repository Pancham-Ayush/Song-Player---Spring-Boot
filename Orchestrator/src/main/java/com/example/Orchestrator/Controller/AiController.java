package com.example.Orchestrator.Controller;

import com.example.Orchestrator.Service.ChatClientService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final ChatClientService chatClientService;

    public AiController(ChatClientService chatClientService) {
        this.chatClientService = chatClientService;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> sendMessage(@RequestParam String q, @RequestParam String chatId) {
        return chatClientService.aiResponse(q, chatId).log();
    }
}
