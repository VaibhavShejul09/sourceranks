package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductEventResponse {

    private Long id;
    private String eventName;
    private String eventCategory;
    private boolean accepted;
    private LocalDateTime occurredAt;
}
