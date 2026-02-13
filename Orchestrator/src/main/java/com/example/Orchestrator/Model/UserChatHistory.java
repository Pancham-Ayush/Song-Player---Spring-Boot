package com.example.Orchestrator.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChatHistory {

    @Id
    private String userId;

    private List<String> chatId;
}
