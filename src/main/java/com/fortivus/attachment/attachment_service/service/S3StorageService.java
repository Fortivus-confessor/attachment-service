package com.fortivus.attachment.attachment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${seaweedfs.bucket:fortivus-attachments}")
    private String bucketName;

    @PostConstruct
    public void initBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket {} found in SeaweedFS.", bucketName);
        } catch (Exception e) {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                log.info("Bucket {} created in SeaweedFS.", bucketName);
            } catch (Exception ex) {
                log.warn("Could not create bucket: {}", ex.getMessage());
            }
        }
    }

    public String generatePresignedUploadUrl(String fileKey, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
        );

        String url = presignedRequest.url().toString();
        if (url.contains("seaweedfs")) {
            url = url.replaceAll("seaweedfs(:\\d+)?", "localhost$1");
        }
        return url;
    }

    public String generatePresignedDownloadUrl(String fileKey) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(r -> r
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(objectRequest)
        );

        String url = presignedRequest.url().toString();
        // Substitui o host interno do docker pelo localhost se contiver seaweedfs (solução para dev)
        if (url.contains("seaweedfs")) {
            url = url.replaceAll("seaweedfs(:\\d+)?", "localhost$1");
        }
        return url;
    }

    public void uploadFile(String fileKey, byte[] content, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();
        s3Client.putObject(objectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(content));
    }

    public void deleteFile(String fileKey) {
        s3Client.deleteObject(b -> b.bucket(bucketName).key(fileKey));
    }
}
