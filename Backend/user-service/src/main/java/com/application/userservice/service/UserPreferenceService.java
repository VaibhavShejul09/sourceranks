package com.application.userservice.service;

import com.application.userservice.dto.DashboardSummaryResponse;
import com.application.userservice.dto.UserPreferenceRequest;
import com.application.userservice.dto.UserPreferenceResponse;
import com.application.userservice.entity.UserPreference;
import com.application.userservice.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public UserPreferenceResponse getPreferences(UUID userId) {
        return userPreferenceRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElseGet(() -> emptyPreference(userId));
    }

    public UserPreferenceResponse updatePreferences(UUID userId, UserPreferenceRequest request) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserPreference.builder()
                        .userId(userId)
                        .build());

        preference.setGoal(request.getGoal());
        preference.setPreferredTrack(request.getPreferredTrack());
        preference.setSkillLevel(request.getSkillLevel());
        preference.setOnboardingCompleted(true);

        return mapToResponse(userPreferenceRepository.save(preference));
    }

    public DashboardSummaryResponse getDashboardSummary(UUID userId, String role, String displayName) {
        UserPreferenceResponse preference = getPreferences(userId);

        return DashboardSummaryResponse.builder()
                .userId(userId.toString())
                .displayName(displayName)
                .role(role)
                .onboardingCompleted(preference.isOnboardingCompleted())
                .goal(preference.getGoal())
                .preferredTrack(preference.getPreferredTrack())
                .skillLevel(preference.getSkillLevel())
                .recommendedFirstAction(resolveRecommendedAction(preference))
                .checklist(buildChecklist(preference.isOnboardingCompleted()))
                .build();
    }

    private DashboardSummaryResponse.RecommendedAction resolveRecommendedAction(UserPreferenceResponse preference) {
        if (!preference.isOnboardingCompleted()) {
            return DashboardSummaryResponse.RecommendedAction.builder()
                    .title("Complete onboarding")
                    .description("Tell RankX about your goal, track, and level to personalize your workspace.")
                    .route("/onboarding")
                    .build();
        }

        return switch (preference.getPreferredTrack()) {
            case "Coding" -> DashboardSummaryResponse.RecommendedAction.builder()
                    .title("Solve your first coding problem")
                    .description("Start coding practice aligned to your current goal and level.")
                    .route("/problems")
                    .build();
            case "Quiz" -> DashboardSummaryResponse.RecommendedAction.builder()
                    .title("Attempt your first quiz")
                    .description("Build momentum with a focused quiz session in your preferred track.")
                    .route("/quiz")
                    .build();
            case "Both" -> {
                if ("College/Exam Practice".equals(preference.getGoal())) {
                    yield DashboardSummaryResponse.RecommendedAction.builder()
                            .title("Attempt your first quiz")
                            .description("Start with quizzes, then expand into coding practice from the dashboard.")
                            .route("/quiz")
                            .build();
                }

                yield DashboardSummaryResponse.RecommendedAction.builder()
                        .title("Solve your first coding problem")
                        .description("Start with coding practice, then mix in quizzes from your dashboard checklist.")
                        .route("/problems")
                        .build();
            }
            default -> DashboardSummaryResponse.RecommendedAction.builder()
                    .title("Explore your dashboard")
                    .description("Pick a path and start building progress across RankX.")
                    .route("/home")
                    .build();
        };
    }

    private List<DashboardSummaryResponse.ChecklistItem> buildChecklist(boolean onboardingCompleted) {
        return List.of(
                checklistItem("complete-profile", "Complete profile", "Finish onboarding to unlock personalized guidance.", onboardingCompleted),
                checklistItem("solve-first-problem", "Solve first problem", "Complete your first coding challenge to start building a streak.", false),
                checklistItem("attempt-first-quiz", "Attempt first quiz", "Attempt a quiz to activate quiz-side progress tracking.", false),
                checklistItem("review-first-result", "Review first result", "Open a result detail page and review performance feedback.", false),
                checklistItem("join-study-path", "Join a study path", "Choose a structured practice direction from your dashboard.", false)
        );
    }

    private DashboardSummaryResponse.ChecklistItem checklistItem(
            String key,
            String title,
            String description,
            boolean completed
    ) {
        return DashboardSummaryResponse.ChecklistItem.builder()
                .key(key)
                .title(title)
                .description(description)
                .completed(completed)
                .build();
    }

    private UserPreferenceResponse emptyPreference(UUID userId) {
        return UserPreferenceResponse.builder()
                .userId(userId.toString())
                .goal(null)
                .preferredTrack(null)
                .skillLevel(null)
                .onboardingCompleted(false)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    private UserPreferenceResponse mapToResponse(UserPreference preference) {
        return UserPreferenceResponse.builder()
                .userId(preference.getUserId().toString())
                .goal(preference.getGoal())
                .preferredTrack(preference.getPreferredTrack())
                .skillLevel(preference.getSkillLevel())
                .onboardingCompleted(preference.isOnboardingCompleted())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}
