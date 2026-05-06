package com.application.attemptservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StartAttemptRequest {
    private UUID quizId;
}

