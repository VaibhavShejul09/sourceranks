package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyPlanResponse {

    private Long id;
    private String slug;
    private String title;
    private String description;
    private String track;
    private String level;
    private Integer totalItems;
    private boolean enrolled;
}
