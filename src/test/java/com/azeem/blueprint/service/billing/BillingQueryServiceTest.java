/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.azeem.blueprint.entity.BillingRecordEntity;
import com.azeem.blueprint.exception.BillingDataNotFoundException;
import com.azeem.blueprint.exception.QueryLimitExceededException;
import com.azeem.blueprint.mapper.BillingRecordMapper;
import com.azeem.blueprint.model.billing.BillingRecord;
import com.azeem.blueprint.model.billing.BillingSummary;
import com.azeem.blueprint.model.billing.Department;
import com.azeem.blueprint.repository.BillingRecordRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BillingQueryServiceTest {
  @Mock private BillingRecordRepository repository;
  @Mock private BillingRecordMapper mapper;

  @InjectMocks private BillingQueryService service;

  private BillingRecordEntity entity1;
  private BillingRecordEntity entity2;
  private BillingRecord domain1;
  private BillingRecord domain2;

  @BeforeEach
  void setUp() {
    // set max top n limit for data retrieval
    ReflectionTestUtils.setField(service, "maxTopNLimit", 100);

    // Build complete entities & respective (mapped) domains for tests
    BillingRecordEntity entity1 = new BillingRecordEntity();
    entity1.setId(1L);
    entity1.setDepartment(Department.IT.name());
    entity1.setAccountName("Mark Wojick");
    entity1.setEmployeeId("EMP4");
    entity1.setPhoneNumber("7034305396");
    entity1.setBillingPeriod("2026-01");
    entity1.setMinutesUsed(100);
    entity1.setDataGbUsed(20);
    entity1.setSmsCount(100);
    entity1.setTotalCharge(70.00);
    this.entity1 = entity1;

    domain1 =
        new BillingRecord(
            entity1.getAccountName(),
            entity1.getEmployeeId(),
            entity1.getDepartment(),
            entity1.getPhoneNumber(),
            entity1.getBillingPeriod(),
            entity1.getMinutesUsed(),
            entity1.getDataGbUsed(),
            entity1.getSmsCount(),
            entity1.getTotalCharge());

    BillingRecordEntity entity2 = new BillingRecordEntity();
    entity2.setId(2L);
    entity2.setDepartment(Department.OPERATIONS.name());
    entity2.setAccountName("Seth Alberts");
    entity2.setEmployeeId("EMP5");
    entity2.setPhoneNumber("2020397483");
    entity2.setBillingPeriod("2026-01");
    entity2.setMinutesUsed(150);
    entity2.setDataGbUsed(15);
    entity2.setSmsCount(70);
    entity2.setTotalCharge(80.00);
    this.entity2 = entity2;

    domain2 =
        new BillingRecord(
            entity2.getAccountName(),
            entity2.getEmployeeId(),
            entity2.getDepartment(),
            entity2.getPhoneNumber(),
            entity2.getBillingPeriod(),
            entity2.getMinutesUsed(),
            entity2.getDataGbUsed(),
            entity2.getSmsCount(),
            entity2.getTotalCharge());
  }

  @Test
  void shouldReturnPagedBillingRecordsForValidPageAndSize() {
    // arrange
    Page<BillingRecordEntity> entityPage =
        new PageImpl<>(List.of(entity1), PageRequest.of(0, 5), 1);

    when(repository.findAll(any(Pageable.class))).thenReturn(entityPage);
    when(mapper.mapToDomain(entity1)).thenReturn(domain1);

    // act
    Page<BillingRecord> result = service.getAllRecords(0, 5);

    // assert - content
    assertThat(result.getContent()).containsExactly(domain1);

    // assert - pagination metadata
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(5);
    assertThat(result.getTotalElements()).isEqualTo(1);

    // verify repository call
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(captor.capture());

    Pageable usedPageable = captor.getValue();
    assertThat(usedPageable.getPageNumber()).isEqualTo(0);
    assertThat(usedPageable.getPageSize()).isEqualTo(5);
    assertThat(usedPageable.getSort()).isEqualTo(Sort.by("department").descending());

    // verify mapper called
    verify(mapper).mapToDomain(entity1);
  }

  @Test
  void shouldMapEntitiesToDomainModelsCorrectly() {}

  @Test
  void shouldReturnEmptyPageWhenRepositoryReturnsNoData() {
    // arrange
    Page<BillingRecordEntity> emptyPage = Page.empty();

    when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);

    // act
    Page<BillingRecord> result = service.getAllRecords(0, 5);

    // assert - content
    assertThat(result.getContent()).isEmpty();

    // assert - pagination metadata
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(0);
    assertThat(result.getTotalElements()).isEqualTo(0);

    // verify repository call
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(captor.capture());

    Pageable usedPageable = captor.getValue();
    assertThat(usedPageable.getPageNumber()).isEqualTo(0);
    assertThat(usedPageable.getPageSize()).isEqualTo(5);
    assertThat(usedPageable.getSort()).isEqualTo(Sort.by("department").descending());

    // verify mapper was NOT called
    verify(mapper, never()).mapToDomain(entity1);
  }

  @Test
  void shouldReturnRecordsForGivenBillingPeriod() {
    // arrange
    Page<BillingRecordEntity> entityPage =
        new PageImpl<>(List.of(entity1), PageRequest.of(0, 5), 1);
    when(repository.findByBillingPeriod(eq("2026-01"), any(Pageable.class))).thenReturn(entityPage);

    // act
    Page<BillingRecord> result = service.getRecordsByPeriod("2026-01", 0, 5);

    // assert
    ArgumentCaptor<String> periodCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findByBillingPeriod(periodCaptor.capture(), pageableCaptor.capture());

    String usedPeriod = periodCaptor.getValue();
    assertThat(usedPeriod).isEqualTo("2026-01");

    Pageable usedPageable = pageableCaptor.getValue();
    assertThat(usedPageable.getPageNumber()).isEqualTo(0);
    assertThat(usedPageable.getPageSize()).isEqualTo(5);

    // verify mapper called
    verify(mapper).mapToDomain(entity1);
  }

  @Test
  void shouldThrowExceptionWhenBillingPeriodFormatIsInvalid() {
    // arrange
    when(repository.findByBillingPeriod(any(String.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    // act & assert
    assertThatThrownBy(() -> service.getRecordsByPeriod("01-2026", 0, 5))
        .isInstanceOf(BillingDataNotFoundException.class);
  }

  @Test
  void shouldReturnRecordsForDepartmentCaseInsensitive() {
    // arrange
    Page<BillingRecordEntity> entityPage =
        new PageImpl<>(List.of(entity1), PageRequest.of(0, 5), 1);
    when(repository.findByDepartmentAndDatasetIdIgnoreCase(any(String.class), any(Pageable.class)))
        .thenReturn(entityPage);

    // act
    Page<BillingRecord> result = service.getRecordsByDepartment("fInanCe", 0, 5);

    // assert
    ArgumentCaptor<String> departmentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository)
        .findByDepartmentAndDatasetIdIgnoreCase(
            departmentCaptor.capture(), pageableCaptor.capture());

    String usedDepartment = departmentCaptor.getValue();
    assertThat(usedDepartment).isEqualTo("fInanCe");

    Pageable usedPageable = pageableCaptor.getValue();
    assertThat(usedPageable.getPageNumber()).isEqualTo(0);
    assertThat(usedPageable.getPageSize()).isEqualTo(5);

    // verify mapper called
    verify(mapper).mapToDomain(entity1);
  }

  @Test
  void shouldReturnTopNRecordsSortedByTotalChargeDescending() {
    // arrange
    Page<BillingRecordEntity> entityPage =
        new PageImpl<>(List.of(entity1), PageRequest.of(0, 5), 1);
    when(repository.findAll(any(Pageable.class))).thenReturn(entityPage);
    when(repository.count()).thenReturn(1L);

    // act
    Page<BillingRecord> result = service.getTopNRecords(1);

    // assert
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(pageableCaptor.capture());

    Pageable usedPageable = pageableCaptor.getValue();
    assertThat(usedPageable.getPageNumber()).isEqualTo(0);
    assertThat(usedPageable.getPageSize()).isEqualTo(1);

    // verify mapper called
    verify(mapper).mapToDomain(entity1);
  }

  @Test
  void shouldRespectLimitNInTopNQuery() {
    // arrange
    when(repository.count()).thenReturn(1L);

    // act & assert
    assertThatThrownBy(() -> service.getTopNRecords(2))
        .isInstanceOf(QueryLimitExceededException.class);
  }

  @Test
  void shouldGenerateSummaryForValidBillingPeriod() {
    // arrange
    Page<BillingRecordEntity> entityPage =
        new PageImpl<>(List.of(entity1), PageRequest.of(0, 5), 1);
    when(repository.findAll(any(Pageable.class))).thenReturn(entityPage);
    when(mapper.mapToDomain(entity1)).thenReturn(domain1);

    BillingSummary summary = service.generateSummary();

    // assert
    assertThat(summary.getTotalRecords()).isEqualTo(1);
  }

  @Test
  void shouldThrowExceptionWhenNoDataExistsForBillingPeriod() {
    // arrange
    Page<BillingRecordEntity> emptyPage = Page.empty();
    when(repository.findByBillingPeriod(eq("2026-01"), any(Pageable.class))).thenReturn(emptyPage);

    // act & assert
    assertThatThrownBy(() -> service.getRecordsByPeriod("2026-01", 0, 5))
        .isInstanceOf(BillingDataNotFoundException.class);
  }

  @Test
  void shouldGenerateSummaryAcrossMultiplePagesAndStopAtLastPage() {
    // arrange

    Page<BillingRecordEntity> firstPage = new PageImpl<>(List.of(entity1), PageRequest.of(0, 1), 2);

    Page<BillingRecordEntity> secondPage =
        new PageImpl<>(List.of(entity2), PageRequest.of(1, 1), 2);

    when(repository.findAll(any(Pageable.class))).thenReturn(firstPage).thenReturn(secondPage);

    when(mapper.mapToDomain(entity1)).thenReturn(domain1);

    when(mapper.mapToDomain(entity2)).thenReturn(domain2);

    // act
    BillingSummary summary = service.generateSummary();

    // assert
    assertThat(summary.getTotalRecords()).isEqualTo(2);
    assertThat(summary.getTotalCharges()).isEqualTo(150.00);
  }

  @Test
  void shouldThrowExceptionWhenNoBillingDataExistsForSummary() {
    // arrange
    when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    // act & assert
    assertThatThrownBy(() -> service.generateSummary())
        .isInstanceOf(BillingDataNotFoundException.class);
  }
}
