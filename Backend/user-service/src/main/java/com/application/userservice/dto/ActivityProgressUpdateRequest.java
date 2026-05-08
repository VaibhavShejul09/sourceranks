package com.application.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityProgressUpdateRequest {

    @NotBlank(message = "Item type is required")
    private String itemType;

    @NotBlank(message = "Reference key is required")
    private String referenceKey;

    @NotBlank(message = "Source event id is required")
    private String sourceEventId;
}
