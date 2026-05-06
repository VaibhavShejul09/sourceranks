package com.application.resultservice.client;

import com.application.resultservice.dto.AttemptDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "attempt-service")
public interface AttemptServiceClient {

    @GetMapping("/api/internal/attempts/{attemptId}")
    AttemptDetails getAttemptDetails(@PathVariable UUID attemptId);
}