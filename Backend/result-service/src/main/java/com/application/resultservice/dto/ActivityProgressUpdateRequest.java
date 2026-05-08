package com.application.resultservice.dto;

import lombok.Builder;

@Builder
public record ActivityProgressUpdateRequest(
        String itemType,
        String referenceKey,
        String sourceEventId
) {
}
