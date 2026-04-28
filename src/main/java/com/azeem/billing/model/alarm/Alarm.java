/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.model.alarm;

import com.azeem.billing.model.billing.Department;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an Alarm.
 * <p>An Alarm object either be for a department or an individual</p>
 */

public record Alarm(UUID id,
                    UUID businessKey,
                    @Enumerated(EnumType.STRING)
                    AlarmScope alarmScope,
                    String billingPeriod,
                    String alarmType,
                    @Enumerated(EnumType.STRING)
                    AlarmSeverity alarmSeverity,
                    String explanation,
                    Instant timestamp,
                    String employeeId, // nullable
                    String phoneNumber, // nullable
                    @Enumerated(EnumType.STRING)
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
                department
        );
    }
}
