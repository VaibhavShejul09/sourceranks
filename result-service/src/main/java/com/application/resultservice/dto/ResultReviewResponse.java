package com.application.resultservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ResultReviewResponse(
        UUID attemptId,
        UUID quizId,
        Integer score,
        Integer totalQuestions,
        Double percentage,
        Integer correctAnswers,
        Integer incorrectAnswers,
        Integer unansweredQuestions,
        List<QuestionReview> questions
) {
    @Builder
    public record QuestionReview(
            UUID questionId,
            Integer questionNumber,
            String selectedOption,
            String correctOption,
            boolean correct
    ) {
    }
}
