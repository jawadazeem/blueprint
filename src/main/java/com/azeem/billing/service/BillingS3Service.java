/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.service;

import com.azeem.billing.exception.BillingDataNotFoundException;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class BillingS3Service {
    private static final Logger log = LoggerFactory.getLogger(BillingS3Service.class);
    private final S3Template s3Template;
    private final S3Operations s3Operations;

    public BillingS3Service(S3Template s3Template, S3Operations s3Operations) {
        this.s3Template = s3Template;
        this.s3Operations = s3Operations;
    }

    public InputStream getBillingDataStream(String bucketName, String key) {
        log.info("Fetching billing data from S3 bucket: {} with key: {}", bucketName, key);

        S3Resource resource = s3Template.download(bucketName, key);

        if (!resource.exists()) {
            throw new BillingDataNotFoundException("Billing file not found in S3: " + key);
        }

        try {
            return resource.getInputStream();
        } catch (IOException e) {
            log.error("Failed to open InputStream for S3 object: {}", key, e);
            throw new RuntimeException("Error accessing S3 stream", e);
        }
    }

    public void uploadUserFile(String bucketName, MultipartFile file) {
        try {
            String key = file.getOriginalFilename();

            if (s3Operations.objectExists(bucketName, key)) {
                log.warn("File {} already exists in bucket {}, "
                        + "the file cannot be uploaded.", key, bucketName
                );

                return;
            }

            log.info("Uploading user file {} to bucket {}", key, bucketName);

            // Streaming directly from the Multipart request to S3
            s3Template.upload(bucketName, key, file.getInputStream());

        } catch (IOException e) {
            log.error("Failed to stream upload to S3", e);
            throw new RuntimeException("S3 Upload Failed");
        }
    }

    public void uploadErrorLog(String bucketName, String billingPeriod, String errorLogContent) {
        String errorKey = String.format("error-logs/%s-errors.log", billingPeriod);

        try {
            InputStream inputStream = new ByteArrayInputStream(
                    errorLogContent.getBytes(StandardCharsets.UTF_8));
            s3Template.upload(bucketName, errorKey, inputStream);
            log.info("Successfully uploaded error log for billing period: {}", billingPeriod);
        } catch (Exception e) {
            log.error("Failed to upload error log to S3 for billing period: {}", billingPeriod, e);
            throw new RuntimeException("Error uploading error log to S3", e);
        }
    }


}