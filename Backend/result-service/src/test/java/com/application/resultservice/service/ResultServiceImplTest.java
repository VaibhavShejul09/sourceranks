package com.application.resultservice.service;

import com.application.resultservice.client.AttemptServiceClient;
import com.application.resultservice.client.QuestionServiceClient;
import com.application.resultservice.client.QuizServiceClient;
import com.application.resultservice.client.UserProgressClient;
import com.application.resultservice.dto.ActivityProgressUpdateRequest;
import com.application.resultservice.dto.AttemptDetails;
import com.application.resultservice.dto.QuestionAnswerDTO;
import com.application.resultservice.dto.ResultResponse;
import com.application.resultservice.entity.Result;
import com.application.resultservice.repository.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultServiceImplTest {

    private static final UUID ATTEMPT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID QUIZ_ID = UUID.randomUUID();
    private static final UUID QUESTION_ONE = UUID.randomUUID();
    private static final UUID QUESTION_TWO = UUID.randomUUID();

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private AttemptServiceClient attemptClient;

    @Mock
    private QuestionServiceClient questionClient;

    @Mock
    private QuizServiceClient quizServiceClient;

    @Mock
    private UserProgressClient userProgressClient;

    @InjectMocks
    private ResultServiceImpl resultService;

    @Captor
    private ArgumentCaptor<ActivityProgressUpdateRequest> progressRequestCaptor;

    private AttemptDetails attemptDetails;

    @BeforeEach
    void setUp() {
        attemptDetails = AttemptDetails.builder()
                .attemptId(ATTEMPT_ID)
                .userId(USER_ID)
                .quizId(QUIZ_ID)
                .answers(Map.of(
                        QUESTION_ONE, "A",
                        QUESTION_TWO, "B"
                ))
                .build();
    }

    @Test
    void completedQuizResultShouldUpdateProgress() {
        QuestionAnswerDTO firstAnswer = new QuestionAnswerDTO();
        firstAnswer.setQuestionId(QUESTION_ONE);
        firstAnswer.setCorrectOption("A");

        QuestionAnswerDTO secondAnswer = new QuestionAnswerDTO();
        secondAnswer.setQuestionId(QUESTION_TWO);
        secondAnswer.setCorrectOption("C");

        when(resultRepository.findByAttemptId(ATTEMPT_ID)).thenReturn(Optional.empty());
        when(attemptClient.getAttemptDetails(ATTEMPT_ID)).thenReturn(attemptDetails);
        when(questionClient.getCorrectAnswers(QUIZ_ID)).thenReturn(List.of(firstAnswer, secondAnswer));
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ResultResponse response = resultService.evaluateAttempt(ATTEMPT_ID, USER_ID);

        assertThat(response.getScore()).isEqualTo(1);
        verify(userProgressClient).updateActivityProgress(eq(USER_ID.toString()), eq("ROLE_USER"), progressRequestCaptor.capture());
        assertThat(progressRequestCaptor.getValue().referenceKey()).isEqualTo("quiz-" + QUIZ_ID);
        assertThat(progressRequestCaptor.getValue().itemType()).isEqualTo("QUIZ");
    }

    @Test
    void duplicateCompletionShouldBeIdempotent() {
        when(resultRepository.findByAttemptId(ATTEMPT_ID)).thenReturn(Optional.of(Result.builder()
                .attemptId(ATTEMPT_ID)
                .userId(USER_ID)
                .quizId(QUIZ_ID)
                .score(2)
                .totalQuestions(2)
                .percentage(100.0)
                .build()));

        assertThatThrownBy(() -> resultService.evaluateAttempt(ATTEMPT_ID, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

        verify(userProgressClient, never()).updateActivityProgress(anyString(), anyString(), any(ActivityProgressUpdateRequest.class));
    }

    @Test
    void shouldFilterResultsByQuizAndPercentage() {
        when(resultRepository.findByUserId(USER_ID)).thenReturn(List.of(
                Result.builder().attemptId(UUID.randomUUID()).userId(USER_ID).quizId(QUIZ_ID).percentage(82.0).build(),
                Result.builder().attemptId(UUID.randomUUID()).userId(USER_ID).quizId(UUID.randomUUID()).percentage(49.0).build()
        ));

        List<ResultResponse> response = resultService.getResultsByUser(USER_ID, QUIZ_ID, 70.0);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getQuizId()).isEqualTo(QUIZ_ID);
    }

    @Test
    void shouldUseLatestPreviousAttemptForScoreComparison() {
        Result current = Result.builder()
                .attemptId(ATTEMPT_ID)
                .userId(USER_ID)
                .quizId(QUIZ_ID)
                .score(8)
                .totalQuestions(10)
                .percentage(80.0)
                .evaluatedAt(LocalDateTime.now())
                .build();
        Result older = Result.builder()
                .attemptId(UUID.randomUUID())
                .userId(USER_ID)
                .quizId(QUIZ_ID)
                .score(5)
                .totalQuestions(10)
                .percentage(50.0)
                .evaluatedAt(LocalDateTime.now().minusDays(2))
                .build();
        Result latestPrior = Result.builder()
                .attemptId(UUID.randomUUID())
                .userId(USER_ID)
                .quizId(QUIZ_ID)
                .score(7)
                .totalQuestions(10)
                .percentage(70.0)
                .evaluatedAt(LocalDateTime.now().minusDays(1))
                .build();

        QuestionAnswerDTO firstAnswer = new QuestionAnswerDTO();
        firstAnswer.setQuestionId(QUESTION_ONE);
        firstAnswer.setCorrectOption("A");
        QuestionAnswerDTO secondAnswer = new QuestionAnswerDTO();
        secondAnswer.setQuestionId(QUESTION_TWO);
        secondAnswer.setCorrectOption("B");

        when(resultRepository.findByAttemptId(ATTEMPT_ID)).thenReturn(Optional.of(current));
        when(attemptClient.getAttemptDetails(ATTEMPT_ID)).thenReturn(attemptDetails);
        when(questionClient.getCorrectAnswers(QUIZ_ID)).thenReturn(List.of(firstAnswer, secondAnswer));
        when(resultRepository.findByUserId(USER_ID)).thenReturn(List.of(current, older, latestPrior));

        var review = resultService.getResultReview(ATTEMPT_ID, USER_ID);

        assertThat(review.previousAttemptPercentage()).isEqualTo(70.0);
        assertThat(review.bestPreviousPercentage()).isEqualTo(70.0);
        assertThat(review.percentageDelta()).isEqualTo(10.0);
    }
}
