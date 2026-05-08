package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuizAnalyticsResponse {

    private long totalAttempts;
    private double averagePercentage;
    private double bestPercentage;
    private List<TopicInsightResponse> weakTopics;
    private List<TopicInsightResponse> strongTopics;
}
