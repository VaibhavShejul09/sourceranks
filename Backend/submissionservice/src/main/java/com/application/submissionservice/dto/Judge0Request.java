package com.application.submissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Judge0Request {
    private String source_code;
    private String stdin;
    private Integer language_id;
}

