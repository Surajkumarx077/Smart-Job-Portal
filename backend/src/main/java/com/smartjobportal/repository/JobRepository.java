package com.smartjobportal.repository;

import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByRecruiter(User recruiter);

    List<Job> findByActiveTrueOrderByCreatedAtDesc();

    List<Job> findByRecruiterAndActiveTrue(User recruiter, org.springframework.data.domain.Pageable pageable);
}
