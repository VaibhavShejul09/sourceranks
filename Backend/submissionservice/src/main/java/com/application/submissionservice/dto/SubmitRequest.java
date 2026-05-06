package com.application.submissionservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRequest {
    private Long problemId;
    private Long userId;
    private String languageKey;
    private String sourceCode;
}
