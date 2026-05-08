package com.application.userservice.service;

import com.application.userservice.client.ProblemServiceClient;
import com.application.userservice.client.QuizServiceClient;
import com.application.userservice.client.ResultServiceClient;
import com.application.userservice.client.SubmissionServiceClient;
import com.application.userservice.dto.ActivityAnalyticsResponse;
import com.application.userservice.dto.CodingAnalyticsResponse;
import com.application.userservice.dto.ProblemMetadataView;
import com.application.userservice.dto.ProgressSummaryResponse;
import com.application.userservice.dto.QuizAnalyticsResponse;
import com.application.userservice.dto.QuizMetadataView;
import com.application.userservice.dto.RecommendationCardResponse;
import com.application.userservice.dto.ResultAnalyticsView;
import com.application.userservice.dto.SubmissionAnalyticsView;
import com.application.userservice.dto.TopicInsightResponse;
import com.application.userservice.dto.UserAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnalyticsService {

    private final SubmissionServiceClient submissionServiceClient;
    private final ResultServiceClient resultServiceClient;
    private final ProblemServiceClient problemServiceClient;
    private final QuizServiceClient quizServiceClient;
    private final UserPreferenceService userPreferenceService;
    private final StudyPlanService studyPlanService;

    public UserAnalyticsResponse getAnalytics(UUID userId, String role) {
        List<SubmissionAnalyticsView> submissions = getSubmissions(userId);
        List<ResultAnalyticsView> results = getResults(userId, role);

        CodingAnalyticsResponse coding = buildCodingAnalytics(submissions);
        QuizAnalyticsResponse quiz = buildQuizAnalytics(results, userId, role);
        ActivityAnalyticsResponse activity = buildActivityAnalytics(submissions, results, userId);
        List<RecommendationCardResponse> recommendations = buildRecommendations(userId, role, coding, quiz, activity);

        return UserAnalyticsResponse.builder()
                .codingPerformance(coding)
                .quizPerformance(quiz)
                .activitySummary(activity)
                .recommendations(recommendations)
                .build();
    }

    public List<RecommendationCardResponse> getDashboardRecommendations(UUID userId, String role) {
        UserAnalyticsResponse analytics = getAnalytics(userId, role);
        return analytics.getRecommendations();
    }

    private List<SubmissionAnalyticsView> getSubmissions(UUID userId) {
        try {
            return submissionServiceClient.getSubmissionHistory(userId.toString());
        } catch (Exception ex) {
            log.warn("Failed to fetch submission analytics for user {}", userId, ex);
            return List.of();
        }
    }

    private List<ResultAnalyticsView> getResults(UUID userId, String role) {
        try {
            return resultServiceClient.getResults(userId.toString(), "ROLE_USER");
        } catch (Exception ex) {
            log.warn("Failed to fetch quiz analytics for user {}", userId, ex);
            return List.of();
        }
    }

    private CodingAnalyticsResponse buildCodingAnalytics(List<SubmissionAnalyticsView> submissions) {
        long accepted = submissions.stream()
                .filter(submission -> "ACCEPTED".equalsIgnoreCase(submission.getStatus()))
                .count();

        Double avgRuntime = submissions.stream()
                .map(SubmissionAnalyticsView::getRuntimeMs)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(Double.NaN);

        Double avgMemory = submissions.stream()
                .map(SubmissionAnalyticsView::getMemoryKb)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(Double.NaN);

        List<TopicInsightResponse> topics = buildCodingTopics(submissions);

        return CodingAnalyticsResponse.builder()
                .totalSubmissions(submissions.size())
                .acceptedSubmissions(accepted)
                .acceptanceRate(submissions.isEmpty() ? 0.0 : round((accepted * 100.0) / submissions.size()))
                .averageRuntimeMs(Double.isNaN(avgRuntime) ? null : round(avgRuntime))
                .averageMemoryKb(Double.isNaN(avgMemory) ? null : round(avgMemory))
                .weakTopics(selectTopics(topics, "WEAK"))
                .strongTopics(selectTopics(topics, "STRONG"))
                .build();
    }

    private QuizAnalyticsResponse buildQuizAnalytics(List<ResultAnalyticsView> results, UUID userId, String role) {
        double avgPercentage = results.stream()
                .map(ResultAnalyticsView::getPercentage)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double bestPercentage = results.stream()
                .map(ResultAnalyticsView::getPercentage)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        List<TopicInsightResponse> topics = buildQuizTopics(results, userId, role);

        return QuizAnalyticsResponse.builder()
                .totalAttempts(results.size())
                .averagePercentage(round(avgPercentage))
                .bestPercentage(round(bestPercentage))
                .weakTopics(selectTopics(topics, "WEAK"))
                .strongTopics(selectTopics(topics, "STRONG"))
                .build();
    }

    private ActivityAnalyticsResponse buildActivityAnalytics(
            List<SubmissionAnalyticsView> submissions,
            List<ResultAnalyticsView> results,
            UUID userId
    ) {
        ProgressSummaryResponse progressSummary = studyPlanService.getProgressSummary(userId);
        LocalDateTime latestCoding = submissions.stream()
                .map(SubmissionAnalyticsView::getCreatedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return ActivityAnalyticsResponse.builder()
                .totalCodingActivities(submissions.size())
                .totalQuizActivities(results.size())
                .enrolledStudyPlans(progressSummary.getEnrolledPlans())
                .streakCount(progressSummary.getStreakCount())
                .latestCodingActivityAt(latestCoding)
                .latestQuizActivityAt(null)
                .build();
    }

    private List<RecommendationCardResponse> buildRecommendations(
            UUID userId,
            String role,
            CodingAnalyticsResponse coding,
            QuizAnalyticsResponse quiz,
            ActivityAnalyticsResponse activity
    ) {
        var preferences = userPreferenceService.getPreferences(userId);
        ProgressSummaryResponse progressSummary = studyPlanService.getProgressSummary(userId);
        List<RecommendationCardResponse> recommendations = new ArrayList<>();

        if (activity.getEnrolledStudyPlans() == 0) {
            recommendations.add(RecommendationCardResponse.builder()
                    .title("Join a study plan")
                    .description("Structured plans unlock guided practice and clearer daily progress.")
                    .route("/study-plans")
                    .reason("No active plan yet")
                    .priority("HIGH")
                    .build());
        }

        if (progressSummary.getCurrentPlan() != null && progressSummary.getCurrentPlan().getNextItemTitle() != null) {
            recommendations.add(RecommendationCardResponse.builder()
                    .title("Continue your current plan")
                    .description("Pick up with " + progressSummary.getCurrentPlan().getNextItemTitle() + " and keep your momentum.")
                    .route("/my-progress")
                    .reason("Active study plan detected")
                    .priority("HIGH")
                    .build());
        }

        if ("Coding".equalsIgnoreCase(preferences.getPreferredTrack()) || "Both".equalsIgnoreCase(preferences.getPreferredTrack())) {
            TopicInsightResponse weakCodingTopic = coding.getWeakTopics().stream().findFirst().orElse(null);
            if (weakCodingTopic != null) {
                recommendations.add(RecommendationCardResponse.builder()
                        .title("Strengthen " + weakCodingTopic.getTopic())
                        .description("Your coding acceptance rate is lowest here. Revisit this topic with one focused problem.")
                        .route("/problems")
                        .reason("Weak coding topic")
                        .priority("MEDIUM")
                        .build());
            } else {
                recommendations.add(RecommendationCardResponse.builder()
                        .title("Solve a coding problem")
                        .description("Build consistency by completing one problem today.")
                        .route("/problems")
                        .reason("Coding track preference")
                        .priority("MEDIUM")
                        .build());
            }
        }

        if ("Quiz".equalsIgnoreCase(preferences.getPreferredTrack()) || "Both".equalsIgnoreCase(preferences.getPreferredTrack())) {
            TopicInsightResponse weakQuizTopic = quiz.getWeakTopics().stream().findFirst().orElse(null);
            if (weakQuizTopic != null) {
                recommendations.add(RecommendationCardResponse.builder()
                        .title("Review " + weakQuizTopic.getTopic())
                        .description("Your quiz performance is weaker in this area. Take another focused quiz attempt.")
                        .route("/quiz")
                        .reason("Weak quiz topic")
                        .priority("MEDIUM")
                        .build());
            } else {
                recommendations.add(RecommendationCardResponse.builder()
                        .title("Attempt a quiz")
                        .description("Keep recall fresh with a short quiz session.")
                        .route("/quiz")
                        .reason("Quiz track preference")
                        .priority("LOW")
                        .build());
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add(RecommendationCardResponse.builder()
                    .title("Open your dashboard")
                    .description("You are in a healthy learning state. Continue your next best activity.")
                    .route("/home")
                    .reason("Balanced activity")
                    .priority("LOW")
                    .build());
        }

        return recommendations.stream().limit(3).toList();
    }

    private List<TopicInsightResponse> buildCodingTopics(List<SubmissionAnalyticsView> submissions) {
        Map<String, TopicAccumulator> topicMap = new LinkedHashMap<>();

        for (SubmissionAnalyticsView submission : submissions) {
            ProblemMetadataView metadata = fetchProblemMetadata(submission.getProblemId());
            String topic = resolveCodingTopic(metadata);
            TopicAccumulator accumulator = topicMap.computeIfAbsent(topic, ignored -> new TopicAccumulator("Coding"));
            accumulator.attempts++;
            if ("ACCEPTED".equalsIgnoreCase(submission.getStatus())) {
                accumulator.successes++;
            }
        }

        return toTopicInsights(topicMap);
    }

    private List<TopicInsightResponse> buildQuizTopics(List<ResultAnalyticsView> results, UUID userId, String role) {
        Map<String, TopicAccumulator> topicMap = new LinkedHashMap<>();

        for (ResultAnalyticsView result : results) {
            QuizMetadataView metadata = fetchQuizMetadata(result.getQuizId(), userId, role);
            String topic = resolveQuizTopic(metadata);
            TopicAccumulator accumulator = topicMap.computeIfAbsent(topic, ignored -> new TopicAccumulator("Quiz"));
            accumulator.attempts++;
            if (result.getPercentage() != null && result.getPercentage() >= 60.0) {
                accumulator.successes++;
            }
        }

        return toTopicInsights(topicMap);
    }

    private List<TopicInsightResponse> toTopicInsights(Map<String, TopicAccumulator> topicMap) {
        return topicMap.entrySet().stream()
                .map(entry -> {
                    TopicAccumulator accumulator = entry.getValue();
                    double successRate = accumulator.attempts == 0
                            ? 0.0
                            : round((accumulator.successes * 100.0) / accumulator.attempts);
                    return TopicInsightResponse.builder()
                            .topic(entry.getKey())
                            .track(accumulator.track)
                            .attempts(accumulator.attempts)
                            .successRate(successRate)
                            .classification(classifyTopic(accumulator.attempts, successRate))
                            .build();
                })
                .sorted(Comparator.comparing(TopicInsightResponse::getSuccessRate))
                .toList();
    }

    private List<TopicInsightResponse> selectTopics(List<TopicInsightResponse> topics, String classification) {
        return topics.stream()
                .filter(topic -> classification.equalsIgnoreCase(topic.getClassification()))
                .limit(3)
                .toList();
    }

    private ProblemMetadataView fetchProblemMetadata(Long problemId) {
        try {
            return problemServiceClient.getProblemById(problemId);
        } catch (Exception ex) {
            log.debug("Problem metadata unavailable for {}", problemId, ex);
            return null;
        }
    }

    private QuizMetadataView fetchQuizMetadata(UUID quizId, UUID userId, String role) {
        try {
            return quizServiceClient.getQuizById(quizId, userId.toString(), "ROLE_USER");
        } catch (Exception ex) {
            log.debug("Quiz metadata unavailable for {}", quizId, ex);
            return null;
        }
    }

    private String resolveCodingTopic(ProblemMetadataView metadata) {
        if (metadata != null && metadata.getTags() != null && !metadata.getTags().isEmpty()) {
            return metadata.getTags().getFirst();
        }
        return "Coding Fundamentals";
    }

    private String resolveQuizTopic(QuizMetadataView metadata) {
        if (metadata == null) {
            return "Quiz Practice";
        }
        if (metadata.getSubCategory() != null && !metadata.getSubCategory().isBlank()) {
            return metadata.getSubCategory();
        }
        if (metadata.getCategory() != null && !metadata.getCategory().isBlank()) {
            return metadata.getCategory();
        }
        return "Quiz Practice";
    }

    private String classifyTopic(long attempts, double successRate) {
        if (attempts == 0) {
            return "NEUTRAL";
        }
        if (attempts >= 2 && successRate >= 75.0) {
            return "STRONG";
        }
        if (successRate < 60.0) {
            return "WEAK";
        }
        return "NEUTRAL";
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static final class TopicAccumulator {
        private final String track;
        private long attempts;
        private long successes;

        private TopicAccumulator(String track) {
            this.track = track;
        }
    }
}
