package com.example.Orchestrator.Repo;

import com.example.Orchestrator.Model.SpringAiChatMemory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMemoryRepo extends ReactiveCrudRepository<SpringAiChatMemory, Long> {
    Flux<SpringAiChatMemory> findByConversationIdOrderByTimestampAsc(String conversation_id);

}
