package com.application.questionservice.controller;

import com.application.questionservice.dto.QuestionAnswerResponse;
import com.application.questionservice.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/questions")
@RequiredArgsConstructor
public class InternalQuestionController {

    private final QuestionRepository questionRepository;

    @GetMapping("/quiz/{quizId}")
    public List<QuestionAnswerResponse> getCorrectAnswers(
            @PathVariable UUID quizId
    ) {
        return questionRepository.findByQuizId(quizId)
                .stream()
                .map(q -> QuestionAnswerResponse.builder()
                        .questionId(q.getId())
                        .correctOption(q.getCorrectOption())
                        .build())
                .toList();
    }
}
