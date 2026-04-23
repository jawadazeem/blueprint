/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.service;

import com.azeem.billing.exception.BillingDataNotFoundException;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;

@Service
public class BillingS3Service {
    private static final Logger log = LoggerFactory.getLogger(BillingS3Service.class);
    private final S3Template s3Template;

    public BillingS3Service(S3Template s3Template) {
        this.s3Template = s3Template;
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


}