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
public class BillingServiceTest {
  @Mock private BillingRecordRepository repository;
  @Mock private BillingRecordMapper mapper;

  @InjectMocks private BillingService service;

  private BillingRecordEntity entity;
  private BillingRecord domain;

  @BeforeEach
  void setUp() {
    // set max top n limit for data retrieval
    ReflectionTestUtils.setField(service, "maxTopNLimit", 100);

    // Build complete entities for tests
    BillingRecordEntity entity = new BillingRecordEntity();
    entity.setId(1L);
    entity.setDepartment(Department.IT.name());
    entity.setAccountName("Mark Wojick");
    entity.setEmployeeId("EMP4");
    entity.setPhoneNumber("7034305396");
    entity.setBillingPeriod("2026-01");
    entity.setMinutesUsed(100);
    entity.setDataGbUsed(20);
    entity.setSmsCount(100);
    entity.setTotalCharge(70.00);
    this.entity = entity;

    domain =
        new BillingRecord(
            entity.getAccountName(),
            entity.getEmployeeId(),
            entity.getDepartment(),
            entity.getPhoneNumber(),
            entity.getBillingPeriod(),
            entity.getMinutesUsed(),
            entity.getDataGbUsed(),
            entity.getSmsCount(),
            entity.getTotalCharge());
  }

  @Test
  void shouldReturnPagedBillingRecordsForValidPageAndSize() {
    // arrange
    Page<BillingRecordEntity> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1);

    when(repository.findAll(any(Pageable.class))).thenReturn(entityPage);
    when(mapper.mapToDomain(entity)).thenReturn(domain);

    // act
    Page<BillingRecord> result = service.getAllRecords(0, 5);

    // assert - content
    assertThat(result.getContent()).containsExactly(domain);

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
    verify(mapper).mapToDomain(entity);
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
    verify(mapper, never()).mapToDomain(entity);
  }

  @Test
  void shouldReturnRecordsForGivenBillingPeriod() {
    // arrange
    Page<BillingRecordEntity> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1);
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
    verify(mapper).mapToDomain(entity);
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
    Page<BillingRecordEntity> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1);
    when(repository.findByDepartmentIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(entityPage);

    // act
    Page<BillingRecord> result = service.getRecordsByDepartment("fInanCe", 0, 5);

    // assert
    ArgumentCaptor<String> departmentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findByDepartmentIgnoreCase(departmentCaptor.capture(), pageableCaptor.capture());

    String usedDepartment = departmentCaptor.getValue();
    assertThat(usedDepartment).isEqualTo("fInanCe");

    Pageable usedPageable = pageableCaptor.getValue();
    assertThat(usedPageable.getPageNumber()).isEqualTo(0);
    assertThat(usedPageable.getPageSize()).isEqualTo(5);

    // verify mapper called
    verify(mapper).mapToDomain(entity);
  }

  @Test
  void shouldReturnTopNRecordsSortedByTotalChargeDescending() {
    // arrange
    Page<BillingRecordEntity> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1);
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
    verify(mapper).mapToDomain(entity);
  }

  @Test
  void shouldRespectLimitNInTopNQuery() {
    // arrange
    Page<BillingRecordEntity> entityPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1);
    when(repository.count()).thenReturn(1L);

    // act & assert
    assertThatThrownBy(() -> service.getTopNRecords(2)).isInstanceOf(QueryLimitExceededException.class);
  }

  @Test
  void shouldGenerateSummaryForValidBillingPeriod() {

  }

  @Test
  void shouldThrowExceptionWhenNoDataExistsForBillingPeriod() {

  }

  @Test
  void shouldGenerateSummaryAcrossMultiplePagesAndStopAtLastPage() {

  }

  @Test
  void shouldThrowExceptionWhenNoBillingDataExistsForSummary() {}

  @Test
  void shouldCallRepositoryWithCorrectPageableAndSortForAllRecords() {}
}
