package com.application.userservice.service;

import com.application.userservice.dto.DashboardSummaryResponse;
import com.application.userservice.dto.UserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserPreferenceService userPreferenceService;

    public UserProfileService(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
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
        return userPreferenceService.getDashboardSummary(userId, role, buildDisplayName(userId));
    }

    private String buildDisplayName(UUID userId) {
        return "User " + userId.toString().substring(0, 8);
    }
}
