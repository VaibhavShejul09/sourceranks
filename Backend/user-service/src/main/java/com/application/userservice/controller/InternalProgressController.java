package com.application.userservice.controller;

import com.application.userservice.dto.ActivityProgressUpdateRequest;
import com.application.userservice.dto.ActivityProgressUpdateResponse;
import com.application.userservice.service.StudyPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/internal/progress")
public class InternalProgressController {

    private final StudyPlanService studyPlanService;

    public InternalProgressController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/activity-completions")
    public ResponseEntity<ActivityProgressUpdateResponse> updateActivityCompletion(
            Authentication authentication,
            @Valid @RequestBody ActivityProgressUpdateRequest request
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(studyPlanService.markActivityCompleted(userId, request));
    }
}
