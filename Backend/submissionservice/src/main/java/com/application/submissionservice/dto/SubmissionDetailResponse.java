package com.application.submissionservice.dto;

import com.application.submissionservice.entity.SubmissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubmissionDetailResponse(
        Long id,
        Long userId,
        Long problemId,
        String languageKey,
        String sourceCode,
        SubmissionStatus status,
        Integer runtimeMs,
        Integer memoryKb,
        LocalDateTime createdAt
) {
}
