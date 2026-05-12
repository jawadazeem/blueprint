/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.repository;

import com.azeem.blueprint.entity.AlarmEntity;
import com.azeem.blueprint.model.alarm.AlarmScope;
import com.azeem.blueprint.model.alarm.AlarmSeverity;
import com.azeem.blueprint.model.billing.Department;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("AlarmRepository Integration Tests")
public class AlarmRepositoryTest {

  @Autowired private AlarmRepository alarmRepository;

  @Autowired private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    // Build and persist complete entities (one of each of the main three types)
    persistAlarm(
        UUID.randomUUID(),
        AlarmScope.DEPARTMENT,
        "2026-01",
        "Department Charge Exceeded",
        AlarmSeverity.HIGH,
        "IT department Exceeds Charge Limit",
        Instant.now(),
        null,
        null,
        Department.IT);

    persistAlarm(
        UUID.randomUUID(),
        AlarmScope.INDIVIDUAL,
        "2026-01",
        "Individual Charge Exceeded",
        AlarmSeverity.LOW,
        "Slightly Exceeds Charge Limit",
        Instant.now(),
        "EMP192",
        "2028402810",
        Department.OPERATIONS);

    persistAlarm(
        UUID.randomUUID(),
        AlarmScope.ACCOUNT,
        "2026-01",
        "Account Charge Exceeded",
        AlarmSeverity.HIGH,
        "Account Exceeds Charge Limit",
        Instant.now(),
        null,
        null,
        null);
  }

  // Helper for creating realistic alarm rows with optional nullable fields.
  private void persistAlarm(
      UUID businessKey,
      AlarmScope scope,
      String billingPeriod,
      String alarmType,
      AlarmSeverity severity,
      String explanation,
      Instant timestamp,
      String employeeId,
      String phoneNumber,
      Department dept) {
    AlarmEntity alarm = new AlarmEntity();
    alarm.setBusinessKey(businessKey);
    alarm.setAlarmScope(scope);
    alarm.setBillingPeriod(billingPeriod);
    alarm.setAlarmType(alarmType);
    alarm.setAlarmSeverity(severity);
    alarm.setExplanation(employeeId);
    alarm.setExplanation(explanation);
    alarm.setTimestamp(timestamp);
    alarm.setPhoneNumber(phoneNumber);
    alarm.setDepartment(dept);
    entityManager.persist(alarm);
  }
}
