package com.smartjobportal.service;

import com.smartjobportal.dto.job.JobRequest;
import com.smartjobportal.dto.job.JobResponse;
import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.Role;
import com.smartjobportal.entity.User;
import com.smartjobportal.repository.JobRepository;
import com.smartjobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobResponse createJob(JobRequest request, Long recruiterId) {
        User recruiter = userRepository.findById(Objects.requireNonNull(recruiterId, "recruiterId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (recruiter.getRole() != Role.RECRUITER && recruiter.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only recruiters can post jobs");
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setRecruiter(recruiter);
        job.setActive(true);
        job = jobRepository.save(job);

        return toResponse(job);
    }

    public List<JobResponse> getActiveJobs() {
        return jobRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getJobsByRecruiter(Long recruiterId) {
        User recruiter = userRepository.findById(Objects.requireNonNull(recruiterId, "recruiterId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jobRepository.findByRecruiter(recruiter).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        return toResponse(job);
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request, Long recruiterId) {
        Job job = jobRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to update this job");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job = jobRepository.save(job);
        return toResponse(job);
    }

    @Transactional
    public void deleteJob(Long id, Long userId) {
        Job job = jobRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        User user = userRepository.findById(Objects.requireNonNull(userId, "userId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!job.getRecruiter().getId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Not authorized to delete this job");
        }
        jobRepository.delete(job);
    }

    public JobResponse toResponse(Job job) {
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
