package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserPreferenceResponse {

    private String userId;
    private String goal;
    private String preferredTrack;
    private String skillLevel;
    private boolean onboardingCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
