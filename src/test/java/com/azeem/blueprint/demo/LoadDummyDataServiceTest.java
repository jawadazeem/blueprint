/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.demo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.azeem.blueprint.repository.BillingRecordRepository;
import com.azeem.blueprint.service.billing.BillingIngestionService;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoadDummyDataServiceTest {

  @Mock private BillingIngestionService billingIngestionService;
  @Mock private BillingRecordRepository billingRecordRepository;

  @InjectMocks private LoadDummyDataService service;

  @Test
  void shouldLoadDummyDataWhenNotAlreadyLoaded() throws Exception {
    // arrange
    when(billingRecordRepository.existsByBillingPeriod("dummy-data")).thenReturn(false);

    // we cannot easily mock ClassPathResource cleanly,
    // so we spy ingestion instead of validating file content

    // act
    service.loadDummyData();

    // then
    verify(billingRecordRepository, times(1)).existsByBillingPeriod("dummy-data");
    verify(billingIngestionService, atLeastOnce()).ingestData(any(InputStream.class));
  }

  @Test
  void shouldNotLoadDummyDataWhenAlreadyLoaded() {
    // arrange
    when(billingRecordRepository.existsByBillingPeriod("dummy-data")).thenReturn(true);

    // act
    service.loadDummyData();

    // then
    verify(billingRecordRepository, times(1)).existsByBillingPeriod("dummy-data");
    verifyNoInteractions(billingIngestionService);
  }

  @Test
  void shouldHandleIOExceptionGracefully() {
    // arrange
    when(billingRecordRepository.existsByBillingPeriod("dummy-data")).thenReturn(false);

    // force ingestion to throw exception
    doThrow(new RuntimeException("IO failure"))
        .when(billingIngestionService)
        .ingestData(any(InputStream.class));

    // act & assert
    assertThatThrownBy(() -> service.loadDummyData());
    verify(billingIngestionService, times(1)).ingestData(any(InputStream.class));
    verify(billingRecordRepository, times(1)).existsByBillingPeriod("dummy-data");
  }

  @Test
  void shouldAlwaysCheckIfDataAlreadyLoaded() {
    // given
    when(billingRecordRepository.existsByBillingPeriod("dummy-data")).thenReturn(false);

    // when
    service.loadDummyData();

    // then
    verify(billingRecordRepository).existsByBillingPeriod("dummy-data");
  }
}
