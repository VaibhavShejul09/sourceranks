package com.application.submissionservice.repository;

import com.application.submissionservice.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findTop5ByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Submission> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserId(UUID userId);

    Optional<Submission> findByIdAndUserId(Long id, UUID userId);
}

