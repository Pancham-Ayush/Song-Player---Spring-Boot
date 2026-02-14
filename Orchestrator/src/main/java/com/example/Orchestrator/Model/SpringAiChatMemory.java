package com.example.Orchestrator.Model;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;


@Table(name = "SPRING_AI_CHAT_MEMORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SpringAiChatMemory {

    @Id
    private Long id;

    @Column("conversation_id")
    private String conversationId;

    private String content;

    private String type;

    @CreatedDate
    private Timestamp timestamp;
}
