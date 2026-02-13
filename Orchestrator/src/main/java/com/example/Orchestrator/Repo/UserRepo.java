package com.example.Orchestrator.Repo;

import com.example.Orchestrator.Model.UserChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<UserChatHistory, String> {

    List<UserChatHistory> findByUserId(String userId);

}
