package com.application.attemptservice.service;

import com.application.attemptservice.dto.AnswerRequest;
import com.application.attemptservice.entity.Answer;
import com.application.attemptservice.entity.Attempt;
import com.application.attemptservice.entity.AttemptStatus;
import com.application.attemptservice.repository.AnswerRepository;
import com.application.attemptservice.repository.AttemptRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;

    @Override
    public UUID startAttempt(UUID quizId, UUID userId) {

        return attemptRepository
                .findFirstByUserIdAndQuizIdAndStatusOrderByStartedAtDesc(
                        userId,
                        quizId,
                        AttemptStatus.IN_PROGRESS
                )
                .map(Attempt::getId)
                .orElseGet(() -> {
                    Attempt attempt = Attempt.builder()
                            .userId(userId)   // ✅ correct now
                            .quizId(quizId)
                            .status(AttemptStatus.IN_PROGRESS)
                            .startedAt(LocalDateTime.now())
                            .build();

                    UUID attemptId = attemptRepository.save(attempt).getId();
                    log.info("Started attempt {} for quiz {} and user {}", attemptId, quizId, userId);
                    return attemptId;
                });
    }



    @Override
    public void saveAnswer(UUID attemptId, UUID userId, AnswerRequest request) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Unauthorized attempt access");
        }

        if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot answer after submission");
        }

        Answer answer = answerRepository
                .findByAttemptAndQuestionId(attempt, request.getQuestionId())
                .orElseGet(() -> new Answer(null, attempt, request.getQuestionId(), null));

        answer.setSelectedOption(request.getSelectedOption());
        answerRepository.save(answer);
        log.debug("Saved answer for attempt {} question {}", attemptId, request.getQuestionId());
    }

    @Override
    public void submitAttempt(UUID attemptId, UUID userId) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Attempt not found"));

        if (!attempt.getUserId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Unauthorized attempt submission");
        }

        if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
            throw new ResponseStatusException(BAD_REQUEST, "Attempt is already submitted");
        }

        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());

        attemptRepository.save(attempt);
        log.info("Submitted attempt {} for user {}", attemptId, userId);
    }
}
