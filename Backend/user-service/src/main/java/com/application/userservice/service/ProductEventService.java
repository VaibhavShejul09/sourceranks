package com.application.userservice.service;

import com.application.userservice.dto.AdminKpiDashboardResponse;
import com.application.userservice.dto.ProductEventRequest;
import com.application.userservice.dto.ProductEventResponse;
import com.application.userservice.entity.ProductEvent;
import com.application.userservice.repository.ProductEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventService {

    private static final String CATEGORY_AUTH = "AUTH";
    private static final String CATEGORY_ONBOARDING = "ONBOARDING";
    private static final String CATEGORY_CODING = "CODING";
    private static final String CATEGORY_QUIZ = "QUIZ";
    private static final String CATEGORY_PROGRESS = "PROGRESS";
    private static final String CONTENT_PROBLEM = "PROBLEM";
    private static final String CONTENT_QUIZ = "QUIZ";
    private static final String CONTENT_QUESTION = "QUESTION";

    private final ProductEventRepository productEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProductEventResponse ingest(UUID userId, String role, ProductEventRequest request) {
        ProductEvent event = ProductEvent.builder()
                .eventName(normalize(request.getEventName()))
                .eventCategory(normalize(request.getEventCategory()))
                .source(normalizeNullable(request.getSource()))
                .track(normalizeNullable(request.getTrack()))
                .userId(userId)
                .role(role)
                .contentType(normalizeNullable(request.getContentType()))
                .contentId(request.getContentId())
                .contentTitle(request.getContentTitle())
                .parentContentId(request.getParentContentId())
                .topic(request.getTopic())
                .outcome(normalizeNullable(request.getOutcome()))
                .numericValue(request.getNumericValue())
                .metadataJson(serializeMetadata(request.getMetadata()))
                .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
                .recordedAt(LocalDateTime.now())
                .build();

        ProductEvent saved = productEventRepository.save(event);
        return ProductEventResponse.builder()
                .id(saved.getId())
                .eventName(saved.getEventName())
                .eventCategory(saved.getEventCategory())
                .accepted(true)
                .occurredAt(saved.getOccurredAt())
                .build();
    }

    @Transactional(readOnly = true)
    public AdminKpiDashboardResponse getKpiDashboard() {
        List<ProductEvent> events = productEventRepository.findAll();
        Set<UUID> trackedUsers = distinctUsers(events);
        Set<UUID> activeUsers = distinctUsers(
                events.stream()
                        .filter(event -> event.getOccurredAt() != null && event.getOccurredAt().isAfter(LocalDateTime.now().minusDays(7)))
                        .toList()
        );

        long loginEvents = countByName(events, "AUTH_LOGIN_SUCCESS");
        long onboardingCompletions = countByName(events, "ONBOARDING_COMPLETED");
        long codingEvents = countByCategory(events, CATEGORY_CODING);
        long quizEvents = countByCategory(events, CATEGORY_QUIZ);
        long progressEvents = countByCategory(events, CATEGORY_PROGRESS);

        double activationRate = trackedUsers.isEmpty() ? 0.0 : round((onboardingCompletions * 100.0) / trackedUsers.size());
        long engagedUsers = events.stream()
                .filter(event -> Set.of(CATEGORY_CODING, CATEGORY_QUIZ, CATEGORY_PROGRESS).contains(event.getEventCategory()))
                .filter(event -> event.getUserId() != null)
                .collect(Collectors.groupingBy(ProductEvent::getUserId, Collectors.counting()))
                .values()
                .stream()
                .filter(count -> count >= 3)
                .count();
        double engagementRate = trackedUsers.isEmpty() ? 0.0 : round((engagedUsers * 100.0) / trackedUsers.size());
        double averageEventsPerTrackedUser = trackedUsers.isEmpty() ? 0.0 : round(events.size() / (double) trackedUsers.size());

        return AdminKpiDashboardResponse.builder()
                .totalTrackedUsers(trackedUsers.size())
                .activeUsersLast7Days(activeUsers.size())
                .totalEvents(events.size())
                .loginEvents(loginEvents)
                .onboardingCompletions(onboardingCompletions)
                .codingEvents(codingEvents)
                .quizEvents(quizEvents)
                .progressEvents(progressEvents)
                .activationRate(activationRate)
                .engagementRate(engagementRate)
                .averageEventsPerTrackedUser(averageEventsPerTrackedUser)
                .highlights(List.of(
                        AdminKpiDashboardResponse.KpiCard.builder()
                                .title("Activation")
                                .description("Users who completed onboarding after entering the product.")
                                .valueLabel(activationRate + "%")
                                .tone(activationRate >= 60.0 ? "positive" : "attention")
                                .build(),
                        AdminKpiDashboardResponse.KpiCard.builder()
                                .title("Engagement")
                                .description("Tracked users with meaningful activity across coding, quiz, or progress flows.")
                                .valueLabel(engagementRate + "%")
                                .tone(engagementRate >= 40.0 ? "positive" : "neutral")
                                .build(),
                        AdminKpiDashboardResponse.KpiCard.builder()
                                .title("Weekly active users")
                                .description("Distinct tracked users active in the last seven days.")
                                .valueLabel(String.valueOf(activeUsers.size()))
                                .tone("neutral")
                                .build()
                ))
                .build();
    }

    @Transactional(readOnly = true)
    public AdminKpiDashboardResponse.ContentAnalyticsResponse getProblemAnalytics() {
        return buildContentAnalytics(CONTENT_PROBLEM);
    }

    @Transactional(readOnly = true)
    public AdminKpiDashboardResponse.ContentAnalyticsResponse getQuizAnalytics() {
        return buildContentAnalytics(CONTENT_QUIZ);
    }

    @Transactional(readOnly = true)
    public AdminKpiDashboardResponse.ContentAnalyticsResponse getQuestionAnalytics() {
        return buildContentAnalytics(CONTENT_QUESTION);
    }

    private AdminKpiDashboardResponse.ContentAnalyticsResponse buildContentAnalytics(String contentType) {
        List<ProductEvent> allEvents = productEventRepository.findAll();
        List<ProductEvent> events = allEvents.stream()
                .filter(event -> contentType.equalsIgnoreCase(event.getContentType()))
                .filter(event -> event.getContentId() != null && !event.getContentId().isBlank())
                .toList();

        Map<String, ContentAccumulator> accumulators = new LinkedHashMap<>();
        Map<String, Long> quizStartsByQuizId = allEvents.stream()
                .filter(event -> "QUIZ_ATTEMPT_STARTED".equalsIgnoreCase(event.getEventName()))
                .filter(event -> event.getContentId() != null)
                .collect(Collectors.groupingBy(ProductEvent::getContentId, Collectors.counting()));

        for (ProductEvent event : events) {
            ContentAccumulator accumulator = accumulators.computeIfAbsent(event.getContentId(), ignored ->
                    new ContentAccumulator(
                            event.getContentId(),
                            defaultString(event.getContentTitle(), contentType + " " + event.getContentId()),
                            event.getTopic()
                    )
            );

            accumulator.attemptCount++;
            if (event.getUserId() != null) {
                accumulator.uniqueUsers.add(event.getUserId());
            }

            if ("ACCEPTED".equalsIgnoreCase(event.getOutcome())
                    || "COMPLETED".equalsIgnoreCase(event.getOutcome())
                    || "ANSWERED".equalsIgnoreCase(event.getOutcome())
                    || "SUBMITTED".equalsIgnoreCase(event.getOutcome())) {
                accumulator.successCount++;
            }

            if (CONTENT_QUESTION.equals(contentType) && event.getParentContentId() != null) {
                accumulator.parentContentId = event.getParentContentId();
            }
        }

        List<AdminKpiDashboardResponse.ContentAnalyticsItem> items = accumulators.values().stream()
                .map(accumulator -> toContentItem(contentType, accumulator, quizStartsByQuizId))
                .sorted(Comparator.comparingLong(AdminKpiDashboardResponse.ContentAnalyticsItem::getAttemptCount).reversed())
                .toList();

        AdminKpiDashboardResponse.ContentAnalyticsItem mostAttempted = items.stream().findFirst().orElse(null);
        AdminKpiDashboardResponse.ContentAnalyticsItem leastAttempted = items.stream()
                .min(Comparator.comparingLong(AdminKpiDashboardResponse.ContentAnalyticsItem::getAttemptCount))
                .orElse(null);

        return AdminKpiDashboardResponse.ContentAnalyticsResponse.builder()
                .contentType(contentType)
                .totalTrackedItems(items.size())
                .averageSolveRate(average(items.stream().map(AdminKpiDashboardResponse.ContentAnalyticsItem::getSolveRate).toList()))
                .averageAcceptanceRate(average(items.stream().map(AdminKpiDashboardResponse.ContentAnalyticsItem::getAcceptanceRate).toList()))
                .averageCompletionRate(average(items.stream().map(AdminKpiDashboardResponse.ContentAnalyticsItem::getCompletionRate).toList()))
                .mostAttempted(mostAttempted)
                .leastAttempted(leastAttempted)
                .items(items)
                .build();
    }

    private AdminKpiDashboardResponse.ContentAnalyticsItem toContentItem(
            String contentType,
            ContentAccumulator accumulator,
            Map<String, Long> quizStartsByQuizId
    ) {
        long uniqueUsers = accumulator.uniqueUsers.size();
        double acceptanceRate = accumulator.attemptCount == 0 ? 0.0 : round((accumulator.successCount * 100.0) / accumulator.attemptCount);
        double solveRate = uniqueUsers == 0 ? 0.0 : round((Math.min(accumulator.successCount, uniqueUsers) * 100.0) / uniqueUsers);
        double completionRate;

        if (CONTENT_QUIZ.equals(contentType)) {
            completionRate = accumulator.attemptCount == 0 ? 0.0 : acceptanceRate;
        } else if (CONTENT_QUESTION.equals(contentType) && accumulator.parentContentId != null) {
            long starts = quizStartsByQuizId.getOrDefault(accumulator.parentContentId, 0L);
            completionRate = starts == 0 ? 0.0 : round((accumulator.attemptCount * 100.0) / starts);
        } else {
            completionRate = accumulator.attemptCount == 0 ? 0.0 : 100.0;
        }

        return AdminKpiDashboardResponse.ContentAnalyticsItem.builder()
                .contentId(accumulator.contentId)
                .contentTitle(accumulator.contentTitle)
                .topic(accumulator.topic)
                .attemptCount(accumulator.attemptCount)
                .uniqueUserCount(uniqueUsers)
                .successCount(accumulator.successCount)
                .solveRate(solveRate)
                .acceptanceRate(acceptanceRate)
                .completionRate(round(Math.min(completionRate, 100.0)))
                .build();
    }

    private String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException exception) {
            log.warn("Failed to serialize analytics event metadata", exception);
            return null;
        }
    }

    private Set<UUID> distinctUsers(List<ProductEvent> events) {
        return events.stream()
                .map(ProductEvent::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private long countByName(List<ProductEvent> events, String name) {
        return events.stream()
                .filter(event -> name.equalsIgnoreCase(event.getEventName()))
                .count();
    }

    private long countByCategory(List<ProductEvent> events, String category) {
        return events.stream()
                .filter(event -> category.equalsIgnoreCase(event.getEventCategory()))
                .count();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return normalize(value);
    }

    private double average(List<Double> values) {
        return values.isEmpty() ? 0.0 : round(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static final class ContentAccumulator {
        private final String contentId;
        private final String contentTitle;
        private final String topic;
        private final Set<UUID> uniqueUsers = new java.util.HashSet<>();
        private long attemptCount;
        private long successCount;
        private String parentContentId;

        private ContentAccumulator(String contentId, String contentTitle, String topic) {
            this.contentId = contentId;
            this.contentTitle = contentTitle;
            this.topic = topic;
        }
    }
}
