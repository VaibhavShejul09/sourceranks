package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminKpiDashboardResponse {

    private long totalTrackedUsers;
    private long activeUsersLast7Days;
    private long totalEvents;
    private long loginEvents;
    private long onboardingCompletions;
    private long codingEvents;
    private long quizEvents;
    private long progressEvents;
    private double activationRate;
    private double engagementRate;
    private double averageEventsPerTrackedUser;
    private List<KpiCard> highlights;

    @Data
    @Builder
    public static class KpiCard {
        private String title;
        private String description;
        private String valueLabel;
        private String tone;
    }

    @Data
    @Builder
    public static class ContentAnalyticsItem {
        private String contentId;
        private String contentTitle;
        private String topic;
        private long attemptCount;
        private long uniqueUserCount;
        private long successCount;
        private double solveRate;
        private double acceptanceRate;
        private double completionRate;
    }

    @Data
    @Builder
    public static class ContentAnalyticsResponse {
        private String contentType;
        private long totalTrackedItems;
        private double averageSolveRate;
        private double averageAcceptanceRate;
        private double averageCompletionRate;
        private ContentAnalyticsItem mostAttempted;
        private ContentAnalyticsItem leastAttempted;
        private List<ContentAnalyticsItem> items;
    }
}
