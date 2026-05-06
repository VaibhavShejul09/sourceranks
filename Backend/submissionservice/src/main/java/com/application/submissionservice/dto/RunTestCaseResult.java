package com.application.submissionservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunTestCaseResult {

    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
}
