package com.smartjobportal.service;

import com.smartjobportal.entity.User;
import com.smartjobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ResumeService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList("application/pdf");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final ResumeParserService resumeParserService;

    public ResumeService(UserRepository userRepository, S3Service s3Service, ResumeParserService resumeParserService) {
        this.userRepository = userRepository;
        this.s3Service = s3Service;
        this.resumeParserService = resumeParserService;
    }

    @Transactional
    public String uploadResume(MultipartFile file, Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId, "userId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("File size must be less than 10MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + e.getMessage());
        }

        try {
            resumeParserService.parseAndSave(bytes, user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse resume: " + e.getMessage());
        }

        String url = s3Service.uploadResume(
                new ByteArrayInputStream(bytes),
                bytes.length,
                file.getContentType(),
                userId
        );

        user.setResumeUrl(url);
        userRepository.save(user);

        return url;
    }

    public String getResumeUrl(Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId, "userId must not be null"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getResumeUrl() != null ? user.getResumeUrl() : "";
    }
}
