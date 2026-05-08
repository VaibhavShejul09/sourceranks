package com.application.userservice.client;

import com.application.userservice.dto.ResultAnalyticsView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "result-service")
public interface ResultServiceClient {

    @GetMapping("/api/results/me")
    List<ResultAnalyticsView> getResults(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Role") String role
    );
}
