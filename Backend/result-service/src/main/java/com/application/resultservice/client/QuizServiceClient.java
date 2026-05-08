package com.application.resultservice.client;

import com.application.resultservice.dto.QuizMetadataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "quiz-service")
public interface QuizServiceClient {

    @GetMapping("/api/quizzes/{quizId}")
    QuizMetadataResponse getQuizById(
            @PathVariable UUID quizId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Role") String role
    );
}
