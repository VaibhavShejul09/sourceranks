package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserStudyPlanResponse {

    private Long studyPlanId;
    private String title;
    private String track;
    private String level;
    private LocalDateTime enrolledAt;
    private Double completionPercentage;
    private String nextItemTitle;
}
