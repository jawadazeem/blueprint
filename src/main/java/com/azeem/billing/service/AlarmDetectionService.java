/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.service;

import com.azeem.billing.config.AlarmConfig;
import com.azeem.billing.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static com.azeem.billing.model.AlarmSeverity.LOW;

/**
 * <p>Input: List<BillingRecord> (scoped by billingPeriod)</p>
 * <p>Output: List<Alarm> (NOT persisted)</p>
 * <p>No repositories</p>
 * <p>No side effects</p>
 */
@Service
public class AlarmDetectionService {
    private final AlarmConfig alarmConfig;

    public AlarmDetectionService(AlarmConfig alarmConfig) {
        this.alarmConfig = alarmConfig;
    }

    public List<Alarm> detectAlarms(List<BillingRecord> records, String billingPeriod) {
        List<Alarm> alarms = getDepartmentsOverLimit(records, billingPeriod);
        alarms.addAll(getIndividualChargesOverLimit(records, billingPeriod));
        alarms.addAll(getGrandTotalOverLimit(records, billingPeriod));
        return alarms;
    }


    private List<Alarm> getDepartmentsOverLimit(List<BillingRecord> records, String billingPeriod) {
        List<Alarm> alarms = new ArrayList<>();

        Map<String, Department> departments = Map.of(
                "Engineering", Department.ENGINEERING,
                "Finance", Department.FINANCE,
                "HR", Department.HR,
                "IT", Department.IT,
                "Legal", Department.LEGAL,
                "Marketing", Department.MARKETING,
                "Operations", Department.OPERATIONS,
                "Sales", Department.SALES,
                "Support", Department.SUPPORT
        );
        Map<String, Double> totals = new HashMap<>();

        for (BillingRecord r : records) {
            totals.merge(r.department(), r.totalCharge(), Double::sum);
        }

        double deptLimit = alarmConfig.getDepartment().getMonthlyLimit();

        for (String d : departments.keySet()) {
            if (totals.getOrDefault(d, 0.0) > deptLimit) {
                Alarm alarm = new Alarm(
                        null, // DB generates ID
                        UUID.randomUUID(),
                        AlarmScope.DEPARTMENT,
                        billingPeriod,
                        "Department Charge Exceeded",
                        LOW, d + " department Exceeds Charge Limit",
                        Instant.now(), null, null, departments.get(d));
                alarms.add(alarm);
            }
        }

        return alarms;
    }

    private List<Alarm> getIndividualChargesOverLimit(List<BillingRecord> records, String billingPeriod) {
        List<Alarm> alarms = new ArrayList<>();

        double low = alarmConfig.getIndividual().getLow();
        double medium = alarmConfig.getIndividual().getMedium();
        double high = alarmConfig.getIndividual().getHigh();

        for (BillingRecord r : records) {
            double charge = r.totalCharge();
            AlarmSeverity severity;
            String message;

            if (charge >= low && charge < medium) {
                severity = AlarmSeverity.LOW;
                message = "Exceeds Charge Limit: LOW";
            } else if (charge >= medium && charge < high) {
                severity = AlarmSeverity.MEDIUM;
                message = "Slightly exceeds Charge Limit: MEDIUM";
            } else if (charge >= high) {
                severity = AlarmSeverity.HIGH;
                message = "Significantly exceeds Charge Limit (TAKE ACTION)";
            } else {
                // No alarm
                continue;
            }

            Alarm alarm = new Alarm(
                    null, // DB generates ID
                    UUID.randomUUID(),
                    AlarmScope.INDIVIDUAL,
                    billingPeriod,
                    "Individual Charge Limit Exceeded",
                    severity,
                    message,
                    Instant.now(),
                    r.employeeId(),
                    r.phoneNumber(),
                    null
            );
            alarms.add(alarm);
        }

        return alarms;
    }

    private List<Alarm> getGrandTotalOverLimit(List<BillingRecord> records, String billingPeriod) {
        double grandTotal = 0;
        double accountLow = alarmConfig.getAccount().getLow();
        double accountHigh = alarmConfig.getAccount().getHigh();

        for (BillingRecord r : records) {
            grandTotal += r.totalCharge();
        }

        if (grandTotal > accountLow && grandTotal < accountHigh) {
            Alarm alarm = new Alarm(UUID.randomUUID(),
                    UUID.randomUUID(),
                    AlarmScope.ACCOUNT,
                    billingPeriod,
                    "Total Account Budget Exceeded: LOW",
                    AlarmSeverity.LOW,
                    "Your account's telecom bill has slightly exceeded its monthly budget.", Instant.now(),
                    null, null, null);
            List<Alarm> alarms = new ArrayList<>();
            alarms.add(alarm);
            return alarms;
        }
        Alarm alarm = new Alarm(UUID.randomUUID(),
                UUID.randomUUID(),
                AlarmScope.ACCOUNT,
                billingPeriod,
                "Total Account Budget Exceeded: HIGH",
                AlarmSeverity.HIGH,
                "Your account's telecom bill has significantly exceeded its monthly budget.", Instant.now(),
                null, null, null);
        return List.of(alarm);
    }
}
