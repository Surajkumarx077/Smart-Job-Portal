package com.smartjobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    private static final String LOCAL_UPLOAD_DIR = "uploads/resumes";

    public String uploadResume(InputStream inputStream, long contentLength, String contentType, Long userId) {
        if (accessKeyId == null || accessKeyId.isEmpty() || secretAccessKey == null || secretAccessKey.isEmpty()) {
            return uploadToLocal(inputStream, userId);
        }

        try {
            S3Client s3 = S3Client.builder()
                    .region(Region.of(region))
                    .build();

            String key = "resumes/" + userId + "/" + UUID.randomUUID() + ".pdf";

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType != null ? contentType : "application/pdf")
                    .build();

            s3.putObject(putRequest, RequestBody.fromInputStream(inputStream, contentLength));

            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload resume: " + e.getMessage());
        }
    }

    private String uploadToLocal(InputStream inputStream, Long userId) {
        try {
            Path dir = Paths.get(LOCAL_UPLOAD_DIR, userId.toString());
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + ".pdf";
            Path file = dir.resolve(filename);
            Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
            return "/" + LOCAL_UPLOAD_DIR + "/" + userId + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume: " + e.getMessage());
        }
    }
}
