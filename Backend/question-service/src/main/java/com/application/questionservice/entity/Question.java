package com.application.questionservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "idx_question_quiz", columnList = "quiz_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "quiz_id", nullable = false, length = 36)
    private UUID quizId;

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @Column(name = "optiona", nullable = false, length = 500)
    private String optionA;

    @Column(name = "optionb", nullable = false, length = 500)
    private String optionB;

    @Column(name = "optionc", nullable = false, length = 500)
    private String optionC;

    @Column(name = "optiond", nullable = false, length = 500)
    private String optionD;

    @Column(name = "correct_option", nullable = false, length = 1)
    private String correctOption;
}
