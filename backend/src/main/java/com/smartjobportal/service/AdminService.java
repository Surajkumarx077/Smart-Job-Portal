package com.smartjobportal.service;

import com.smartjobportal.dto.admin.AdminStatsResponse;
import com.smartjobportal.dto.admin.AdminUserResponse;
import com.smartjobportal.dto.application.ApplicationResponse;
import com.smartjobportal.dto.job.JobResponse;
import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.Role;
import com.smartjobportal.entity.User;
import com.smartjobportal.repository.JobApplicationRepository;
import com.smartjobportal.repository.JobRepository;
import com.smartjobportal.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    public AdminService(UserRepository userRepository, JobRepository jobRepository,
                        JobApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public AdminStatsResponse getStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalJobs(jobRepository.count());
        stats.setTotalApplications(applicationRepository.count());

        Map<String, Long> byRole = new HashMap<>();
        for (Role role : Role.values()) {
            byRole.put(role.name(), userRepository.countByRole(role));
        }
        stats.setUsersByRole(byRole);
        return stats;
    }

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(Objects.requireNonNull(pageable, "pageable must not be null")).map(this::toUserResponse);
    }

    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(app -> {
                    ApplicationResponse resp = new ApplicationResponse();
                    resp.setId(app.getId());
                    resp.setJobId(app.getJob().getId());
                    resp.setJobTitle(app.getJob().getTitle());
                    resp.setApplicantId(app.getApplicant().getId());
                    resp.setApplicantName(app.getApplicant().getFullName());
                    resp.setApplicantEmail(app.getApplicant().getEmail());
                    resp.setResumeUrl(app.getApplicant().getResumeUrl());
                    resp.setCoverLetter(app.getCoverLetter());
                    resp.setCreatedAt(app.getCreatedAt());
                    return resp;
                })
                .collect(Collectors.toList());
    }

    private AdminUserResponse toUserResponse(User user) {
        AdminUserResponse resp = new AdminUserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setFullName(user.getFullName());
        resp.setRole(user.getRole());
        resp.setHasResume(user.getResumeUrl() != null && !user.getResumeUrl().isEmpty());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }

    private JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompany(),
                job.getLocation(),
                job.getJobType(),
                job.getRecruiter().getId(),
                job.getRecruiter().getFullName(),
                job.getCreatedAt(),
                job.getActive()
        );
    }
}
