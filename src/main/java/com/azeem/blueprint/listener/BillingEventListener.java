/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.listener;

import com.azeem.blueprint.model.billing.IngestionResult;
import com.azeem.blueprint.service.alarm.AlarmService;
import com.azeem.blueprint.service.billing.BillingIngestionService;
import com.azeem.blueprint.service.billing.BillingS3Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BillingEventListener {
  private static final Logger log = LoggerFactory.getLogger(BillingEventListener.class);

  private final BillingS3Service s3Service;
  private final BillingIngestionService ingestionService;
  private final ObjectMapper objectMapper;

  public BillingEventListener(
      BillingS3Service s3Service,
      BillingIngestionService ingestionService,
      AlarmService alarmService,
      ObjectMapper objectMapper) {
    this.s3Service = s3Service;
    this.ingestionService = ingestionService;
    this.objectMapper = objectMapper;
  }

  @SqsListener("billing-event-queue")
  public void onS3Event(String message) {
    try {
      JsonNode root = objectMapper.readTree(message);

      JsonNode records = root.path("Records");

      if (!records.isArray() || records.isEmpty()) {
        return;
      }

      JsonNode s3Node = records.get(0).path("s3");

      String bucket = s3Node.path("bucket").path("name").asText();
      String key = s3Node.path("object").path("key").asText();

      if (bucket.isEmpty() || key.isEmpty()) {
        log.warn("Received SQS message but could not find S3 metadata. Message: {}", message);
        return;
      }

      log.info("Triggering Ingestion for S3 Object: s3://{}/{}", bucket, key);

      try (InputStream s3Stream = s3Service.getBillingDataStream(bucket, key)) {
        IngestionResult result = ingestionService.ingestData(s3Stream);

        if (result.failureCount() > 0) {
          s3Service.uploadErrorLog(bucket, result.billingPeriod(), result.errorLog());
        }

        log.info("Event-driven ingestion successful for {}", key);
      }

    } catch (Exception e) {
      log.error("Failed to process S3 event from SQS", e);
    }
  }
}
