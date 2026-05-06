package com.application.quizservice.service;

import com.application.quizservice.dto.QuizAnalyticsResponse;
import com.application.quizservice.dto.QuizRequest;
import com.application.quizservice.dto.QuizResponse;
import com.application.quizservice.entity.DifficultyLevel;
import com.application.quizservice.entity.Quiz;
import com.application.quizservice.entity.QuizStatus;
import com.application.quizservice.exception.ResourceNotFoundException;
import com.application.quizservice.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    public QuizResponse createQuiz(QuizRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .status(QuizStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .category(request.getCategory())
                .subCategory(request.getSubCategory())
                .difficulty(request.getDifficulty())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Created quiz {}", savedQuiz.getId());
        return mapToResponse(savedQuiz);
    }

    @Override
    public QuizResponse updateQuiz(UUID quizId, QuizRequest request) {
        Quiz quiz = getQuizOrThrow(quizId);

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setCategory(request.getCategory());
        quiz.setSubCategory(request.getSubCategory());
        quiz.setDifficulty(resolveDifficulty(request));

        Quiz updatedQuiz = quizRepository.save(quiz);
        log.info("Updated quiz {}", updatedQuiz.getId());
        return mapToResponse(updatedQuiz);
    }

    @Override
    public void deleteQuiz(UUID quizId) {
        Quiz quiz = getQuizOrThrow(quizId);
        quizRepository.delete(quiz);
        log.info("Deleted quiz {}", quizId);
    }

    @Override
    public void publishQuiz(UUID quizId) {
        Quiz quiz = getQuizOrThrow(quizId);

        if (quiz.getStatus() == QuizStatus.PUBLISHED) {
            throw new IllegalStateException("Quiz is already published");
        }

        quiz.setStatus(QuizStatus.PUBLISHED);
        quizRepository.save(quiz);
        log.info("Published quiz {}", quizId);
    }

    @Override
    public void updateStatus(UUID quizId, QuizStatus status) {
        Quiz quiz = getQuizOrThrow(quizId);

        if (quiz.getStatus() == status) {
            throw new IllegalStateException("Quiz is already in status " + status.name());
        }

        quiz.setStatus(status);
        quizRepository.save(quiz);
        log.info("Updated quiz {} to status {}", quizId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getPublishedQuizzes() {
        return quizRepository.findByStatus(QuizStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getPublishedQuizById(UUID quizId) {
        Quiz quiz = quizRepository
                .findByIdAndStatus(quizId, QuizStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Published quiz not found"));

        return mapToResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(UUID quizId) {
        Quiz quiz = quizRepository
                .findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        return mapToResponse(quiz);
    }

    public List<QuizResponse> getPublishedQuizzes(
            String category,
            String subCategory,
            DifficultyLevel difficulty
    ) {

        if (category == null && difficulty == null && subCategory == null) {
            return quizRepository.findByStatus(QuizStatus.PUBLISHED)
                    .stream().map(this::mapToResponse).toList();
        }

        if (category != null && difficulty != null && subCategory != null) {
            return quizRepository
                    .findByStatusAndCategoryAndSubCategoryAndDifficulty(
                            QuizStatus.PUBLISHED, category, subCategory, difficulty
                    )
                    .stream().map(this::mapToResponse).toList();
        }

        if (category != null && difficulty != null) {
            return quizRepository
                    .findByStatusAndCategoryAndDifficulty(
                            QuizStatus.PUBLISHED, category, difficulty
                    )
                    .stream().map(this::mapToResponse).toList();
        }

        if (category != null) {
            return quizRepository
                    .findByStatusAndCategory(QuizStatus.PUBLISHED, category)
                    .stream().map(this::mapToResponse).toList();
        }

        return quizRepository.findByStatus(QuizStatus.PUBLISHED)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getAllQuizzesFilter(String category, String subCategory) {

        if (category == null && subCategory == null) {
            return quizRepository.findAll()
                    .stream().map(this::mapToResponse).toList();
        }

        if (category != null && subCategory != null) {
            return quizRepository
                    .findByCategoryAndSubCategory(category, subCategory)
                    .stream().map(this::mapToResponse).toList();
        }

        return quizRepository
                .findByCategory(category)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getAllQuizzes() {
        return quizRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAnalyticsResponse getQuizAnalytics() {
        long totalQuizzes = quizRepository.count();
        return new QuizAnalyticsResponse(totalQuizzes);
    }

    @Override
    public void bulkPublish(List<UUID> quizIds, boolean published) {
        QuizStatus status = published ? QuizStatus.PUBLISHED : QuizStatus.DRAFT;
        quizRepository.updateStatusBulk(quizIds, status);
        log.info("Updated {} quizzes to status {}", quizIds.size(), status);
    }

    @Override
    public void bulkDelete(List<UUID> quizIds) {
        quizRepository.deleteAllByIdInBatch(quizIds);
        log.info("Deleted {} quizzes in bulk", quizIds.size());
    }

    private Quiz getQuizOrThrow(UUID quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
    }

    private DifficultyLevel resolveDifficulty(QuizRequest request) {
        if (request.getDifficulty() == null) {
            return null;
        }

        try {
            return DifficultyLevel.valueOf(request.getDifficulty().name());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid difficulty level: " + request.getDifficulty());
        }
    }

    private QuizResponse mapToResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .durationMinutes(quiz.getDurationMinutes())
                .status(quiz.getStatus().name())
                .createdAt(quiz.getCreatedAt())
                .category(quiz.getCategory())
                .subCategory(quiz.getSubCategory())
                .difficulty(quiz.getDifficulty())
                .build();
    }
}
