package com.application.questionservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class QuestionRequest {

    private UUID quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
}

