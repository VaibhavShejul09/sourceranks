package com.application.resultservice.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class QuestionAnswer {

    private UUID questionId;
    private String correctOption;
}
