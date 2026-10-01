package com.smartjobportal.dto.resume;

import java.util.List;

public class ParsedResumeResponse {

    private String skills;
    private String experienceSummary;
    private String education;
    private String aiSummary;
    private List<String> aiStrengths;
    private List<String> aiRecommendations;

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getExperienceSummary() { return experienceSummary; }
    public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public List<String> getAiStrengths() { return aiStrengths; }
    public void setAiStrengths(List<String> aiStrengths) { this.aiStrengths = aiStrengths; }
    public List<String> getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(List<String> aiRecommendations) { this.aiRecommendations = aiRecommendations; }
}
