package com.application.questionservice.service;

import com.application.questionservice.dto.QuestionAnswerAdminResponse;
import com.application.questionservice.dto.QuestionRequest;
import com.application.questionservice.dto.QuestionResponse;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse updateQuestion(UUID questionId, QuestionRequest request);

    void deleteQuestion(UUID questionId);

    List<QuestionAnswerAdminResponse> getQuestionsAnswerByQuiz(UUID quizId);

    List<QuestionResponse> getQuestionsByQuiz(UUID quizId);
}


