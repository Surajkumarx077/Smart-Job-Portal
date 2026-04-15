package com.smartjobportal.controller;

import com.smartjobportal.dto.job.JobRequest;
import com.smartjobportal.dto.job.JobResponse;
import com.smartjobportal.security.UserPrincipal;
import com.smartjobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<JobResponse>> getActiveJobs() {
        return ResponseEntity.ok(jobService.getActiveJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(jobService.createJob(request, principal.getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<JobResponse>> getMyJobs(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(principal.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id,
                                                 @Valid @RequestBody JobRequest request,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(jobService.updateJob(id, request, principal.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id,
                                          @AuthenticationPrincipal UserPrincipal principal) {
        jobService.deleteJob(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
