package com.application.submissionservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProblemAttemptSummaryResponse(
        Long problemId,
        long totalAttempts,
        long acceptedAttempts,
        String latestStatus,
        Integer bestRuntimeMs,
        List<String> languagesUsed
) {
}
