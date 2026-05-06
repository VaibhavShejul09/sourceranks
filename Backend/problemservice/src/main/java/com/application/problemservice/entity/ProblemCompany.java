package com.application.problemservice.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "problem_companies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"problem_id", "company_id"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProblemCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
