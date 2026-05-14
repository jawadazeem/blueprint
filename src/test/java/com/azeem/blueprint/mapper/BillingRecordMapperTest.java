/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.azeem.blueprint.entity.BillingRecordEntity;
import com.azeem.blueprint.model.billing.BillingRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BillingRecordMapperTest {

  private BillingRecordMapper billingRecordMapper;

  @BeforeEach
  void setUp() {
    billingRecordMapper = new BillingRecordMapper();
  }

  @Test
  @DisplayName("Should map BillingRecord domain object to BillingRecordEntity")
  void shouldMapToEntity() {

    BillingRecord record =
            new BillingRecord(
                    "Acme Corporation",
                    "EMP-1001",
                    "FINANCE",
                    "+15551234567",
                    "2026-01",
                    1240,
                    18.75,
                    320,
                    249.99);

    BillingRecordEntity result = billingRecordMapper.mapToEntity(record);

    assertThat(result).isNotNull();

    assertThat(result.getAccountName()).isEqualTo("Acme Corporation");
    assertThat(result.getEmployeeId()).isEqualTo("EMP-1001");
    assertThat(result.getDepartment()).isEqualTo("FINANCE");
    assertThat(result.getPhoneNumber()).isEqualTo("+15551234567");
    assertThat(result.getBillingPeriod()).isEqualTo("2026-01");
    assertThat(result.getMinutesUsed()).isEqualTo(1240);
    assertThat(result.getDataGbUsed()).isEqualTo(18.75);
    assertThat(result.getSmsCount()).isEqualTo(320);
    assertThat(result.getTotalCharge()).isEqualTo(249.99);
  }

  @Test
  @DisplayName("Should map BillingRecordEntity to BillingRecord domain object")
  void shouldMapToDomain() {

    BillingRecordEntity entity = new BillingRecordEntity();

    entity.setId(1L);
    entity.setAccountName("Acme Corporation");
    entity.setEmployeeId("EMP-1001");
    entity.setDepartment("FINANCE");
    entity.setPhoneNumber("+15551234567");
    entity.setBillingPeriod("2026-01");
    entity.setMinutesUsed(1240);
    entity.setDataGbUsed(18.75);
    entity.setSmsCount(320);
    entity.setTotalCharge(249.99);

    BillingRecord result = billingRecordMapper.mapToDomain(entity);

    assertThat(result).isNotNull();

    assertThat(result.accountName()).isEqualTo("Acme Corporation");
    assertThat(result.employeeId()).isEqualTo("EMP-1001");
    assertThat(result.department()).isEqualTo("FINANCE");
    assertThat(result.phoneNumber()).isEqualTo("+15551234567");
    assertThat(result.billingPeriod()).isEqualTo("2026-01");
    assertThat(result.minutesUsed()).isEqualTo(1240);
    assertThat(result.dataGbUsed()).isEqualTo(18.75);
    assertThat(result.smsCount()).isEqualTo(320);
    assertThat(result.totalCharge()).isEqualTo(249.99);
  }

  @Test
  @DisplayName("Should preserve null optional fields during mapping")
  void shouldHandleNullFields() {

    BillingRecord record =
            new BillingRecord(
                    null,
                    null,
                    null,
                    null,
                    "2026-01",
                    0,
                    0.0,
                    0,
                    0.0);

    BillingRecordEntity entity = billingRecordMapper.mapToEntity(record);

    assertThat(entity.getAccountName()).isNull();
    assertThat(entity.getEmployeeId()).isNull();
    assertThat(entity.getDepartment()).isNull();
    assertThat(entity.getPhoneNumber()).isNull();

    BillingRecord mappedBack = billingRecordMapper.mapToDomain(entity);

    assertThat(mappedBack.accountName()).isNull();
    assertThat(mappedBack.employeeId()).isNull();
    assertThat(mappedBack.department()).isNull();
    assertThat(mappedBack.phoneNumber()).isNull();
  }

  @Test
  @DisplayName("Should preserve numeric values accurately during mapping")
  void shouldPreserveNumericValues() {

    BillingRecord record =
            new BillingRecord(
                    "Enterprise Telecom",
                    "EMP-9001",
                    "OPERATIONS",
                    "+15559876543",
                    "2026-02",
                    99999,
                    9999.99,
                    50000,
                    123456.78);

    BillingRecordEntity entity = billingRecordMapper.mapToEntity(record);

    assertThat(entity.getMinutesUsed()).isEqualTo(99999);
    assertThat(entity.getDataGbUsed()).isEqualTo(9999.99);
    assertThat(entity.getSmsCount()).isEqualTo(50000);
    assertThat(entity.getTotalCharge()).isEqualTo(123456.78);

    BillingRecord mappedBack = billingRecordMapper.mapToDomain(entity);

    assertThat(mappedBack.minutesUsed()).isEqualTo(99999);
    assertThat(mappedBack.dataGbUsed()).isEqualTo(9999.99);
    assertThat(mappedBack.smsCount()).isEqualTo(50000);
    assertThat(mappedBack.totalCharge()).isEqualTo(123456.78);
  }
}