package com.example.Orchestrator.Controller;

import com.example.Orchestrator.Model.SpringAiChatMemory;
import com.example.Orchestrator.Model.UserChatHistory;
import com.example.Orchestrator.Repo.ChatMemoryRepo;
import com.example.Orchestrator.Repo.UserChatHistoryRepo;
import com.example.Orchestrator.Service.ChatClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
public class TestController {

    @Autowired
    ChatClientService chatClientService;

    @Autowired
    ChatMemoryRepo chatMemoryRepository;

    @Autowired
    UserChatHistoryRepo chatHistoryRepository;


    @GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<SpringAiChatMemory> test() {
        return chatMemoryRepository.findByConversationIdOrderByTimestampAsc("1").delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/test0")
    public ResponseEntity<Flux<String>> getStudents(@RequestParam String q) {
        String Email = "1";
        return ResponseEntity.ok().body(chatClientService.aiResponse(q, Email).delayElements(Duration.ofSeconds(1)).log());
    }


    @GetMapping(value = "/test2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<?> test2() {
        return chatHistoryRepository.save(
                UserChatHistory.builder()
//                        .id(5L)
                        .userId("1")
                        .chatId("5")
                        .message("hi")
                        .createdAt(LocalDateTime.now())
                        .build()).delayElement(Duration.ofSeconds(3)).log();
    }

}
