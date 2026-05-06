package com.application.submissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunRequest {
    private Long problemId;
    private Long userId;
    private String languageKey;
    private String sourceCode;
}
