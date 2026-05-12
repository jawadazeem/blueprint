/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.alarm;

import com.azeem.blueprint.entity.AlarmEntity;
import com.azeem.blueprint.mapper.AlarmMapper;
import com.azeem.blueprint.mapper.BillingRecordMapper;
import com.azeem.blueprint.model.alarm.Alarm;
import com.azeem.blueprint.model.alarm.AlarmScope;
import com.azeem.blueprint.model.billing.BillingRecord;
import com.azeem.blueprint.repository.AlarmRepository;
import com.azeem.blueprint.repository.BillingRecordRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlarmService {

  AlarmRepository alarmRepository;
  BillingRecordRepository billingRecordRepository;
  AlarmMapper alarmMapper;
  BillingRecordMapper billingMapper;
  AlarmDetectionService alarmDetectionService;

  public AlarmService(
      AlarmRepository alarmRepository,
      BillingRecordRepository billingRecordRepository,
      AlarmDetectionService alarmDetectionService,
      AlarmMapper alarmMapper,
      BillingRecordMapper billingRecordMapper) {
    this.alarmRepository = alarmRepository;
    this.billingRecordRepository = billingRecordRepository;
    this.alarmDetectionService = alarmDetectionService;
    this.billingMapper = billingRecordMapper;
    this.alarmMapper = alarmMapper;
  }

  @Transactional
  public void detectAndPersistAlarms(String billingPeriod) {
    List<BillingRecord> records =
        billingRecordRepository.findByBillingPeriod(billingPeriod, Pageable.unpaged()).stream()
            .map(billingMapper::mapToDomain)
            .toList();

    List<Alarm> detectedAlarms = alarmDetectionService.detectAlarms(records, billingPeriod);

    if (detectedAlarms.isEmpty()) return;

    List<UUID> existingAlarmKeys = alarmRepository.findBusinessKeysByBillingPeriod(billingPeriod);

    Set<UUID> existingKeys = new HashSet<>(existingAlarmKeys); // using set for o(m) lookups

    List<Alarm> newAlarms =
        detectedAlarms.stream().filter(a -> !existingKeys.contains(a.businessKey())).toList();

    if (newAlarms.isEmpty()) return;

    List<AlarmEntity> entities = newAlarms.stream().map(alarmMapper::mapToEntity).toList();

    alarmRepository.saveAll(entities);
  }

  public List<Alarm> getAllAlarms(String billingPeriod) {
    return alarmRepository.findByBillingPeriod(billingPeriod).stream()
        .map(alarmMapper::mapToDomain)
        .toList();
  }

  public List<Alarm> getDepartmentAlarms(String billingPeriod) {
    return alarmRepository
        .findByBillingPeriodAndAlarmScope(billingPeriod, AlarmScope.DEPARTMENT)
        .stream()
        .map(alarmMapper::mapToDomain)
        .toList();
  }

  public List<Alarm> getIndividualAlarms(String billingPeriod) {
    return alarmRepository
        .findByBillingPeriodAndAlarmScope(billingPeriod, AlarmScope.INDIVIDUAL)
        .stream()
        .map(alarmMapper::mapToDomain)
        .toList();
  }

  public List<Alarm> getAccountAlarm(String billingPeriod) {
    return alarmRepository
        .findByBillingPeriodAndAlarmScope(billingPeriod, AlarmScope.ACCOUNT)
        .stream()
        .map(alarmMapper::mapToDomain)
        .toList();
  }
}
