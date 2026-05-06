package com.application.questionservice.controller;

import com.application.questionservice.dto.QuestionAnswerAdminResponse;
import com.application.questionservice.dto.QuestionRequest;
import com.application.questionservice.dto.QuestionResponse;
import com.application.questionservice.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class QuestionAdminController {

    private final QuestionService questionService;

    @GetMapping("/quiz/{quizId}")
    public List<QuestionAnswerAdminResponse> getByQuiz(@PathVariable UUID quizId) {
        return questionService.getQuestionsAnswerByQuiz(quizId);
    }

    @PostMapping
    public QuestionResponse create(@Valid @RequestBody QuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @PutMapping("/{id}")
    public QuestionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequest request
    ) {
        return questionService.updateQuestion(id, request);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
    }
}
