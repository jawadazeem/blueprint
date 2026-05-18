/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.dataset.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.azeem.blueprint.repository.BillingRecordRepository;
import com.azeem.blueprint.service.billing.BillingIngestionService;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DemoDatasetLoaderTest {

  private static final UUID DEMO_DATASET_ID = new UUID(0L, 0L);

  @Mock private BillingIngestionService billingIngestionService;
  @Mock private BillingRecordRepository billingRecordRepository;

  @InjectMocks private DemoDatasetLoader loader;

  @Test
  void shouldLoadDemoDataWhenNotAlreadyLoaded() throws Exception {
    when(billingRecordRepository.existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data"))
        .thenReturn(false);

    loader.loadDemoData();

    verify(billingRecordRepository).existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data");
    verify(billingIngestionService, atLeastOnce())
        .ingestData(eq(DEMO_DATASET_ID), any(InputStream.class));
  }

  @Test
  void shouldNotLoadDemoDataWhenAlreadyLoaded() {
    when(billingRecordRepository.existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data"))
        .thenReturn(true);

    loader.loadDemoData();

    verify(billingRecordRepository).existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data");
    verifyNoInteractions(billingIngestionService);
  }

  @Test
  void shouldHandleRuntimeExceptionFromIngestionGracefully() {
    when(billingRecordRepository.existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data"))
        .thenReturn(false);

    doThrow(new RuntimeException("ingestion failure"))
        .when(billingIngestionService)
        .ingestData(any(UUID.class), any(InputStream.class));

    // RuntimeException propagates — DemoDatasetLoader only catches IOException
    org.assertj.core.api.Assertions.assertThatThrownBy(() -> loader.loadDemoData())
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void shouldAlwaysCheckIfDataAlreadyLoaded() {
    when(billingRecordRepository.existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data"))
        .thenReturn(false);

    loader.loadDemoData();

    verify(billingRecordRepository).existsByDatasetIdAndBillingPeriod(DEMO_DATASET_ID, "demo-data");
  }
}
