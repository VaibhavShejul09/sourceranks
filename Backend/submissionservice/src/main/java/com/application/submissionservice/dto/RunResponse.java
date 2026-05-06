package com.application.submissionservice.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunResponse {

    private List<RunTestCaseResult> results = new ArrayList<>();

    public void addResult(String input,
                          String expectedOutput,
                          String actualOutput,
                          boolean passed) {

        results.add(RunTestCaseResult.builder()
                .input(input)
                .expectedOutput(expectedOutput)
                .actualOutput(actualOutput)
                .passed(passed)
                .build());
    }
}
