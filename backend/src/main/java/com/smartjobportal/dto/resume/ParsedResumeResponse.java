package com.smartjobportal.dto.resume;

public class ParsedResumeResponse {

    private String skills;
    private String experienceSummary;
    private String education;

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getExperienceSummary() { return experienceSummary; }
    public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
}
