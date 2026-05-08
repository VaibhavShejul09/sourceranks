package com.application.userservice.repository;

import com.application.userservice.entity.UserStudyPlan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStudyPlanRepository extends JpaRepository<UserStudyPlan, Long> {

    boolean existsByUserIdAndStudyPlanId(UUID userId, Long studyPlanId);

    @EntityGraph(attributePaths = {"studyPlan", "studyPlan.items"})
    List<UserStudyPlan> findByUserIdOrderByEnrolledAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"studyPlan", "studyPlan.items"})
    List<UserStudyPlan> findByUserIdAndActiveTrueOrderByEnrolledAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"studyPlan", "studyPlan.items"})
    Optional<UserStudyPlan> findByUserIdAndStudyPlanId(UUID userId, Long studyPlanId);
}
