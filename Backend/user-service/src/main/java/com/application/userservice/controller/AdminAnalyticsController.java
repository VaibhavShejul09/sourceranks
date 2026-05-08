package com.application.userservice.controller;

import com.application.userservice.dto.AdminKpiDashboardResponse;
import com.application.userservice.service.ProductEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final ProductEventService productEventService;

    public AdminAnalyticsController(ProductEventService productEventService) {
        this.productEventService = productEventService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<AdminKpiDashboardResponse> getKpis() {
        return ResponseEntity.ok(productEventService.getKpiDashboard());
    }

    @GetMapping("/problems")
    public ResponseEntity<AdminKpiDashboardResponse.ContentAnalyticsResponse> getProblemAnalytics() {
        return ResponseEntity.ok(productEventService.getProblemAnalytics());
    }

    @GetMapping("/quizzes")
    public ResponseEntity<AdminKpiDashboardResponse.ContentAnalyticsResponse> getQuizAnalytics() {
        return ResponseEntity.ok(productEventService.getQuizAnalytics());
    }

    @GetMapping("/questions")
    public ResponseEntity<AdminKpiDashboardResponse.ContentAnalyticsResponse> getQuestionAnalytics() {
        return ResponseEntity.ok(productEventService.getQuestionAnalytics());
    }
}
