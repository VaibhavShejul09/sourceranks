package com.application.resultservice.client;

import com.application.resultservice.dto.ActivityProgressUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserProgressClient {

    @PostMapping("/api/users/internal/progress/activity-completions")
    void updateActivityProgress(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Role") String role,
            @RequestBody ActivityProgressUpdateRequest request
    );
}
