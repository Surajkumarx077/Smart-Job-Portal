package com.smartjobportal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smartjobportal.dto.ranking.RankedCandidateResponse;
import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.JobApplication;
import com.smartjobportal.repository.JobApplicationRepository;
import com.smartjobportal.repository.JobRepository;

@Service
public class CandidateRankingService {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[a-zA-Z]{2,}\\b");
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "for", "with", "from", "have", "has", "had", "this", "that",
            "these", "those", "will", "would", "could", "should", "experience", "education",
            "work", "project", "team", "year", "years", "need", "required", "apply"
    );

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeParserService resumeParserService;
    private final GeminiScreeningService geminiScreeningService;

    public CandidateRankingService(JobApplicationRepository applicationRepository,
                                  JobRepository jobRepository,
                                  ResumeParserService resumeParserService,
                                  GeminiScreeningService geminiScreeningService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.resumeParserService = resumeParserService;
        this.geminiScreeningService = geminiScreeningService;
    }

    public List<RankedCandidateResponse> getRankedCandidates(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(Objects.requireNonNull(jobId, "jobId must not be null"))
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to view candidates for this job");
        }

        List<JobApplication> applications = applicationRepository.findByJob(job);
        Set<String> jobKeywords = extractKeywords(job.getTitle() + " " + (job.getDescription() != null ? job.getDescription() : ""));

        String jobText = job.getTitle() + " " + (job.getDescription() != null ? job.getDescription() : "");

        List<RankedCandidateResponse> ranked = new ArrayList<>();
        for (JobApplication app : applications) {
            String resumeText = resumeParserService.getSearchableText(app.getApplicant().getId());
            double score;
            List<String> matchedSkills;

            if (geminiScreeningService.isAvailable()) {
                var aiResponse = geminiScreeningService.getScore(jobText, resumeText);
                if (aiResponse != null) {
                    score = aiResponse.getScore();
                    matchedSkills = aiResponse.getMatchedSkills() != null ? aiResponse.getMatchedSkills() : List.of();
                } else {
                    score = computeMatchScore(jobKeywords, resumeText);
                    matchedSkills = getMatchedSkills(jobKeywords, resumeText);
                }
            } else {
                score = computeMatchScore(jobKeywords, resumeText);
                matchedSkills = getMatchedSkills(jobKeywords, resumeText);
            }

            RankedCandidateResponse response = new RankedCandidateResponse();
            response.setApplicationId(app.getId());
            response.setApplicantId(app.getApplicant().getId());
            response.setApplicantName(app.getApplicant().getFullName());
            response.setApplicantEmail(app.getApplicant().getEmail());
            response.setResumeUrl(app.getApplicant().getResumeUrl());
            response.setCoverLetter(app.getCoverLetter());
            response.setAppliedAt(app.getCreatedAt());
            response.setMatchScore(Math.round(score * 100.0) / 100.0);
            response.setMatchedSkills(matchedSkills);
            ranked.add(response);
        }

        return ranked.stream()
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());
    }

    private Set<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) return Collections.emptySet();
        return WORD_PATTERN.matcher(text.toLowerCase())
                .results()
                .map(m -> m.group().toLowerCase())
                .filter(w -> !STOP_WORDS.contains(w) && w.length() >= 2)
                .collect(Collectors.toSet());
    }

    private double computeMatchScore(Set<String> jobWords, String resumeText) {
        if (jobWords.isEmpty()) return 0.0;

        String resumeLower = (resumeText != null ? resumeText : "").toLowerCase();
        long matched = jobWords.stream()
                .filter(resumeLower::contains)
                .count();

        double ratio = (double) matched / jobWords.size();
        return Math.min(100.0, ratio * 100.0);
    }

    private List<String> getMatchedSkills(Set<String> jobKeywords, String resumeText) {
        if (resumeText == null) return List.of();
        String lower = resumeText.toLowerCase();
        return jobKeywords.stream()
                .filter(lower::contains)
                .sorted()
                .limit(20)
                .collect(Collectors.toList());
    }
}
