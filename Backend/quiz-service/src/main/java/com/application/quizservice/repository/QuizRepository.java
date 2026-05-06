package com.application.quizservice.repository;

import com.application.quizservice.entity.DifficultyLevel;
import com.application.quizservice.entity.Quiz;
import com.application.quizservice.entity.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    List<Quiz> findByStatus(QuizStatus status);

    Optional<Quiz> findByIdAndStatus(UUID id, QuizStatus status);

    List<Quiz> findByStatusAndCategory(
            QuizStatus status,
            String category
    );

    List<Quiz> findByStatusAndCategoryAndDifficulty(
            QuizStatus status,
            String category,
            DifficultyLevel difficulty
    );

    List<Quiz> findByStatusAndCategoryAndSubCategoryAndDifficulty(
            QuizStatus status,
            String category,
            String subCategory,
            DifficultyLevel difficulty
    );

    List<Quiz> findByCategory(String category);

    List<Quiz> findByCategoryAndSubCategory(String category, String subCategory);

    @Modifying
    @Query("UPDATE Quiz q SET q.status = :status WHERE q.id IN :ids")
    void updateStatusBulk(@Param("ids") List<UUID> ids,
                          @Param("status") QuizStatus status);

}
