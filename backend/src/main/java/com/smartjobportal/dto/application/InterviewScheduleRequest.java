package com.smartjobportal.dto.application;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InterviewScheduleRequest {

    @NotNull
    @Future
    private Instant interviewScheduledAt;

    @NotBlank
    @Size(max = 500)
    private String onlineMeetingLink;

    @Size(max = 2000)
    private String interviewNotes;

    public Instant getInterviewScheduledAt() {
        return interviewScheduledAt;
    }

    public void setInterviewScheduledAt(Instant interviewScheduledAt) {
        this.interviewScheduledAt = interviewScheduledAt;
    }

    public String getOnlineMeetingLink() {
        return onlineMeetingLink;
    }

    public void setOnlineMeetingLink(String onlineMeetingLink) {
        this.onlineMeetingLink = onlineMeetingLink;
    }

    public String getInterviewNotes() {
        return interviewNotes;
    }

    public void setInterviewNotes(String interviewNotes) {
        this.interviewNotes = interviewNotes;
    }
}
