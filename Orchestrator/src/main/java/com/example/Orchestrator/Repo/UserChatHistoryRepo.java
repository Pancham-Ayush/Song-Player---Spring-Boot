package com.example.Orchestrator.Repo;

import com.example.Orchestrator.Model.UserChatHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserChatHistoryRepo extends ReactiveCrudRepository<UserChatHistory, Long> {

}
