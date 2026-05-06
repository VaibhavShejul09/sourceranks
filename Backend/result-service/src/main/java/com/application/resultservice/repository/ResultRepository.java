package com.application.resultservice.repository;

import com.application.resultservice.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultRepository extends JpaRepository<Result, UUID> {

    Optional<Result> findByAttemptId(UUID attemptId);

    List<Result> findByUserId(UUID userId);
}
