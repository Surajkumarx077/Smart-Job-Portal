package com.smartjobportal.dto.resume;

import java.util.List;

public class ResumeAiInsights {

    private String summary;
    private List<String> strengths;
    private List<String> recommendations;

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}