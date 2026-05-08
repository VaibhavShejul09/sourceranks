package com.application.userservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResultAnalyticsView {

    private UUID attemptId;
    private UUID quizId;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
}
