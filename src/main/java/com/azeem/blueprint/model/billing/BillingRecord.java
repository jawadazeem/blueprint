/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.billing;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Billing Record DTO
 *
 * <p>Represents the detailed billing record for a single account.
 */
public record BillingRecord(
    UUID datasetId,
    String accountName,
    String employeeId,
    String department,
    String phoneNumber,
    String billingPeriod,
    int minutesUsed,
    double dataGbUsed,
    int smsCount,
    double totalCharge) {

  @NotNull
  @Override
  public String toString() {
    return datasetId
        + ", "
        + accountName
        + ", "
        + employeeId
        + ", "
        + department
        + ", "
        + phoneNumber
        + ", "
        + billingPeriod
        + ", "
        + minutesUsed
        + ", "
        + dataGbUsed
        + ", "
        + smsCount
        + ", "
        + totalCharge;
  }
}
