/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import com.azeem.blueprint.demo.DemoDatasetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
  private static final Logger log = LoggerFactory.getLogger(BillingController.class);
  DemoDatasetLoader datasetService;

  public DemoController(DemoDatasetLoader datasetService) {
    this.datasetService = datasetService;
  }

  /**
   * Trigger ingestion of the built-in demonstration dataset. This allows users to test the ETL
   * pipeline and analytics without providing their own CSV.
   */
  @PostMapping("/demo-load")
  public ResponseEntity<String> loadDemoData() {
    log.info("POST /demo-load called. Triggering dummy data ingestion.");
    datasetService.loadDemoData();
    return ResponseEntity.ok("Demo data loaded. You can now use the analytics endpoints.");
  }
}
