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
import org.springframework.stereotype.Service;

/**
 * Class used for loading dummy data for demonstration purposes. Used by those who may not have a
 * properly formatted CSV file to run analytics on
 */
@Service
public class LoadDummyDataService {
  Logger log = LoggerFactory.getLogger(LoadDummyDataService.class);
  private final BillingIngestionService billingIngestionService;
  private final BillingRecordRepository billingRecordRepository;
  private final UUID DUMMY_DATA_DATASET_ID = new UUID(0L, 0L);

  public LoadDummyDataService(
      BillingIngestionService billingIngestionService,
      BillingRecordRepository billingRecordRepository) {
    this.billingIngestionService = billingIngestionService;
    this.billingRecordRepository = billingRecordRepository;
  }

  public void loadDummyData() {
    if (isLoaded()) {
      log.info("Dummy data already loaded, cannot load again.");
      return;
    }

    ClassPathResource resource = new ClassPathResource("dummy-data.csv");
    try (InputStream is = resource.getInputStream()) {
      log.info("Loading dummy data from: {}", resource.getFilename());
      billingIngestionService.ingestData(DUMMY_DATA_DATASET_ID, is);
    } catch (IOException e) {
      log.error("Dummy data ingestion failed: {}", e.getMessage());
    }
  }

  private boolean isLoaded() {
    return billingRecordRepository.existsByDatasetIdAndBillingPeriod(
        DUMMY_DATA_DATASET_ID, "dummy-data");
  }
}
