package com.application.submissionservice.dto;

import com.application.submissionservice.entity.SubmissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SubmissionDetailResponse(
        Long id,
        UUID userId,
        Long problemId,
        String languageKey,
        String sourceCode,
        SubmissionStatus status,
        Integer runtimeMs,
        Integer memoryKb,
        LocalDateTime createdAt
) {
}
