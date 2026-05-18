/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import com.azeem.blueprint.service.dataset.demo.DemoDatasetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
  private static final Logger log = LoggerFactory.getLogger(DemoController.class);
  private final DemoDatasetLoader demoDatasetLoader;

  public DemoController(DemoDatasetLoader demoDatasetLoader) {
    this.demoDatasetLoader = demoDatasetLoader;
  }

  @PostMapping("/demo-dataset")
  public ResponseEntity<String> loadDemoData() {
    log.info("POST /demo-dataset called. Triggering demo data ingestion.");
    demoDatasetLoader.loadDemoData();
    return ResponseEntity.ok("Demo data loaded. You can now use the analytics endpoints.");
  }
}
