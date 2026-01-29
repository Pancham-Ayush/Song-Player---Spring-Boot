package com.example.Orchestrator.Controller;

import com.example.Orchestrator.Service.ChatClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AIController {

    @Autowired
    ChatClientService chatClientService;

    @GetMapping("/chat")
    Flux<String> sendMessage(@RequestParam String q) {
        String Email = "1";
        return chatClientService.aiResponse(q, Email);
    }
}
