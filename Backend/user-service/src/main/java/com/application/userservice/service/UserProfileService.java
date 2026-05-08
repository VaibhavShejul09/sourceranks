package com.application.userservice.service;

import com.application.userservice.dto.DashboardSummaryResponse;
import com.application.userservice.dto.UserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserPreferenceService userPreferenceService;
    private final StudyPlanService studyPlanService;
    private final UserAnalyticsService userAnalyticsService;

    public UserProfileService(
            UserPreferenceService userPreferenceService,
            StudyPlanService studyPlanService,
            UserAnalyticsService userAnalyticsService
    ) {
        this.userPreferenceService = userPreferenceService;
        this.studyPlanService = studyPlanService;
        this.userAnalyticsService = userAnalyticsService;
    }

    public UserProfileResponse getProfile(UUID userId, String role) {
        return UserProfileResponse.builder()
                .userId(userId.toString())
                .role(role)
                .displayName(buildDisplayName(userId))
                .onboardingCompleted(userPreferenceService.getPreferences(userId).isOnboardingCompleted())
                .build();
    }

    public DashboardSummaryResponse getDashboardSummary(UUID userId, String role) {
        DashboardSummaryResponse summary = userPreferenceService.getDashboardSummary(userId, role, buildDisplayName(userId));
        var progressSummary = studyPlanService.getProgressSummary(userId);

        summary.setStreakCount(progressSummary.getStreakCount());
        summary.setRecommendations(userAnalyticsService.getDashboardRecommendations(userId, role));
        if (progressSummary.getCurrentPlan() != null) {
            summary.setCurrentStudyPlan(DashboardSummaryResponse.CurrentStudyPlan.builder()
                    .studyPlanId(progressSummary.getCurrentPlan().getStudyPlanId())
                    .title(progressSummary.getCurrentPlan().getTitle())
                    .completionPercentage(progressSummary.getCurrentPlan().getCompletionPercentage())
                    .nextItemTitle(progressSummary.getCurrentPlan().getNextItemTitle())
                    .build());
        }

        return summary;
    }

    private String buildDisplayName(UUID userId) {
        return "User " + userId.toString().substring(0, 8);
    }
}
