/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.mapper;
import com.azeem.blueprint.entity.BillingRecordEntity;
import com.azeem.blueprint.model.billing.BillingRecord;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between BillingRecord domain model and BillingRecordEntity database entity.
 */

@Component
public class BillingRecordMapper {

    public BillingRecordMapper() {}

    public BillingRecordEntity mapToEntity(BillingRecord record) {
        BillingRecordEntity entity = new BillingRecordEntity();
        entity.setAccountName(record.accountName());
        entity.setEmployeeId(record.employeeId());
        entity.setDepartment(record.department());
        entity.setPhoneNumber(record.phoneNumber());
        entity.setBillingPeriod(record.billingPeriod());
        entity.setMinutesUsed(record.minutesUsed());
        entity.setDataGbUsed(record.dataGbUsed());
        entity.setSmsCount(record.smsCount());
        entity.setTotalCharge(record.totalCharge());
        return entity;
    }

    public BillingRecord mapToDomain(BillingRecordEntity entity) {
        return new BillingRecord(
                entity.getAccountName(),
                entity.getEmployeeId(),
                entity.getDepartment(),
                entity.getPhoneNumber(),
                entity.getBillingPeriod(),
                entity.getMinutesUsed(),
                entity.getDataGbUsed(),
                entity.getSmsCount(),
                entity.getTotalCharge()
        );
    }
}
