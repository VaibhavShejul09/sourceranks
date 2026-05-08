package com.application.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicInsightResponse {

    private String topic;
    private String track;
    private long attempts;
    private double successRate;
    private String classification;
}
