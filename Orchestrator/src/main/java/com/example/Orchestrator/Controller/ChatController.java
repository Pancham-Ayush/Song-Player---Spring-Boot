package com.example.Orchestrator.Controller;

import com.example.Orchestrator.Exception.ApiError;
import com.example.Orchestrator.Model.UserChatHistory;
import com.example.Orchestrator.Repo.UserChatHistoryRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final UserChatHistoryRepo userChatHistoryRepo;

    public ChatController(UserChatHistoryRepo userChatHistoryRepo) {
        this.userChatHistoryRepo = userChatHistoryRepo;
    }

    @GetMapping
    public Mono<ResponseEntity<UserChatHistory>> get(
            @RequestHeader("X-User-Email") String userId,
            @RequestParam String chatId) {

        return userChatHistoryRepo
                .findByUserIdAndChatId(userId, chatId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public Mono<UserChatHistory> create(@RequestHeader("X-User-Email") String userId) {
        return userChatHistoryRepo.save(UserChatHistory
                .builder()
                .userId(userId)
                .chatId(String.valueOf(UUID.randomUUID()))
                .createdAt(LocalDateTime.now())
                .build());
    }

    @PatchMapping
    public Mono<UserChatHistory> update(@RequestParam String chatId, @RequestParam String content) {
        return userChatHistoryRepo
                .findUserChatHistoriesByChatId(chatId)
                .flatMap(existing -> {
                    existing.setMessage(content);
                    return userChatHistoryRepo.save(existing);

                });
    }

    @DeleteMapping
    public Mono<ResponseEntity<UserChatHistory>> delete(@RequestParam String chatId) {
        return userChatHistoryRepo
                .findUserChatHistoriesByChatId(chatId)
                .flatMap(
                        x -> userChatHistoryRepo
                                .deleteById(
                                        x.getId()
                                )
                                .thenReturn(
                                        ResponseEntity.ok(x)
                                )
                )
                .defaultIfEmpty(
                        ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiError("Error not there", 204))
                );

    }


}
