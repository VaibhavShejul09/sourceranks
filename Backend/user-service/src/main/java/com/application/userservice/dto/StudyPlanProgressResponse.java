package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudyPlanProgressResponse {

    private Long studyPlanId;
    private String title;
    private Double completionPercentage;
    private Integer totalItems;
    private Integer completedItems;
    private String nextItemTitle;
    private List<ItemProgress> items;

    @Data
    @Builder
    public static class ItemProgress {
        private Long itemId;
        private Integer sequenceNumber;
        private String title;
        private String itemType;
        private String referenceKey;
        private boolean completed;
        private String progressState;
    }
}
