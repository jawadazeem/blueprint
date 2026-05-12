/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.alarm;

import com.azeem.blueprint.model.billing.Department;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an Alarm.
 *
 * <p>An Alarm object either be for the entire account, a department or an individual
 */
public record Alarm(
    UUID id,
    UUID businessKey,
    AlarmScope alarmScope,
    String billingPeriod,
    String alarmType,
    AlarmSeverity alarmSeverity,
    String explanation,
    Instant timestamp,
    String employeeId, // nullable
    String phoneNumber, // nullable
    Department department // nullable
    ) {

  @Override
  public int hashCode() {
    return Objects.hash(
        businessKey,
        alarmScope,
        billingPeriod,
        alarmType,
        alarmSeverity,
        explanation,
        employeeId,
        phoneNumber,
        department);
  }
}
