package com.application.userservice.client;

import com.application.userservice.dto.ProblemMetadataView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "problem-service")
public interface ProblemServiceClient {

    @GetMapping("/api/problems/{id}")
    ProblemMetadataView getProblemById(@PathVariable Long id);
}
