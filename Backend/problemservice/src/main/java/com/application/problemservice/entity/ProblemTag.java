package com.application.problemservice.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "problem_tags",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"problem_id", "tag_id"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProblemTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
