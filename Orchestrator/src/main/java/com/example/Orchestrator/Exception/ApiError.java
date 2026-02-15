package com.example.Orchestrator.Exception;

import com.example.Orchestrator.Model.UserChatHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Setter

public class ApiError extends UserChatHistory {

    private String message;
    private int code;
}
