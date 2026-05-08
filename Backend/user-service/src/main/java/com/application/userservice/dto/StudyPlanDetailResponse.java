package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudyPlanDetailResponse {

    private Long id;
    private String slug;
    private String title;
    private String description;
    private String track;
    private String level;
    private boolean enrolled;
    private List<StudyPlanItemResponse> items;

    @Data
    @Builder
    public static class StudyPlanItemResponse {
        private Long id;
        private Integer sequenceNumber;
        private String title;
        private String description;
        private String itemType;
        private String referenceKey;
        private Integer estimatedMinutes;
        private String progressState;
    }
}
