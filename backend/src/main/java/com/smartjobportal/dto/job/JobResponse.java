package com.smartjobportal.dto.job;

import java.time.Instant;

public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String company;
    private String location;
    private String jobType;
    private Long recruiterId;
    private String recruiterName;
    private Instant createdAt;
    private Boolean active;

    public JobResponse() {}

    public JobResponse(Long id, String title, String description, String company, String location,
                       String jobType, Long recruiterId, String recruiterName, Instant createdAt, Boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.jobType = jobType;
        this.recruiterId = recruiterId;
        this.recruiterName = recruiterName;
        this.createdAt = createdAt;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public Long getRecruiterId() { return recruiterId; }
    public void setRecruiterId(Long recruiterId) { this.recruiterId = recruiterId; }
    public String getRecruiterName() { return recruiterName; }
    public void setRecruiterName(String recruiterName) { this.recruiterName = recruiterName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
