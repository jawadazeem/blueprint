/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.demo;

import com.azeem.blueprint.repository.BillingRecordRepository;
import com.azeem.blueprint.service.billing.BillingIngestionService;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Class used for loading demo data for demonstration purposes. Used by those who may not have a
 * properly formatted CSV file to run analytics on
 */
@Component
public class DemoDatasetLoader {
  Logger log = LoggerFactory.getLogger(DemoDatasetLoader.class);
  private final BillingIngestionService billingIngestionService;
  private final BillingRecordRepository billingRecordRepository;
  private final UUID DUMMY_DATA_DATASET_ID = new UUID(0L, 0L);

  public DemoDatasetLoader(
      BillingIngestionService billingIngestionService,
      BillingRecordRepository billingRecordRepository) {
    this.billingIngestionService = billingIngestionService;
    this.billingRecordRepository = billingRecordRepository;
  }

  public void loadDemoData() {
    if (isLoaded()) {
      log.info("Demo data already loaded, cannot load again.");
      return;
    }

    ClassPathResource resource = new ClassPathResource("demo-data.csv");
    try (InputStream is = resource.getInputStream()) {
      log.info("Loading demo data from: {}", resource.getFilename());
      billingIngestionService.ingestData(DUMMY_DATA_DATASET_ID, is);
    } catch (IOException e) {
      log.error("Demo data ingestion failed: {}", e.getMessage());
    }
  }

  private boolean isLoaded() {
    return billingRecordRepository.existsByDatasetIdAndBillingPeriod(
        DUMMY_DATA_DATASET_ID, "demo-data");
  }
}
