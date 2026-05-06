package com.application.attemptservice.repository;

import com.application.attemptservice.entity.Answer;
import com.application.attemptservice.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    Optional<Answer> findByAttemptIdAndQuestionId(UUID attemptId, UUID questionId);

    List<Answer> findByAttempt(Attempt attempt);

    Optional<Answer> findByAttemptAndQuestionId(Attempt attempt, UUID questionId);


}
