package com.application.resultservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class QuizMetadataResponse {

    private UUID id;
    private String title;
    private String category;
    private String subCategory;
    private String difficulty;
}
