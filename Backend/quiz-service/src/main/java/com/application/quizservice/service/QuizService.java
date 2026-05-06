package com.application.quizservice.service;

import com.application.quizservice.dto.QuizAnalyticsResponse;
import com.application.quizservice.dto.QuizRequest;
import com.application.quizservice.dto.QuizResponse;
import com.application.quizservice.entity.DifficultyLevel;
import com.application.quizservice.entity.QuizStatus;

import java.util.List;
import java.util.UUID;
public interface QuizService {

    // Existing
    QuizResponse createQuiz(QuizRequest request);
    QuizResponse updateQuiz(UUID quizId, QuizRequest request);
    void deleteQuiz(UUID quizId);
    void publishQuiz(UUID quizId);
    void updateStatus(UUID quizId, QuizStatus status);

    List<QuizResponse> getPublishedQuizzes();
    QuizResponse getPublishedQuizById(UUID quizId);

    // 🔹 NEW ADMIN METHODS
    QuizResponse getQuizById(UUID quizId);
    List<QuizResponse> getAllQuizzes();
    List<QuizResponse> getAllQuizzesFilter(String category, String subCategory);
    QuizAnalyticsResponse getQuizAnalytics();
    void bulkPublish(List<UUID> quizIds, boolean published);
    void bulkDelete(List<UUID> quizIds);
}
