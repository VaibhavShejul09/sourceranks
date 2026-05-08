package com.application.userservice.config;

import com.application.userservice.entity.StudyPlan;
import com.application.userservice.entity.StudyPlanItem;
import com.application.userservice.entity.StudyPlanItemType;
import com.application.userservice.repository.StudyPlanRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StudyPlanSeedData {

    @Bean
    ApplicationRunner studyPlanSeeder(StudyPlanRepository studyPlanRepository) {
        return args -> {
            if (studyPlanRepository.count() > 0) {
                return;
            }

            studyPlanRepository.saveAll(List.of(
                    plan("dsa-basics", "DSA Basics", "Core arrays, strings, and two-pointer coding warmup.", "Coding", "Beginner",
                            item(1, "Arrays warmup", "Solve an introductory array problem.", StudyPlanItemType.CODING_PROBLEM, "problem-arrays-101", 20),
                            item(2, "String fundamentals quiz", "Test basic string manipulation concepts.", StudyPlanItemType.QUIZ, "quiz-strings-101", 15),
                            item(3, "Two pointers intro", "Solve a beginner two-pointer challenge.", StudyPlanItemType.CODING_PROBLEM, "problem-two-pointers-101", 25)
                    ),
                    plan("java-problem-solving", "Java Problem Solving", "Strengthen Java syntax and implementation confidence with guided practice.", "Coding", "Intermediate",
                            item(1, "Java collections challenge", "Implement a hash map based coding task.", StudyPlanItemType.CODING_PROBLEM, "problem-java-collections", 30),
                            item(2, "OOP concept checkpoint", "Review Java OOP concepts in a quiz.", StudyPlanItemType.QUIZ, "quiz-java-oop", 15),
                            item(3, "Streams and loops drill", "Practice Java iteration and streams.", StudyPlanItemType.CODING_PROBLEM, "problem-java-streams", 25)
                    ),
                    plan("frontend-mcq-revision", "Frontend MCQ Revision", "Revise browser, HTML, CSS, and JavaScript concepts through quizzes.", "Quiz", "Beginner",
                            item(1, "HTML and semantics", "Quick MCQ revision on semantic HTML.", StudyPlanItemType.QUIZ, "quiz-html-semantics", 15),
                            item(2, "CSS layouts", "Review flexbox and grid concepts.", StudyPlanItemType.QUIZ, "quiz-css-layouts", 15),
                            item(3, "JavaScript basics", "Strengthen core JS concept recall.", StudyPlanItemType.QUIZ, "quiz-js-basics", 20)
                    ),
                    plan("sql-backend-quiz-track", "SQL + Backend Quiz Track", "Refresh backend and database concepts with interview-style quizzes.", "Quiz", "Intermediate",
                            item(1, "SQL joins and indexing", "Revise query optimization basics.", StudyPlanItemType.QUIZ, "quiz-sql-joins", 20),
                            item(2, "REST and HTTP", "Checkpoint on backend API fundamentals.", StudyPlanItemType.QUIZ, "quiz-rest-http", 15),
                            item(3, "Spring backend concepts", "Review Spring and service design topics.", StudyPlanItemType.QUIZ, "quiz-spring-backend", 20)
                    ),
                    plan("mixed-interview-prep", "Mixed Interview Prep", "Blend coding drills and quizzes for broad interview readiness.", "Both", "Advanced",
                            item(1, "Algorithm sprint", "Solve an interview-grade coding problem.", StudyPlanItemType.CODING_PROBLEM, "problem-interview-algo", 35),
                            item(2, "System design concepts quiz", "Review architecture fundamentals in a quiz.", StudyPlanItemType.QUIZ, "quiz-system-design", 20),
                            item(3, "Database coding challenge", "Solve a backend-flavored coding task.", StudyPlanItemType.CODING_PROBLEM, "problem-backend-db", 30)
                    )
            ));
        };
    }

    private StudyPlan plan(
            String slug,
            String title,
            String description,
            String track,
            String level,
            StudyPlanItem... items
    ) {
        StudyPlan studyPlan = StudyPlan.builder()
                .slug(slug)
                .title(title)
                .description(description)
                .track(track)
                .level(level)
                .active(true)
                .build();

        for (StudyPlanItem item : items) {
            item.setStudyPlan(studyPlan);
            studyPlan.getItems().add(item);
        }

        return studyPlan;
    }

    private StudyPlanItem item(
            int sequenceNumber,
            String title,
            String description,
            StudyPlanItemType itemType,
            String referenceKey,
            int estimatedMinutes
    ) {
        return StudyPlanItem.builder()
                .sequenceNumber(sequenceNumber)
                .title(title)
                .description(description)
                .itemType(itemType)
                .referenceKey(referenceKey)
                .estimatedMinutes(estimatedMinutes)
                .build();
    }
}
