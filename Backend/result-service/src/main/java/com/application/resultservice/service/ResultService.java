package com.application.resultservice.service;

import com.application.resultservice.dto.ResultResponse;
import com.application.resultservice.dto.ResultReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ResultService {

    ResultResponse evaluateAttempt(UUID attemptId, UUID userId);

    ResultResponse getResultByAttempt(UUID attemptId, UUID userId);

    List<ResultResponse> getResultsByUser(UUID userId, UUID quizId, Double minimumPercentage);

    ResultReviewResponse getResultReview(UUID attemptId, UUID userId);
}
