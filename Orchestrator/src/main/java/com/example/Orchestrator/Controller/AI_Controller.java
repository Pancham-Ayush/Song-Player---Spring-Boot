package com.example.Orchestrator.Controller;

import com.example.Orchestrator.Service.ChatClientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AI_Controller {


    private final ChatClientService chatClientService;


    AI_Controller(ChatClientService chatClientService) {
        this.chatClientService = chatClientService;
    }


    @PostMapping
    public String search(@RequestParam("q") String q) {
        return chatClientService.aiResponse(q);
    }
}
