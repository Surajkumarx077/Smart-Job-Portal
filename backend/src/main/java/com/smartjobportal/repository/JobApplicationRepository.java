package com.smartjobportal.repository;

import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.JobApplication;
import com.smartjobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByApplicant(User applicant);

    List<JobApplication> findByJob(Job job);

    Optional<JobApplication> findByJobAndApplicant(Job job, User applicant);

    boolean existsByJobAndApplicant(Job job, User applicant);
}
