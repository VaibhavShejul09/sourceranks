package com.application.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEventRequest {

    @NotBlank
    private String eventName;

    @NotBlank
    private String eventCategory;

    private String source;

    private String track;

    private String contentType;

    private String contentId;

    private String contentTitle;

    private String parentContentId;

    private String topic;

    private String outcome;

    private Double numericValue;

    private LocalDateTime occurredAt;

    private Map<String, Object> metadata;
}
