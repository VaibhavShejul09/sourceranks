package com.application.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserPreferenceRequest {

    @NotBlank(message = "Goal is required")
    @Pattern(
            regexp = "Interview Prep|College/Exam Practice|Skill Improvement",
            message = "Goal must be one of: Interview Prep, College/Exam Practice, Skill Improvement"
    )
    private String goal;

    @NotBlank(message = "Preferred track is required")
    @Pattern(
            regexp = "Coding|Quiz|Both",
            message = "Track must be one of: Coding, Quiz, Both"
    )
    private String preferredTrack;

    @NotBlank(message = "Skill level is required")
    @Pattern(
            regexp = "Beginner|Intermediate|Advanced",
            message = "Level must be one of: Beginner, Intermediate, Advanced"
    )
    private String skillLevel;
}
