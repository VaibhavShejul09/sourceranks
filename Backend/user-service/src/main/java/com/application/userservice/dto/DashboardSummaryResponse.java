package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardSummaryResponse {

    private String userId;
    private String displayName;
    private String role;
    private boolean onboardingCompleted;
    private String goal;
    private String preferredTrack;
    private String skillLevel;
    private RecommendedAction recommendedFirstAction;
    private List<ChecklistItem> checklist;

    @Data
    @Builder
    public static class RecommendedAction {
        private String title;
        private String description;
        private String route;
    }

    @Data
    @Builder
    public static class ChecklistItem {
        private String key;
        private String title;
        private String description;
        private boolean completed;
    }
}
