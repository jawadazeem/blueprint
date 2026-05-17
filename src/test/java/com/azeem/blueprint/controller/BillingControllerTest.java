/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.azeem.blueprint.demo.LoadDummyDataService;
import com.azeem.blueprint.model.billing.BillingRecord;
import com.azeem.blueprint.model.billing.BillingSummary;
import com.azeem.blueprint.service.billing.BillingQueryService;
import com.azeem.blueprint.service.billing.BillingS3Service;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BillingController.class)
class BillingControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private BillingQueryService billingQueryService;
  @MockitoBean private BillingS3Service s3Service;
  @MockitoBean private LoadDummyDataService dummyDataService;

  @Test
  void shouldAcceptMultipartFile() throws Exception {
    // arrange
    byte[] bytes = new byte[16];
    new Random().nextBytes(bytes);

    MockMultipartFile file = new MockMultipartFile("file", "2026-01.csv", "text/csv", bytes);

    // act & assert
    mockMvc.perform(multipart("/upload").file(file)).andExpect(status().isOk());

    verify(s3Service)
        .uploadUserFile(anyString(), argThat(f -> f.getOriginalFilename().equals("2026-01.csv")));
  }

  @Test
  void shouldLoadDemoData() throws Exception {
    // arrange, act & assert
    mockMvc.perform(post("/demo-load")).andExpect(status().isOk());

    verify(dummyDataService).loadDummyData();
  }

  @Test
  void defaultPageAndSize_shouldReturnAllRecordsInDefaultSizedPage() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/records")).andExpect(status().isOk());

    verify(billingQueryService).getAllRecords(eq(0), eq(20));
  }

  @Test
  void customPageAndSize_shouldReturnAllRecordsInCustomSizedPage() throws Exception {
    // arrange, act & assert
    mockMvc
        .perform(get("/records").param("page", "1").param("size", "1"))
        .andExpect(status().isOk());

    verify(billingQueryService).getAllRecords(1, 1);
  }

  @Test
  void shouldReturnGeneratedBillingSummary() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/summary")).andExpect(status().isOk());

    verify(billingQueryService).generateSummary();
  }

  @Test
  void customPageAndSize_shouldReturnRecordsByDepartment() throws Exception {
    // arrange, act & assert
    mockMvc
        .perform(get("/records/department/IT").param("page", "1").param("size", "20"))
        .andExpect(status().isOk());

    verify(billingQueryService).getRecordsByDepartment(anyString(), eq(1), eq(20));
  }

  @Test
  void defaultPageAndSize_shouldReturnRecordsByDepartment() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/records/department/IT")).andExpect(status().isOk());

    verify(billingQueryService).getRecordsByDepartment(eq("IT"), eq(0), eq(20));
  }

  @Test
  void shouldReturnTopNBillingRecordsBySpend() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/top/5")).andExpect(status().isOk());

    verify(billingQueryService).getTopNRecords(eq(5));
  }

  // N cannot be greater than 100 (@Max(100) validation)
  @Test
  void shouldGracefullyHandleTooHighN_TopNBillingRecordsBySpend() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/top/101")).andExpect(status().isBadRequest());

    verify(billingQueryService, never()).getTopNRecords(anyInt());
  }

  @Test
  void shouldReturnAllDepartments() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/departments")).andExpect(status().isOk());

    verify(billingQueryService).getDistinctDepartments();
  }

  @Test
  void shouldReturnAllPeriods() throws Exception {
    // arrange, act & assert
    mockMvc.perform(get("/periods")).andExpect(status().isOk());

    verify(billingQueryService).getDistinctBillingPeriods();
  }

  @Test
  void defaultPageAndSize_shouldReturnBillingRecordsByPeriod() throws Exception {
    // arrange
    Page<BillingRecord> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 1);
    when(billingQueryService.getRecordsByPeriod(anyString(), anyInt(), anyInt())).thenReturn(page);

    // act & assert
    mockMvc.perform(get("/records/period/2026-01")).andExpect(status().isOk());
    verify(billingQueryService).getRecordsByPeriod("2026-01", 0, 20);
  }

  @Test
  void customPageAndSize_shouldReturnBillingRecordsByPeriod() throws Exception {

    when(billingQueryService.getRecordsByPeriod(anyString(), anyInt(), anyInt()))
        .thenReturn(Page.empty());

    mockMvc
        .perform(get("/records/period/2026-01").param("page", "1").param("size", "1"))
        .andExpect(status().isOk());

    verify(billingQueryService).getRecordsByPeriod("2026-01", 1, 1);
  }

  @Test
  void shouldReturnGeneratedBillingSummaryByPeriod() throws Exception {
    // arrange
    BillingSummary summary = new BillingSummary();
    when(billingQueryService.generateSummaryForPeriod("2026-01")).thenReturn(summary);

    // act & assert
    mockMvc.perform(get("/summary/period/2026-01")).andExpect(status().isOk());
    verify(billingQueryService).generateSummaryForPeriod("2026-01");
  }

  @Test
  void handleInvalidBillingPeriodFormat() throws Exception {

    BillingSummary summary = new BillingSummary();

    mockMvc.perform(get("/summary/period/70-2070")).andExpect(status().isBadRequest());

    verify(billingQueryService, never()).generateSummaryForPeriod(anyString());
  }

  @Test
  void shouldDeleteRecordsByPeriod() throws Exception {
    // arrange
    when(billingQueryService.deleteRecordsByPeriod("2026-01")).thenReturn(5);

    // act & assert
    mockMvc.perform(delete("/records/period/2026-01")).andExpect(status().isNoContent());

    verify(billingQueryService).deleteRecordsByPeriod("2026-01");
  }
}
