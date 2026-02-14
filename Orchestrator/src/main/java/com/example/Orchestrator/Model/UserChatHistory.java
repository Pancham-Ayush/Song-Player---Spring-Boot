package com.example.Orchestrator.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("USER_CHAT_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChatHistory {

    @Id
    private Long id;

    @Column("user_id")
    private String userId;

    @Column("chat_id")
    private String chatId;

    @Column("message")
    private String message;

    @Column("created_at")
    private LocalDateTime createdAt;
}
