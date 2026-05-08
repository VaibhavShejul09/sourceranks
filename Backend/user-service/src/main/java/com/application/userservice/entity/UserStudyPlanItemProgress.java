package com.application.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_study_plan_item_progress",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_plan_item_progress",
                columnNames = {"user_study_plan_id", "study_plan_item_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStudyPlanItemProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_study_plan_id", nullable = false)
    private UserStudyPlan userStudyPlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_plan_item_id", nullable = false)
    private StudyPlanItem studyPlanItem;

    @Column(nullable = false)
    private boolean completed;

    private LocalDateTime completedAt;
}
