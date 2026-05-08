package com.application.userservice.service;

import com.application.userservice.client.ProblemServiceClient;
import com.application.userservice.client.QuizServiceClient;
import com.application.userservice.client.ResultServiceClient;
import com.application.userservice.client.SubmissionServiceClient;
import com.application.userservice.dto.ProblemMetadataView;
import com.application.userservice.dto.ProgressSummaryResponse;
import com.application.userservice.dto.QuizMetadataView;
import com.application.userservice.dto.ResultAnalyticsView;
import com.application.userservice.dto.SubmissionAnalyticsView;
import com.application.userservice.dto.UserAnalyticsResponse;
import com.application.userservice.dto.UserPreferenceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAnalyticsServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SubmissionServiceClient submissionServiceClient;

    @Mock
    private ResultServiceClient resultServiceClient;

    @Mock
    private ProblemServiceClient problemServiceClient;

    @Mock
    private QuizServiceClient quizServiceClient;

    @Mock
    private UserPreferenceService userPreferenceService;

    @Mock
    private StudyPlanService studyPlanService;

    @InjectMocks
    private UserAnalyticsService userAnalyticsService;

    @Test
    void shouldCalculateAnalyticsAndRecommendations() {
        SubmissionAnalyticsView acceptedSubmission = new SubmissionAnalyticsView();
        acceptedSubmission.setId(1L);
        acceptedSubmission.setProblemId(101L);
        acceptedSubmission.setLanguageKey("java");
        acceptedSubmission.setStatus("ACCEPTED");
        acceptedSubmission.setRuntimeMs(120);
        acceptedSubmission.setMemoryKb(256);
        acceptedSubmission.setCreatedAt(LocalDateTime.now().minusDays(1));

        SubmissionAnalyticsView failedSubmission = new SubmissionAnalyticsView();
        failedSubmission.setId(2L);
        failedSubmission.setProblemId(102L);
        failedSubmission.setLanguageKey("java");
        failedSubmission.setStatus("WRONG_ANSWER");
        failedSubmission.setRuntimeMs(180);
        failedSubmission.setMemoryKb(300);
        failedSubmission.setCreatedAt(LocalDateTime.now());

        ResultAnalyticsView quizResult = new ResultAnalyticsView();
        quizResult.setAttemptId(UUID.randomUUID());
        quizResult.setQuizId(UUID.randomUUID());
        quizResult.setScore(4);
        quizResult.setTotalQuestions(10);
        quizResult.setPercentage(40.0);

        ProblemMetadataView arraysProblem = new ProblemMetadataView();
        arraysProblem.setId(101L);
        arraysProblem.setTags(List.of("Arrays"));

        ProblemMetadataView dpProblem = new ProblemMetadataView();
        dpProblem.setId(102L);
        dpProblem.setTags(List.of("Dynamic Programming"));

        QuizMetadataView quizMetadata = new QuizMetadataView();
        quizMetadata.setId(quizResult.getQuizId());
        quizMetadata.setCategory("Java");
        quizMetadata.setSubCategory("Collections");

        when(submissionServiceClient.getSubmissionHistory(USER_ID.toString())).thenReturn(List.of(acceptedSubmission, failedSubmission));
        when(resultServiceClient.getResults(USER_ID.toString(), "ROLE_USER")).thenReturn(List.of(quizResult));
        when(problemServiceClient.getProblemById(101L)).thenReturn(arraysProblem);
        when(problemServiceClient.getProblemById(102L)).thenReturn(dpProblem);
        when(quizServiceClient.getQuizById(quizResult.getQuizId(), USER_ID.toString(), "ROLE_USER")).thenReturn(quizMetadata);
        when(userPreferenceService.getPreferences(USER_ID)).thenReturn(UserPreferenceResponse.builder()
                .userId(USER_ID.toString())
                .goal("Interview Prep")
                .preferredTrack("Both")
                .skillLevel("Intermediate")
                .onboardingCompleted(true)
                .build());
        when(studyPlanService.getProgressSummary(USER_ID)).thenReturn(ProgressSummaryResponse.builder()
                .enrolledPlans(0)
                .streakCount(2)
                .build());

        UserAnalyticsResponse response = userAnalyticsService.getAnalytics(USER_ID, "ROLE_USER");

        assertThat(response.getCodingPerformance().getTotalSubmissions()).isEqualTo(2);
        assertThat(response.getCodingPerformance().getAcceptedSubmissions()).isEqualTo(1);
        assertThat(response.getQuizPerformance().getAveragePercentage()).isEqualTo(40.0);
        assertThat(response.getCodingPerformance().getWeakTopics()).extracting("topic").contains("Dynamic Programming");
        assertThat(response.getQuizPerformance().getWeakTopics()).extracting("topic").contains("Collections");
        assertThat(response.getRecommendations()).isNotEmpty();
    }
}
