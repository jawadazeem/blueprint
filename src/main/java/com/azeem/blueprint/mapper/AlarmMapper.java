/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.mapper;

import com.azeem.blueprint.entity.AlarmEntity;
import com.azeem.blueprint.model.alarm.Alarm;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between Alarm domain model and AlarmEntity database entity.
 */

@Component
public class AlarmMapper {

    public AlarmEntity mapToEntity(Alarm alarm) {
        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setBusinessKey(alarm.businessKey());
        alarmEntity.setAlarmScope(alarm.alarmScope());
        alarmEntity.setAlarmSeverity(alarm.alarmSeverity());
        alarmEntity.setAlarmType(alarm.alarmType());
        alarmEntity.setBillingPeriod(alarm.billingPeriod());
        alarmEntity.setEmployeeId(alarm.employeeId());
        alarmEntity.setExplanation(alarm.explanation());
        alarmEntity.setPhoneNumber(alarm.phoneNumber());
        alarmEntity.setDepartment(alarm.department());
        alarmEntity.setTimestamp(alarm.timestamp());
        return alarmEntity;
    }

    public Alarm mapToDomain(AlarmEntity alarmEntity) {
        return new Alarm(
                alarmEntity.getId(),
                alarmEntity.getBusinessKey(),
                alarmEntity.getAlarmScope(),
                alarmEntity.getBillingPeriod(),
                alarmEntity.getAlarmType(),
                alarmEntity.getAlarmSeverity(),
                alarmEntity.getExplanation(),
                alarmEntity.getTimestamp(),
                alarmEntity.getEmployeeId(),
                alarmEntity.getPhoneNumber(),
                alarmEntity.getDepartment()
        );
    }
}
