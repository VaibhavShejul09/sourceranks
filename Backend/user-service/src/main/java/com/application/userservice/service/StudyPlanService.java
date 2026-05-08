package com.application.userservice.service;

import com.application.userservice.dto.ActivityProgressUpdateRequest;
import com.application.userservice.dto.ActivityProgressUpdateResponse;
import com.application.userservice.dto.ProgressSummaryResponse;
import com.application.userservice.dto.StudyPlanDetailResponse;
import com.application.userservice.dto.StudyPlanProgressResponse;
import com.application.userservice.dto.StudyPlanResponse;
import com.application.userservice.dto.UserStudyPlanResponse;
import com.application.userservice.entity.*;
import com.application.userservice.repository.StudyPlanRepository;
import com.application.userservice.repository.UserStreakRepository;
import com.application.userservice.repository.UserStudyPlanItemProgressRepository;
import com.application.userservice.repository.UserStudyPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final UserStudyPlanRepository userStudyPlanRepository;
    private final UserStudyPlanItemProgressRepository itemProgressRepository;
    private final UserStreakRepository userStreakRepository;

    public StudyPlanService(
            StudyPlanRepository studyPlanRepository,
            UserStudyPlanRepository userStudyPlanRepository,
            UserStudyPlanItemProgressRepository itemProgressRepository,
            UserStreakRepository userStreakRepository
    ) {
        this.studyPlanRepository = studyPlanRepository;
        this.userStudyPlanRepository = userStudyPlanRepository;
        this.itemProgressRepository = itemProgressRepository;
        this.userStreakRepository = userStreakRepository;
    }

    @Transactional(readOnly = true)
    public List<StudyPlanResponse> getStudyPlans(UUID userId) {
        List<Long> enrolledPlanIds = userStudyPlanRepository.findByUserIdOrderByEnrolledAtDesc(userId)
                .stream()
                .map(userStudyPlan -> userStudyPlan.getStudyPlan().getId())
                .toList();

        return studyPlanRepository.findByActiveTrueOrderByTitleAsc()
                .stream()
                .map(plan -> mapStudyPlan(plan, enrolledPlanIds.contains(plan.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public StudyPlanDetailResponse getStudyPlanDetail(UUID userId, Long studyPlanId) {
        StudyPlan studyPlan = getActiveStudyPlan(studyPlanId);
        UserStudyPlan userStudyPlan = userStudyPlanRepository.findByUserIdAndStudyPlanId(userId, studyPlanId)
                .orElse(null);
        boolean enrolled = userStudyPlan != null;
        Map<Long, Boolean> completionMap = enrolled
                ? buildCompletionMap(userStudyPlan)
                : Map.of();
        Long nextIncompleteItemId = findNextIncompleteItemId(studyPlan.getItems(), completionMap);

        return StudyPlanDetailResponse.builder()
                .id(studyPlan.getId())
                .slug(studyPlan.getSlug())
                .title(studyPlan.getTitle())
                .description(studyPlan.getDescription())
                .track(studyPlan.getTrack())
                .level(studyPlan.getLevel())
                .enrolled(enrolled)
                .items(studyPlan.getItems().stream()
                        .sorted(Comparator.comparing(StudyPlanItem::getSequenceNumber))
                        .map(item -> StudyPlanDetailResponse.StudyPlanItemResponse.builder()
                                .id(item.getId())
                                .sequenceNumber(item.getSequenceNumber())
                                .title(item.getTitle())
                                .description(item.getDescription())
                                .itemType(item.getItemType().name())
                                .referenceKey(item.getReferenceKey())
                                .estimatedMinutes(item.getEstimatedMinutes())
                                .progressState(determineItemState(item, completionMap, nextIncompleteItemId, enrolled))
                                .build())
                        .toList())
                .build();
    }

    public UserStudyPlanResponse enroll(UUID userId, Long studyPlanId) {
        StudyPlan studyPlan = getActiveStudyPlan(studyPlanId);

        if (userStudyPlanRepository.existsByUserIdAndStudyPlanId(userId, studyPlanId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enrolled in this study plan");
        }

        UserStudyPlan userStudyPlan = userStudyPlanRepository.save(UserStudyPlan.builder()
                .userId(userId)
                .studyPlan(studyPlan)
                .completionPercentage(0.0)
                .active(true)
                .build());

        studyPlan.getItems().forEach(item -> itemProgressRepository.save(
                UserStudyPlanItemProgress.builder()
                        .userStudyPlan(userStudyPlan)
                        .studyPlanItem(item)
                        .completed(false)
                        .build()
        ));

        ensureUserStreak(userId);
        return mapUserStudyPlan(userStudyPlan, resolveNextItemTitle(userStudyPlan));
    }

    @Transactional(readOnly = true)
    public List<UserStudyPlanResponse> getUserStudyPlans(UUID userId) {
        return userStudyPlanRepository.findByUserIdOrderByEnrolledAtDesc(userId)
                .stream()
                .map(plan -> mapUserStudyPlan(plan, resolveNextItemTitle(plan)))
                .toList();
    }

    @Transactional(readOnly = true)
    public StudyPlanProgressResponse getStudyPlanProgress(UUID userId, Long studyPlanId) {
        UserStudyPlan userStudyPlan = userStudyPlanRepository.findByUserIdAndStudyPlanId(userId, studyPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study plan enrollment not found"));

        List<UserStudyPlanItemProgress> progressEntries = itemProgressRepository.findByUserStudyPlanId(userStudyPlan.getId());
        Map<Long, UserStudyPlanItemProgress> progressByItemId = progressEntries.stream()
                .collect(Collectors.toMap(entry -> entry.getStudyPlanItem().getId(), Function.identity()));
        Map<Long, Boolean> completionMap = progressEntries.stream()
                .collect(Collectors.toMap(entry -> entry.getStudyPlanItem().getId(), UserStudyPlanItemProgress::isCompleted));

        List<StudyPlanItem> items = userStudyPlan.getStudyPlan().getItems().stream()
                .sorted(Comparator.comparing(StudyPlanItem::getSequenceNumber))
                .toList();

        int completedItems = (int) progressEntries.stream().filter(UserStudyPlanItemProgress::isCompleted).count();
        String nextItemTitle = resolveNextItemTitle(userStudyPlan);
        Long nextIncompleteItemId = findNextIncompleteItemId(items, completionMap);

        List<StudyPlanProgressResponse.ItemProgress> itemResponses = items.stream()
                .map(item -> {
                    UserStudyPlanItemProgress progress = progressByItemId.get(item.getId());
                    return StudyPlanProgressResponse.ItemProgress.builder()
                            .itemId(item.getId())
                            .sequenceNumber(item.getSequenceNumber())
                            .title(item.getTitle())
                            .itemType(item.getItemType().name())
                            .referenceKey(item.getReferenceKey())
                            .completed(progress != null && progress.isCompleted())
                            .progressState(determineItemState(item, completionMap, nextIncompleteItemId, true))
                            .build();
                })
                .toList();

        return StudyPlanProgressResponse.builder()
                .studyPlanId(userStudyPlan.getStudyPlan().getId())
                .title(userStudyPlan.getStudyPlan().getTitle())
                .completionPercentage(calculateCompletionPercentage(items.size(), completedItems))
                .totalItems(items.size())
                .completedItems(completedItems)
                .nextItemTitle(nextItemTitle)
                .items(itemResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public ProgressSummaryResponse getProgressSummary(UUID userId) {
        List<UserStudyPlan> enrolledPlans = userStudyPlanRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        UserStudyPlan currentPlan = enrolledPlans.stream().findFirst().orElse(null);

        return ProgressSummaryResponse.builder()
                .enrolledPlans(enrolledPlans.size())
                .streakCount(userStreakRepository.findByUserId(userId)
                        .map(UserStreak::getCurrentStreak)
                        .orElse(0))
                .currentPlan(currentPlan == null ? null : ProgressSummaryResponse.CurrentPlan.builder()
                        .studyPlanId(currentPlan.getStudyPlan().getId())
                        .title(currentPlan.getStudyPlan().getTitle())
                        .completionPercentage(currentPlan.getCompletionPercentage())
                        .nextItemTitle(resolveNextItemTitle(currentPlan))
                        .build())
                .build();
    }

    public ActivityProgressUpdateResponse markActivityCompleted(UUID userId, ActivityProgressUpdateRequest request) {
        String normalizedItemType = normalizeItemType(request.getItemType());
        String normalizedReferenceKey = request.getReferenceKey().trim();
        List<UserStudyPlan> activePlans = userStudyPlanRepository.findByUserIdAndActiveTrueOrderByEnrolledAtDesc(userId);

        int affectedPlans = 0;
        int completedItems = 0;

        for (UserStudyPlan userStudyPlan : activePlans) {
            Map<Long, UserStudyPlanItemProgress> progressByItemId = itemProgressRepository
                    .findByUserStudyPlanId(userStudyPlan.getId())
                    .stream()
                    .collect(Collectors.toMap(progress -> progress.getStudyPlanItem().getId(), Function.identity()));

            boolean planChanged = false;
            for (StudyPlanItem item : userStudyPlan.getStudyPlan().getItems()) {
                if (!item.getItemType().name().equals(normalizedItemType)) {
                    continue;
                }
                if (!normalizedReferenceKey.equals(item.getReferenceKey())) {
                    continue;
                }

                UserStudyPlanItemProgress progress = progressByItemId.get(item.getId());
                if (progress != null && !progress.isCompleted()) {
                    progress.setCompleted(true);
                    itemProgressRepository.save(progress);
                    completedItems++;
                    planChanged = true;
                }
            }

            if (planChanged) {
                updateCompletionPercentage(userStudyPlan, progressByItemId.values().stream().toList());
                userStudyPlanRepository.save(userStudyPlan);
                affectedPlans++;
            }
        }

        if (affectedPlans > 0) {
            log.info(
                    "Progress updated for user {} via {} event {} on reference {} | plans={}, items={}",
                    userId,
                    normalizedItemType,
                    request.getSourceEventId(),
                    normalizedReferenceKey,
                    affectedPlans,
                    completedItems
            );
        } else {
            log.debug(
                    "No study-plan progress change for user {} via {} event {} on reference {}",
                    userId,
                    normalizedItemType,
                    request.getSourceEventId(),
                    normalizedReferenceKey
            );
        }

        return ActivityProgressUpdateResponse.builder()
                .progressChanged(completedItems > 0)
                .affectedStudyPlans(affectedPlans)
                .completedItems(completedItems)
                .build();
    }

    private StudyPlan getActiveStudyPlan(Long studyPlanId) {
        return studyPlanRepository.findByIdAndActiveTrue(studyPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study plan not found"));
    }

    private StudyPlanResponse mapStudyPlan(StudyPlan studyPlan, boolean enrolled) {
        return StudyPlanResponse.builder()
                .id(studyPlan.getId())
                .slug(studyPlan.getSlug())
                .title(studyPlan.getTitle())
                .description(studyPlan.getDescription())
                .track(studyPlan.getTrack())
                .level(studyPlan.getLevel())
                .totalItems(studyPlan.getItems().size())
                .enrolled(enrolled)
                .build();
    }

    private UserStudyPlanResponse mapUserStudyPlan(UserStudyPlan userStudyPlan, String nextItemTitle) {
        return UserStudyPlanResponse.builder()
                .studyPlanId(userStudyPlan.getStudyPlan().getId())
                .title(userStudyPlan.getStudyPlan().getTitle())
                .track(userStudyPlan.getStudyPlan().getTrack())
                .level(userStudyPlan.getStudyPlan().getLevel())
                .enrolledAt(userStudyPlan.getEnrolledAt())
                .completionPercentage(userStudyPlan.getCompletionPercentage())
                .nextItemTitle(nextItemTitle)
                .build();
    }

    private Map<Long, Boolean> buildCompletionMap(UserStudyPlan userStudyPlan) {
        return itemProgressRepository.findByUserStudyPlanId(userStudyPlan.getId())
                .stream()
                .collect(Collectors.toMap(progress -> progress.getStudyPlanItem().getId(), UserStudyPlanItemProgress::isCompleted));
    }

    private String resolveNextItemTitle(UserStudyPlan userStudyPlan) {
        Map<Long, Boolean> completionMap = buildCompletionMap(userStudyPlan);

        return userStudyPlan.getStudyPlan().getItems().stream()
                .sorted(Comparator.comparing(StudyPlanItem::getSequenceNumber))
                .filter(item -> !completionMap.getOrDefault(item.getId(), false))
                .map(StudyPlanItem::getTitle)
                .findFirst()
                .orElse("Plan completed");
    }

    private Long findNextIncompleteItemId(List<StudyPlanItem> items, Map<Long, Boolean> completionMap) {
        return items.stream()
                .sorted(Comparator.comparing(StudyPlanItem::getSequenceNumber))
                .filter(item -> !completionMap.getOrDefault(item.getId(), false))
                .map(StudyPlanItem::getId)
                .findFirst()
                .orElse(null);
    }

    private String determineItemState(
            StudyPlanItem item,
            Map<Long, Boolean> completionMap,
            Long nextIncompleteItemId,
            boolean enrolled
    ) {
        if (completionMap.getOrDefault(item.getId(), false)) {
            return "COMPLETED";
        }
        if (!enrolled) {
            return item.getSequenceNumber() == 1 ? "NEXT" : "LOCKED";
        }
        if (Objects.equals(item.getId(), nextIncompleteItemId)) {
            return "NEXT";
        }
        return "LOCKED";
    }

    private void updateCompletionPercentage(UserStudyPlan userStudyPlan, List<UserStudyPlanItemProgress> progressEntries) {
        int totalItems = userStudyPlan.getStudyPlan().getItems().size();
        int completedItems = (int) progressEntries.stream()
                .filter(UserStudyPlanItemProgress::isCompleted)
                .count();
        userStudyPlan.setCompletionPercentage(calculateCompletionPercentage(totalItems, completedItems));
    }

    private String normalizeItemType(String itemType) {
        if (itemType == null || itemType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item type is required");
        }
        try {
            return StudyPlanItemType.valueOf(itemType.trim().toUpperCase()).name();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported item type");
        }
    }

    private void ensureUserStreak(UUID userId) {
        userStreakRepository.findByUserId(userId).orElseGet(() ->
                userStreakRepository.save(UserStreak.builder()
                        .userId(userId)
                        .currentStreak(1)
                        .longestStreak(1)
                        .lastActivityDate(LocalDate.now())
                        .build())
        );
    }

    private double calculateCompletionPercentage(int totalItems, int completedItems) {
        if (totalItems == 0) {
            return 0.0;
        }
        return (completedItems * 100.0) / totalItems;
    }
}
