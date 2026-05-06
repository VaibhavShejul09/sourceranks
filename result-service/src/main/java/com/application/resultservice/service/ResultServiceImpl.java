package com.application.resultservice.service;

import com.application.resultservice.client.AttemptServiceClient;
import com.application.resultservice.client.QuestionServiceClient;
import com.application.resultservice.dto.AttemptDetails;
import com.application.resultservice.dto.QuestionAnswerDTO;
import com.application.resultservice.dto.ResultResponse;
import com.application.resultservice.dto.ResultReviewResponse;
import com.application.resultservice.entity.Result;
import com.application.resultservice.repository.ResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final AttemptServiceClient attemptClient;
    private final QuestionServiceClient questionClient;

    @Override
    public ResultResponse evaluateAttempt(UUID attemptId, UUID userId) {
        resultRepository.findByAttemptId(attemptId)
                .ifPresent(r -> {
                    throw new IllegalStateException("Result already exists");
                });

        AttemptDetails attempt = attemptClient.getAttemptDetails(attemptId);
        if (!attempt.getUserId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Unauthorized result evaluation");
        }

        List<QuestionAnswerDTO> correctAnswers =
                questionClient.getCorrectAnswers(attempt.getQuizId());

        int score = 0;
        for (QuestionAnswerDTO q : correctAnswers) {
            String userAnswer = attempt.getAnswers().get(q.getQuestionId());
            if (q.getCorrectOption().equals(userAnswer)) {
                score++;
            }
        }

        int total = correctAnswers.size();
        double percentage = total == 0 ? 0.0 : (score * 100.0) / total;

        Result result = Result.builder()
                .attemptId(attemptId)
                .userId(attempt.getUserId())
                .quizId(attempt.getQuizId())
                .score(score)
                .totalQuestions(total)
                .percentage(percentage)
                .build();

        resultRepository.save(result);
        log.info("Evaluated result for attempt {} and user {}", attemptId, userId);
        return mapToResponse(result);
    }

    @Override
    public ResultResponse getResultByAttempt(UUID attemptId, UUID userId) {
        return resultRepository.findByAttemptId(attemptId)
                .map(result -> {
                    if (!result.getUserId().equals(userId)) {
                        throw new ResponseStatusException(FORBIDDEN, "Unauthorized result access");
                    }
                    return mapToResponse(result);
                })
                .orElseGet(() -> evaluateAttempt(attemptId, userId));
    }

    @Override
    public List<ResultResponse> getResultsByUser(UUID userId) {
        return resultRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ResultReviewResponse getResultReview(UUID attemptId, UUID userId) {
        ResultResponse result = getResultByAttempt(attemptId, userId);
        AttemptDetails attempt = attemptClient.getAttemptDetails(attemptId);

        if (!attempt.getUserId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Unauthorized review access");
        }

        List<QuestionAnswerDTO> correctAnswers =
                questionClient.getCorrectAnswers(attempt.getQuizId());

        Map<UUID, String> selectedAnswers = attempt.getAnswers();
        AtomicInteger questionCounter = new AtomicInteger(1);

        List<ResultReviewResponse.QuestionReview> reviews = correctAnswers.stream()
                .map(answer -> {
                    String selectedOption = selectedAnswers.get(answer.getQuestionId());
                    String correctOption = answer.getCorrectOption();
                    return ResultReviewResponse.QuestionReview.builder()
                            .questionId(answer.getQuestionId())
                            .questionNumber(questionCounter.getAndIncrement())
                            .selectedOption(selectedOption)
                            .correctOption(correctOption)
                            .correct(correctOption.equals(selectedOption))
                            .build();
                })
                .toList();
        int correctCount = (int) reviews.stream().filter(ResultReviewResponse.QuestionReview::correct).count();
        int unansweredCount = (int) reviews.stream()
                .filter(question -> question.selectedOption() == null)
                .count();

        return ResultReviewResponse.builder()
                .attemptId(result.getAttemptId())
                .quizId(result.getQuizId())
                .score(result.getScore())
                .totalQuestions(result.getTotalQuestions())
                .percentage(result.getPercentage())
                .correctAnswers(correctCount)
                .incorrectAnswers(reviews.size() - correctCount - unansweredCount)
                .unansweredQuestions(unansweredCount)
                .questions(reviews)
                .build();
    }

    private ResultResponse mapToResponse(Result r) {
        return ResultResponse.builder()
                .attemptId(r.getAttemptId())
                .quizId(r.getQuizId())
                .score(r.getScore())
                .totalQuestions(r.getTotalQuestions())
                .percentage(r.getPercentage())
                .build();
    }
}
