package com.application.submissionservice.dto;


import java.util.Map;

public record Judge0Result(
        String stdout,
        String stderr,
        String compile_output,
        String time,
        Integer memory,
        Map<String, Object> status
) {}
