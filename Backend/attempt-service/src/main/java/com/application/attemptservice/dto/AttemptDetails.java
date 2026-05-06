package com.application.attemptservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AttemptDetails {

    private UUID attemptId;
    private UUID userId;
    private UUID quizId;

    // questionId -> selectedOption
    private Map<UUID, String> answers;
}