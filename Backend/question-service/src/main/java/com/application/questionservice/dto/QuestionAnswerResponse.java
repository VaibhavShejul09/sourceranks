package com.application.questionservice.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class QuestionAnswerResponse {

    private UUID questionId;
    private String correctOption;
}
