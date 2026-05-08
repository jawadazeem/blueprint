/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.repository;

import com.azeem.blueprint.entity.AlarmEntity;
import com.azeem.blueprint.model.alarm.AlarmScope;
import com.azeem.blueprint.model.alarm.AlarmSeverity;
import com.azeem.blueprint.model.billing.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

@DataJpaTest
@DisplayName("AlarmRepository Integration Tests")
public class AlarmRepositoryTest {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {

    }

    private void persistAlarm(String dept) {
        AlarmEntity alarm = new AlarmEntity();
        alarm.setAlarmScope(AlarmScope.DEPARTMENT);
        alarm.setBillingPeriod("2026-01");
        alarm.setAlarmType("Department Charge Exceeded");
        alarm.setAlarmSeverity(AlarmSeverity.HIGH);
        alarm.setExplanation("IT department Exceeds Charge Limit");
        alarm.setTimestamp(Instant.now());
        alarm.setPhoneNumber("7034283104");
        alarm.setDepartment(Department.IT);
        entityManager.persist(alarm);
    }
}
