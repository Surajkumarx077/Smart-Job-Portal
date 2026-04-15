package com.smartjobportal.dto.ai;

import java.util.List;

public class AiScoreResponse {

    private double score;
    private List<String> matchedSkills;
    private List<String> extractedSkills;

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public List<String> getExtractedSkills() { return extractedSkills; }
    public void setExtractedSkills(List<String> extractedSkills) { this.extractedSkills = extractedSkills; }
}
