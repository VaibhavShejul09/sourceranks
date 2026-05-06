package com.application.questionservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuestionAnswerAdminResponse {
    private UUID id;
    private String questionText;
    private List<String> options;
    private String correctOption;
}
