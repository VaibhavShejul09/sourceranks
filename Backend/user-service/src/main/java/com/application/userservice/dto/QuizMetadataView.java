package com.application.userservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class QuizMetadataView {

    private UUID id;
    private String title;
    private String category;
    private String subCategory;
    private String difficulty;
}
