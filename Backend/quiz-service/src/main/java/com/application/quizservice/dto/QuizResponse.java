package com.application.quizservice.dto;

import com.application.quizservice.entity.DifficultyLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuizResponse {

    private UUID id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String status;
    private LocalDateTime createdAt;
    private String category;
    private String subCategory;
    private DifficultyLevel difficulty;
}
