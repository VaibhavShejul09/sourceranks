package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressSummaryResponse {

    private Integer enrolledPlans;
    private Integer streakCount;
    private CurrentPlan currentPlan;

    @Data
    @Builder
    public static class CurrentPlan {
        private Long studyPlanId;
        private String title;
        private Double completionPercentage;
        private String nextItemTitle;
    }
}
