package com.smartjobportal.repository;

import com.smartjobportal.entity.ParsedResume;
import com.smartjobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParsedResumeRepository extends JpaRepository<ParsedResume, Long> {

    Optional<ParsedResume> findByUser(User user);

    Optional<ParsedResume> findByUserId(Long userId);

    void deleteByUser(User user);
}
