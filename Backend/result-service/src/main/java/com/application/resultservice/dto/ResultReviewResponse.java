package com.application.resultservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ResultReviewResponse(
        UUID attemptId,
        UUID quizId,
        String quizTitle,
        String category,
        String subCategory,
        String difficulty,
        Integer score,
        Integer totalQuestions,
        Double percentage,
        Double previousAttemptPercentage,
        Double bestPreviousPercentage,
        Double percentageDelta,
        Integer priorAttempts,
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
