package com.application.attemptservice.controller;

import com.application.attemptservice.dto.AttemptDetails;
import com.application.attemptservice.entity.Answer;
import com.application.attemptservice.entity.Attempt;
import com.application.attemptservice.repository.AnswerRepository;
import com.application.attemptservice.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/internal/attempts")
@RequiredArgsConstructor
public class InternalAttemptController {

    private final AttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;

    @GetMapping("/{attemptId}")
    public AttemptDetails getAttemptDetails(@PathVariable UUID attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<Answer> answers = answerRepository.findByAttempt(attempt);

        Map<UUID, String> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        Answer::getQuestionId,
                        Answer::getSelectedOption
                ));

        return AttemptDetails.builder()
                .attemptId(attempt.getId())
                .userId(attempt.getUserId())
                .quizId(attempt.getQuizId())
                .answers(answerMap)
                .build();
    }
}
