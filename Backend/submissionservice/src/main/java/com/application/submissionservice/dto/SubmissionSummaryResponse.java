package com.application.submissionservice.dto;

import com.application.submissionservice.entity.SubmissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubmissionSummaryResponse(
        Long id,
        Long problemId,
        String languageKey,
        SubmissionStatus status,
        Integer runtimeMs,
        Integer memoryKb,
        LocalDateTime createdAt
) {
}
