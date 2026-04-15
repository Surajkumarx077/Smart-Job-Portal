package com.smartjobportal.dto.application;

import java.time.Instant;

import com.smartjobportal.entity.ApplicationStatus;

public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private Instant shortlistedAt;
    private Instant interviewScheduledAt;
    private String onlineMeetingLink;
    private String interviewNotes;
    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Instant getShortlistedAt() { return shortlistedAt; }
    public void setShortlistedAt(Instant shortlistedAt) { this.shortlistedAt = shortlistedAt; }
    public Instant getInterviewScheduledAt() { return interviewScheduledAt; }
    public void setInterviewScheduledAt(Instant interviewScheduledAt) { this.interviewScheduledAt = interviewScheduledAt; }
    public String getOnlineMeetingLink() { return onlineMeetingLink; }
    public void setOnlineMeetingLink(String onlineMeetingLink) { this.onlineMeetingLink = onlineMeetingLink; }
    public String getInterviewNotes() { return interviewNotes; }
    public void setInterviewNotes(String interviewNotes) { this.interviewNotes = interviewNotes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
