package com.application.quizservice.controller;

import com.application.quizservice.dto.QuizAnalyticsResponse;
import com.application.quizservice.dto.QuizRequest;
import com.application.quizservice.dto.QuizResponse;
import com.application.quizservice.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/quizzes")
    public ResponseEntity<List<QuizResponse>> getPublishedQuizzes() {
        return ResponseEntity.ok(
                quizService.getPublishedQuizzes()
        );
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/quizzes/{quizId}")
    public ResponseEntity<QuizResponse> getQuizById(
            @PathVariable UUID quizId
    ) {
        return ResponseEntity.ok(
                quizService.getPublishedQuizById(quizId)
        );
    }
}
