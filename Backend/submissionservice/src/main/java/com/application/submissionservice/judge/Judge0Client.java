package com.application.submissionservice.judge;

import com.application.submissionservice.dto.Judge0Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class Judge0Client {

    @Value("${judge0.base-url}")
    private String judge0BaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public Judge0Client(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> submit(String code, String input, int languageId) {

        try {
            Judge0Request body = new Judge0Request(code, input, languageId);

            String json = objectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            return restTemplate.postForObject(
                    judge0BaseUrl.trim() + "/submissions?wait=true",
                    entity,
                    Map.class
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
