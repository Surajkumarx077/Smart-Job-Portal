package com.smartjobportal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjobportal.dto.resume.ParsedResumeResponse;
import com.smartjobportal.security.UserPrincipal;
import com.smartjobportal.service.ResumeParserService;
import com.smartjobportal.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeParserService resumeParserService;
    private final ObjectMapper objectMapper;

    public ResumeController(ResumeService resumeService,
                            ResumeParserService resumeParserService,
                            ObjectMapper objectMapper) {
        this.resumeService = resumeService;
        this.resumeParserService = resumeParserService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        String url = resumeService.uploadResume(file, principal.getId());
        return ResponseEntity.ok(Map.of("resumeUrl", url));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getMyResume(
            @AuthenticationPrincipal UserPrincipal principal) {
        String url = resumeService.getResumeUrl(principal.getId());
        return ResponseEntity.ok(Map.of("resumeUrl", url));
    }

    @GetMapping("/parsed")
    public ResponseEntity<ParsedResumeResponse> getParsedResume(
            @AuthenticationPrincipal UserPrincipal principal) {
        return resumeParserService.getParsedResume(principal.getId())
                .map(parsed -> {
                    ParsedResumeResponse resp = new ParsedResumeResponse();
                    resp.setSkills(parsed.getSkills());
                    resp.setExperienceSummary(parsed.getExperienceSummary());
                    resp.setEducation(parsed.getEducation());
                    resp.setAiSummary(parsed.getAiSummary());
                    resp.setAiStrengths(parseList(parsed.getAiStrengths()));
                    resp.setAiRecommendations(parseList(parsed.getAiRecommendations()));
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private List<String> parseList(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException exception) {
            return List.of();
        }
    }
}
