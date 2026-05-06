package com.application.quizservice.controller;

import com.application.quizservice.dto.QuizAnalyticsResponse;
import com.application.quizservice.dto.QuizRequest;
import com.application.quizservice.dto.QuizResponse;
import com.application.quizservice.entity.QuizStatus;
import com.application.quizservice.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class QuizAdminController {private final QuizService quizService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<QuizResponse> getQuizByIdForAdmin(
            @PathVariable UUID quizId
    ) {
        return ResponseEntity.ok(
                quizService.getQuizById(quizId)
        );
    }

    @PostMapping("/quizzes")
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(request));
        } catch (Exception e) {
            e.printStackTrace(); // Log exact DB/Hibernate error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/quizzes/{quizId}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable UUID quizId,
            @Valid @RequestBody QuizRequest request
    ) {
        System.out.println("Updating quiz ID: " + quizId);
        System.out.println("Payload: " + request);
        return ResponseEntity.ok(
                quizService.updateQuiz(quizId, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable UUID quizId
    ) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/quizzes/{quizId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID quizId,
            @RequestParam("status") String status
    ) {
        QuizStatus quizStatus;
        try {
            quizStatus = QuizStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        quizService.updateStatus(quizId, quizStatus);
        return ResponseEntity.ok().build();
    }




    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        return ResponseEntity.ok(
                quizService.getAllQuizzes()
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/quizzes/filter")
    public ResponseEntity<List<QuizResponse>> getAllQuizzesFilter(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subCategory
    ) {
        return ResponseEntity.ok(
                quizService.getAllQuizzesFilter(category, subCategory)
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/quizzes/analytics")
    public ResponseEntity<QuizAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(
                quizService.getQuizAnalytics()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/quizzes/publish")
    public ResponseEntity<Void> bulkPublish(
            @RequestBody List<UUID> quizIds,
            @RequestParam boolean published
    ) {
        quizService.bulkPublish(quizIds, published);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/quizzes")
    public ResponseEntity<Void> bulkDelete(
            @RequestBody List<UUID> quizIds
    ) {
        quizService.bulkDelete(quizIds);
        return ResponseEntity.noContent().build();
    }
    
}
