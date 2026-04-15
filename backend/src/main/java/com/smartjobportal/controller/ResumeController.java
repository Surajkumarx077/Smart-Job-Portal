package com.smartjobportal.controller;

import com.smartjobportal.dto.resume.ParsedResumeResponse;
import com.smartjobportal.security.UserPrincipal;
import com.smartjobportal.service.ResumeParserService;
import com.smartjobportal.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeParserService resumeParserService;

    public ResumeController(ResumeService resumeService, ResumeParserService resumeParserService) {
        this.resumeService = resumeService;
        this.resumeParserService = resumeParserService;
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
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
