package com.example.Orchestrator.Repo;

import com.example.Orchestrator.Model.UserChatHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserChatHistoryRepo extends ReactiveCrudRepository<UserChatHistory, Long> {

    Mono<UserChatHistory> findUserChatHistoriesByChatId(String chatId);

    Mono<UserChatHistory> deleteByChatId(String chatId);

    Mono<UserChatHistory> findByUserIdAndChatId(String userId, String chatId);
}
