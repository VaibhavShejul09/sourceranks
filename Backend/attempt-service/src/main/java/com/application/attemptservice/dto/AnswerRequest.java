package com.application.attemptservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class AnswerRequest {

    @NotNull
    private UUID questionId;

    @NotBlank
    @Pattern(regexp = "[A-D]")
    private String selectedOption;
}
