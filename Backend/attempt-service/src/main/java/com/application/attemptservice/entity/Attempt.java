package com.application.attemptservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "attempts",
        indexes = {
                @Index(name = "idx_attempt_user_quiz", columnList = "userId, quizId"),
                @Index(name = "idx_attempt_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attempt {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID quizId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttemptStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @PrePersist
    public void onCreate() {
        this.startedAt = LocalDateTime.now();
        this.status = AttemptStatus.IN_PROGRESS;
    }
}
