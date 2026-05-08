package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CodingAnalyticsResponse {

    private long totalSubmissions;
    private long acceptedSubmissions;
    private double acceptanceRate;
    private Double averageRuntimeMs;
    private Double averageMemoryKb;
    private List<TopicInsightResponse> weakTopics;
    private List<TopicInsightResponse> strongTopics;
}
