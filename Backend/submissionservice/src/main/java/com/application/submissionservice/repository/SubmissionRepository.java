package com.application.submissionservice.repository;

import com.application.submissionservice.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);

    Optional<Submission> findByIdAndUserId(Long id, Long userId);
}

