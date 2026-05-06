package com.application.attemptservice.repository;

import com.application.attemptservice.entity.Attempt;
import com.application.attemptservice.entity.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {

    Optional<Attempt> findFirstByUserIdAndQuizIdAndStatusOrderByStartedAtDesc(
            UUID userId,
            UUID quizId,
            AttemptStatus status
    );

    Optional<Attempt> findByIdAndUserId(UUID id, UUID userId);


}
