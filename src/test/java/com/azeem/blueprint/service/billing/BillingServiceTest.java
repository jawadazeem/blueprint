/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.billing;

import com.azeem.blueprint.repository.BillingRecordRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {
  @Mock private BillingRecordRepository billingRecordRepository;

  @InjectMocks private BillingService billingService;
}
