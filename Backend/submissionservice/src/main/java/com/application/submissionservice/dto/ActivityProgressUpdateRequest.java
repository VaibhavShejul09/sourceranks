package com.application.submissionservice.dto;

import lombok.Builder;

@Builder
public record ActivityProgressUpdateRequest(
        String itemType,
        String referenceKey,
        String sourceEventId
) {
}
