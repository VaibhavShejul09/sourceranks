package com.application.submissionservice.controller;

import com.application.submissionservice.dto.*;
import com.application.submissionservice.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService service;

    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @PostMapping("/run")
    public RunResponse run(@RequestBody Map<String, Object> payload) {
        RunRequest request = new RunRequest();
        request.setProblemId(((Number) payload.get("problemId")).longValue());
        request.setLanguageKey((String) payload.get("languageKey"));
        request.setSourceCode((String) payload.get("sourceCode"));

        String customInput = payload.get("customInput") instanceof String
                ? (String) payload.get("customInput")
                : null;

        return service.run(request, customInput);
    }

    @PostMapping("/submit")
    public SubmitResponse submit(
            @RequestBody SubmitRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return service.submit(request, userId);
    }

    @GetMapping("/me")
    public List<SubmissionSummaryResponse> getMyRecentSubmissions(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String languageKey,
            @RequestParam(required = false) Long problemId
    ) {
        return service.getSubmissionHistory(userId, status, languageKey, problemId);
    }

    @GetMapping("/me/problem-summary/{problemId}")
    public ProblemAttemptSummaryResponse getProblemAttemptSummary(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable Long problemId
    ) {
        return service.getProblemAttemptSummary(userId, problemId);
    }

    @GetMapping("/{submissionId}")
    public SubmissionDetailResponse getSubmissionDetail(
            @PathVariable Long submissionId,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return service.getSubmissionDetail(submissionId, userId);
    }
}

