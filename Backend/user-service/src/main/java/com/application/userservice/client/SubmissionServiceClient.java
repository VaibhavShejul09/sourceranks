package com.application.userservice.client;

import com.application.userservice.dto.SubmissionAnalyticsView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "submissionservice")
public interface SubmissionServiceClient {

    @GetMapping("/api/submissions/me")
    List<SubmissionAnalyticsView> getSubmissionHistory(
            @RequestHeader("X-User-Id") String userId
    );
}
