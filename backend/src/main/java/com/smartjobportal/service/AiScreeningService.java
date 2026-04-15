package com.smartjobportal.service;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.smartjobportal.dto.ai.AiScoreResponse;

@Service
public class AiScreeningService {

    @Value("${ai.screening.url:}")
    private String aiServiceUrl;

    public boolean isAvailable() {
        return aiServiceUrl != null && !aiServiceUrl.isBlank();
    }

    public AiScoreResponse getScore(String jobDescription, String resumeText) {
        if (!isAvailable()) {
            return null;
        }
        try {
            RestClient client = RestClient.create();
            Map<String, String> body = Map.of(
                    "job_description", jobDescription != null ? jobDescription : "",
                    "resume_text", resumeText != null ? resumeText : ""
            );
            AiScoreResponse response = client.post()
                    .uri(aiServiceUrl + "/score")
                    .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                    .body(Objects.requireNonNull(body))
                    .retrieve()
                    .body(AiScoreResponse.class);
            return response;
        } catch (Exception e) {
            return null;
        }
    }
}
