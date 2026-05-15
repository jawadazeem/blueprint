/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import com.azeem.blueprint.demo.LoadDummyDataService;
import com.azeem.blueprint.model.billing.BillingRecord;
import com.azeem.blueprint.model.billing.BillingSummary;
import com.azeem.blueprint.service.billing.BillingS3Service;
import com.azeem.blueprint.service.billing.BillingService;
import com.azeem.blueprint.validation.BillingPeriod;
import com.azeem.blueprint.validation.ValidCsvFile;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller exposing endpoints for billing data retrieval and summary generation.
 *
 * <p>Endpoints:
 *
 * <ul>
 *   <li>POST /upload - Upload a csv file that contains billing records to the application
 *   <li>GET /records - Retrieve all billing records.
 *   <li>GET /summary - Get aggregated billing summary.
 *   <li>GET /records/department/{department} - Get billing records filtered by department.
 *   <li>GET /top/{n} - Get top N billing records by total charge.
 *   <li>GET /departments - List all unique departments.
 *   <li>GET /periods - List all available billing periods.
 *   <li>GET /records/period/{billingPeriod} - Get billing records for specified billing period.
 *   <li>GET /summary/period/{billingPeriod} - Get billing summary for specified billing period.
 * </ul>
 */
@RestController
@Validated
public class BillingController {
  private static final Logger log = LoggerFactory.getLogger(BillingController.class);
  private final BillingService service;
  private final BillingS3Service s3Service;
  private final LoadDummyDataService dummyDataService;

  public BillingController(
      BillingService service, BillingS3Service s3Service, LoadDummyDataService dummyDataService) {
    this.service = service;
    this.s3Service = s3Service;
    this.dummyDataService = dummyDataService;
  }

  @PostMapping("/upload")
  public ResponseEntity<String> handleFileUpload(
      @ValidCsvFile @RequestParam("file") MultipartFile file) {

    s3Service.uploadUserFile("telecom-billing", file);

    return ResponseEntity.ok("File received. Processing has started in the background.");
  }

  /**
   * Trigger ingestion of the built-in demonstration dataset. This allows users to test the ETL
   * pipeline and analytics without providing their own CSV.
   */
  @PostMapping("/demo-load")
  public ResponseEntity<String> loadDemoData() {
    log.info("POST /demo-load called. Triggering dummy data ingestion.");
    dummyDataService.loadDummyData();
    return ResponseEntity.ok("Demo data loaded. You can now use the analytics endpoints.");
  }

  @GetMapping("/records")
  public Page<BillingRecord> getAllRecords(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    log.info(
        "GET /records called to retrieve all billing records, page: {}, size: {}.", page, size);
    return service.getAllRecords(page, size);
  }

  @GetMapping("/summary")
  public ResponseEntity<BillingSummary> getSummary() {
    log.info("GET /summary called to generate billing summary.");
    return ResponseEntity.ok(service.generateSummary());
  }

  @GetMapping("/records/department/{department}")
  public ResponseEntity<Page<BillingRecord>> getRecordsByDepartment(
      @PathVariable @NotBlank String department,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    log.info("API Request: Fetching records for department: {}", department);
    Page<BillingRecord> result = service.getRecordsByDepartment(department, page, size);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/top/{n}")
  public Page<BillingRecord> getTopN(@PathVariable @Min(1) @Max(100) int n) {
    log.info("GET /top/{} called to retrieve top N billing records by total charge.", n);
    return service.getTopNRecords(n);
  }

  @GetMapping("/departments")
  public List<String> getDepartments() {
    log.info("GET /departments called.");
    return service.getDistinctDepartments();
  }

  @GetMapping("/periods")
  public List<String> getBillingPeriods() {
    log.info("GET /periods called.");
    return service.getDistinctBillingPeriods();
  }

  @GetMapping("/records/period/{billingPeriod}")
  public Page<BillingRecord> getRecordsByPeriod(
      @BillingPeriod @PathVariable String billingPeriod,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    log.info("GET /records/period/{} called with page {}, size {}", billingPeriod, page, size);
    return service.getRecordsByPeriod(billingPeriod, page, size);
  }

  @GetMapping("/summary/period/{billingPeriod}")
  public BillingSummary getSummaryByPeriod(@BillingPeriod @PathVariable String billingPeriod) {
    log.info("GET /summary/period/{} called.", billingPeriod);
    return service.generateSummaryForPeriod(billingPeriod);
  }
}
