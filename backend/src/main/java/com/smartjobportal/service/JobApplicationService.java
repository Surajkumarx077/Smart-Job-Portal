package com.smartjobportal.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartjobportal.dto.application.ApplicationRequest;
import com.smartjobportal.dto.application.ApplicationResponse;
import com.smartjobportal.dto.application.InterviewScheduleRequest;
import com.smartjobportal.entity.ApplicationStatus;
import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.JobApplication;
import com.smartjobportal.entity.Role;
import com.smartjobportal.entity.User;
import com.smartjobportal.repository.JobApplicationRepository;
import com.smartjobportal.repository.JobRepository;
import com.smartjobportal.repository.UserRepository;

@Service
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public JobApplicationService(JobApplicationRepository applicationRepository,
                                 JobRepository jobRepository,
                                 UserRepository userRepository,
                                 EmailService emailService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ApplicationResponse apply(ApplicationRequest request, Long applicantId) {
        User applicant = userRepository.findById(Objects.requireNonNull(applicantId, "applicantId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (applicant.getRole() != Role.STUDENT && applicant.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only students can apply for jobs");
        }
        if (applicant.getResumeUrl() == null || applicant.getResumeUrl().isEmpty()) {
            throw new RuntimeException("Please upload your resume before applying");
        }

        Job job = jobRepository.findById(Objects.requireNonNull(request.getJobId(), "jobId must not be null"))
                .orElseThrow(() -> new RuntimeException("Job not found: " + request.getJobId()));

        if (applicationRepository.existsByJobAndApplicant(job, applicant)) {
            throw new RuntimeException("You have already applied for this job");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicant(applicant);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(ApplicationStatus.APPLIED);
        application = applicationRepository.save(application);

        emailService.sendApplicationNotificationToRecruiter(
                job.getRecruiter().getEmail(), job.getTitle(), applicant.getFullName());
        emailService.sendApplicationConfirmationToApplicant(
                applicant.getEmail(), job.getTitle(), job.getCompany());

        return toResponse(application);
    }

    public List<ApplicationResponse> getMyApplications(Long applicantId) {
        User applicant = userRepository.findById(Objects.requireNonNull(applicantId, "applicantId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return applicationRepository.findByApplicant(applicant).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse shortlistAndScheduleInterview(Long applicationId,
                                                             InterviewScheduleRequest request,
                                                             Long recruiterId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        if (!application.getJob().getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to schedule interview for this application");
        }

        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        application.setShortlistedAt(Instant.now());
        application.setInterviewScheduledAt(request.getInterviewScheduledAt());
        application.setOnlineMeetingLink(request.getOnlineMeetingLink().trim());
        application.setInterviewNotes(request.getInterviewNotes());

        application = applicationRepository.save(application);

        emailService.sendInterviewScheduleToApplicant(
                application.getApplicant().getEmail(),
                application.getJob().getTitle(),
                application.getInterviewScheduledAt(),
                application.getOnlineMeetingLink()
        );

        return toResponse(application);
    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(Objects.requireNonNull(jobId, "jobId must not be null"))
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to view applications for this job");
        }
        return applicationRepository.findByJob(job).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ApplicationResponse toResponse(JobApplication app) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(app.getId());
        response.setJobId(app.getJob().getId());
        response.setJobTitle(app.getJob().getTitle());
        response.setApplicantId(app.getApplicant().getId());
        response.setApplicantName(app.getApplicant().getFullName());
        response.setApplicantEmail(app.getApplicant().getEmail());
        response.setResumeUrl(app.getApplicant().getResumeUrl());
        response.setCoverLetter(app.getCoverLetter());
        response.setStatus(app.getStatus());
        response.setShortlistedAt(app.getShortlistedAt());
        response.setInterviewScheduledAt(app.getInterviewScheduledAt());
        response.setOnlineMeetingLink(app.getOnlineMeetingLink());
        response.setInterviewNotes(app.getInterviewNotes());
        response.setCreatedAt(app.getCreatedAt());
        return response;
    }
}
