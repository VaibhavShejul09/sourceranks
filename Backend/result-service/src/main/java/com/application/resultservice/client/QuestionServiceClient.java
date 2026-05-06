package com.application.resultservice.client;

import com.application.resultservice.dto.QuestionAnswer;
import com.application.resultservice.dto.QuestionAnswerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "question-service")
public interface QuestionServiceClient {

    @GetMapping("/api/internal/questions/quiz/{quizId}")
    List<QuestionAnswerDTO> getCorrectAnswers(@PathVariable UUID quizId);
}
