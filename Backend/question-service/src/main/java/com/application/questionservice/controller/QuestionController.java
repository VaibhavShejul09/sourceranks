package com.application.questionservice.controller;

import com.application.questionservice.dto.QuestionRequest;
import com.application.questionservice.dto.QuestionResponse;
import com.application.questionservice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // ========== USER APIs ==========
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable UUID quizId
    ) {
        return ResponseEntity.ok(questionService.getQuestionsByQuiz(quizId));
    }
}
