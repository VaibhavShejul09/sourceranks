package com.application.userservice.repository;

import com.application.userservice.entity.UserStudyPlanItemProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserStudyPlanItemProgressRepository extends JpaRepository<UserStudyPlanItemProgress, Long> {

    List<UserStudyPlanItemProgress> findByUserStudyPlanId(Long userStudyPlanId);

    Optional<UserStudyPlanItemProgress> findByUserStudyPlanIdAndStudyPlanItemId(Long userStudyPlanId, Long studyPlanItemId);
}
