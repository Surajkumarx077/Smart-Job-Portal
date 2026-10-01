package com.smartjobportal.service;

import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjobportal.dto.ai.AiScoreResponse;
import com.smartjobportal.dto.resume.ResumeAiInsights;

@Service
public class GeminiScreeningService {

    private final ObjectProvider<ChatModel> chatModelProvider;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GeminiScreeningService(ObjectProvider<ChatModel> chatModelProvider,
                                  ObjectMapper objectMapper,
                                  @Value("${GEMINI_API_KEY:}") String apiKey) {
        this.chatModelProvider = chatModelProvider;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public boolean isAvailable() {
        return !apiKey.isBlank() && chatModelProvider.getIfAvailable() != null;
    }

    public AiScoreResponse getScore(String jobDescription, String resumeText) {
        if (apiKey.isBlank()) {
            return null;
        }
        ChatModel chatModel = chatModelProvider.getIfAvailable();
        if (chatModel == null) {
            return null;
        }

        String prompt = """
                You are an applicant tracking system. Compare the job description with the resume.
                Return only valid JSON with this exact shape:
                {"score": number, "matchedSkills": ["skill"], "extractedSkills": ["skill"]}
                The score must be a number from 0 to 100. Do not use markdown or additional text.

                JOB DESCRIPTION:
                %s

                RESUME:
                %s
                """.formatted(valueOrEmpty(jobDescription), valueOrEmpty(resumeText));

        try {
            String content = chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
            AiScoreResponse response = objectMapper.readValue(content, AiScoreResponse.class);
            response.setScore(Math.max(0.0, Math.min(100.0, response.getScore())));
            response.setMatchedSkills(response.getMatchedSkills() != null ? response.getMatchedSkills() : List.of());
            response.setExtractedSkills(response.getExtractedSkills() != null ? response.getExtractedSkills() : List.of());
            return response;
        } catch (Exception exception) {
            return null;
        }
    }

    public ResumeAiInsights analyzeResume(String resumeText) {
        if (!isAvailable() || resumeText == null || resumeText.isBlank()) {
            return null;
        }

        String prompt = """
                You are a professional resume-screening assistant. Analyze the resume below.
                Return only valid JSON with this exact shape:
                {"summary":"short professional profile","strengths":["strength"],"recommendations":["specific improvement"]}
                Use 3 to 6 concise strengths and 2 to 4 practical recommendations. Do not use markdown.

                RESUME:
                %s
                """.formatted(resumeText);

        try {
            ChatModel chatModel = chatModelProvider.getIfAvailable();
            if (chatModel == null) {
                return null;
            }
            String content = chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
            ResumeAiInsights insights = objectMapper.readValue(content, ResumeAiInsights.class);
            insights.setStrengths(insights.getStrengths() != null ? insights.getStrengths() : List.of());
            insights.setRecommendations(insights.getRecommendations() != null ? insights.getRecommendations() : List.of());
            return insights;
        } catch (Exception exception) {
            return null;
        }
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }
}