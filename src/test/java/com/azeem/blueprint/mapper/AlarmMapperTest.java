/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.azeem.blueprint.entity.AlarmEntity;
import com.azeem.blueprint.model.alarm.Alarm;
import com.azeem.blueprint.model.alarm.AlarmScope;
import com.azeem.blueprint.model.alarm.AlarmSeverity;
import com.azeem.blueprint.model.billing.Department;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlarmMapperTest {

  private AlarmMapper alarmMapper;

  private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  private static final UUID BUSINESS_KEY = UUID.fromString("22222222-2222-2222-2222-222222222222");

  private static final Instant TIMESTAMP = Instant.parse("2026-01-15T10:15:30Z");

  @BeforeEach
  void setUp() {
    alarmMapper = new AlarmMapper();
  }

  @Test
  @DisplayName("Should map Alarm domain object to AlarmEntity")
  void shouldMapToEntity() {

    Alarm alarm =
        new Alarm(
            ID,
            BUSINESS_KEY,
            AlarmScope.ACCOUNT,
            "2026-01",
            "OVERAGE",
            AlarmSeverity.HIGH,
            "Usage exceeded threshold",
            TIMESTAMP,
            "EMP-1001",
            "+15551234567",
            Department.FINANCE);

    AlarmEntity result = alarmMapper.mapToEntity(alarm);

    assertThat(result).isNotNull();

    assertThat(result.getBusinessKey()).isEqualTo(BUSINESS_KEY);
    assertThat(result.getAlarmScope()).isEqualTo(AlarmScope.ACCOUNT);
    assertThat(result.getBillingPeriod()).isEqualTo("2026-01");
    assertThat(result.getAlarmType()).isEqualTo("OVERAGE");
    assertThat(result.getAlarmSeverity()).isEqualTo(AlarmSeverity.HIGH);
    assertThat(result.getExplanation()).isEqualTo("Usage exceeded threshold");
    assertThat(result.getTimestamp()).isEqualTo(TIMESTAMP);
    assertThat(result.getEmployeeId()).isEqualTo("EMP-1001");
    assertThat(result.getPhoneNumber()).isEqualTo("+15551234567");
    assertThat(result.getDepartment()).isEqualTo(Department.FINANCE);
  }

  @Test
  @DisplayName("Should map AlarmEntity to Alarm domain object")
  void shouldMapToDomain() {

    AlarmEntity entity = new AlarmEntity();

    entity.setId(ID);
    entity.setBusinessKey(BUSINESS_KEY);
    entity.setAlarmScope(AlarmScope.ACCOUNT);
    entity.setBillingPeriod("2026-01");
    entity.setAlarmType("OVERAGE");
    entity.setAlarmSeverity(AlarmSeverity.HIGH);
    entity.setExplanation("Usage exceeded threshold");
    entity.setTimestamp(TIMESTAMP);
    entity.setEmployeeId("EMP-1001");
    entity.setPhoneNumber("+15551234567");
    entity.setDepartment(Department.FINANCE);

    Alarm result = alarmMapper.mapToDomain(entity);

    assertThat(result).isNotNull();

    assertThat(result.id()).isEqualTo(ID);
    assertThat(result.businessKey()).isEqualTo(BUSINESS_KEY);
    assertThat(result.alarmScope()).isEqualTo(AlarmScope.ACCOUNT);
    assertThat(result.billingPeriod()).isEqualTo("2026-01");
    assertThat(result.alarmType()).isEqualTo("OVERAGE");
    assertThat(result.alarmSeverity()).isEqualTo(AlarmSeverity.HIGH);
    assertThat(result.explanation()).isEqualTo("Usage exceeded threshold");
    assertThat(result.timestamp()).isEqualTo(TIMESTAMP);
    assertThat(result.employeeId()).isEqualTo("EMP-1001");
    assertThat(result.phoneNumber()).isEqualTo("+15551234567");
    assertThat(result.department()).isEqualTo(Department.FINANCE);
  }

  @Test
  @DisplayName("Should preserve null optional fields during mapping")
  void shouldHandleNullOptionalFields() {

    Alarm alarm =
        new Alarm(
            ID,
            BUSINESS_KEY,
            AlarmScope.ACCOUNT,
            "2026-01",
            "OVERAGE",
            AlarmSeverity.MEDIUM,
            "Department not specified",
            TIMESTAMP,
            null,
            null,
            null);

    AlarmEntity entity = alarmMapper.mapToEntity(alarm);

    assertThat(entity.getEmployeeId()).isNull();
    assertThat(entity.getPhoneNumber()).isNull();
    assertThat(entity.getDepartment()).isNull();

    Alarm mappedBack = alarmMapper.mapToDomain(entity);

    assertThat(mappedBack.employeeId()).isNull();
    assertThat(mappedBack.phoneNumber()).isNull();
    assertThat(mappedBack.department()).isNull();
  }
}
