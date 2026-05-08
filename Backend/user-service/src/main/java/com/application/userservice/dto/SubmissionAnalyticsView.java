package com.application.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionAnalyticsView {

    private Long id;
    private Long problemId;
    private String languageKey;
    private String status;
    private Integer runtimeMs;
    private Integer memoryKb;
    private LocalDateTime createdAt;
}
