/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.alarm;

import static com.azeem.blueprint.model.alarm.AlarmSeverity.LOW;

import com.azeem.blueprint.model.billing.Department;
import jakarta.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

/**
 * Alarm DTO
 *
 * <p>An Alarm object that represents an alarm for either the entire account, a department or an
 * individual
 */
public record Alarm(
    UUID id,
    UUID datasetId,
    UUID businessKey,
    AlarmScope alarmScope,
    String billingPeriod,
    String alarmType,
    AlarmSeverity alarmSeverity,
    String explanation,
    Instant timestamp,
    @Nullable String employeeId,
    @Nullable String phoneNumber,
    @Nullable Department department) {

  public static Alarm individual(
      UUID datasetId,
      String billingPeriod,
      AlarmSeverity severity,
      String message,
      String employeeId,
      String phoneNumber) {
    return new Alarm(
        null, // DB generates ID
        datasetId,
        generateBusinessKey(
            datasetId,
            billingPeriod,
            AlarmScope.DEPARTMENT.toString(),
            severity.toString(),
            employeeId,
            ""),
        AlarmScope.INDIVIDUAL,
        billingPeriod,
        "Individual Charge Limit Exceeded",
        severity,
        message,
        Instant.now(),
        employeeId,
        phoneNumber,
        null);
  }

  public static Alarm department(UUID datasetId, String billingPeriod, Department department) {
    return new Alarm(
        null, // DB generates ID
        datasetId,
        generateBusinessKey(
            datasetId,
            billingPeriod,
            AlarmScope.DEPARTMENT.toString(),
            AlarmSeverity.LOW.toString(),
            "",
            department.toString()),
        AlarmScope.DEPARTMENT,
        billingPeriod,
        "Department Charge Exceeded",
        LOW,
        department + " department Exceeds Charge Limit",
        Instant.now(),
        null,
        null,
        department);
  }

  public static Alarm accountLow(UUID datasetId, String billingPeriod) {
    return new Alarm(
        null, // DB generates PK ID
        datasetId,
        generateBusinessKey(
            datasetId,
            billingPeriod,
            AlarmScope.ACCOUNT.toString(),
            AlarmSeverity.LOW.toString(),
            "",
            ""),
        AlarmScope.ACCOUNT,
        billingPeriod,
        "Total Account Budget Exceeded: LOW",
        AlarmSeverity.LOW,
        "Your account's telecom bill has slightly exceeded its monthly budget.",
        Instant.now(),
        null,
        null,
        null);
  }

  public static Alarm accountHigh(UUID datasetId, String billingPeriod) {
    return new Alarm(
        null, // DB generates ID
        datasetId,
        generateBusinessKey(
            datasetId,
            billingPeriod,
            AlarmScope.ACCOUNT.toString(),
            AlarmSeverity.HIGH.toString(),
            "",
            ""),
        AlarmScope.ACCOUNT,
        billingPeriod,
        "Total Account Budget Exceeded: HIGH",
        AlarmSeverity.HIGH,
        "Your account's telecom bill has significantly exceeded its monthly budget.",
        Instant.now(),
        null,
        null,
        null);
  }

  /** Generates a deterministic business key for deduplication */
  private static UUID generateBusinessKey(
      UUID datasetId,
      String billingPeriod,
      String alarmScope,
      String alarmSeverity,
      String employeeId,
      String department) {

    String fingerprint =
        datasetId + billingPeriod + alarmScope + alarmSeverity + employeeId + department;

    return UUID.nameUUIDFromBytes(fingerprint.getBytes(StandardCharsets.UTF_8));
  }
}
