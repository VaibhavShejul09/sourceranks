package com.application.userservice.repository;

import com.application.userservice.entity.StudyPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {
}
