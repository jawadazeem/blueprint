/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import com.azeem.blueprint.model.alarm.Alarm;
import com.azeem.blueprint.service.alarm.AlarmService;
import com.azeem.blueprint.validation.BillingPeriod;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/datasets/{datasetId}")
public class AlarmController {
  private static final Logger log = LoggerFactory.getLogger(AlarmController.class);
  private final AlarmService service;

  public AlarmController(AlarmService service) {
    this.service = service;
  }

  @GetMapping("/alarms/{billingPeriod}")
  public List<Alarm> getAllAlarms(
      @PathVariable UUID datasetId, @BillingPeriod @PathVariable String billingPeriod) {
    log.info("GET /datasets/{}/alarms/{} called.", datasetId, billingPeriod);
    return service.getAllAlarmsInDataset(datasetId, billingPeriod);
  }

  @GetMapping("/alarms/{billingPeriod}/department")
  public List<Alarm> getDepartmentAlarms(
      @PathVariable UUID datasetId, @BillingPeriod @PathVariable String billingPeriod) {
    log.info("GET /datasets/{}/alarms/{}/department called.", datasetId, billingPeriod);
    return service.getDepartmentAlarmsInDataset(datasetId, billingPeriod);
  }

  @GetMapping("/alarms/{billingPeriod}/individual")
  public List<Alarm> getIndividualAlarms(
      @PathVariable UUID datasetId, @BillingPeriod @PathVariable String billingPeriod) {
    log.info("GET /datasets/{}/alarms/{}/individual called.", datasetId, billingPeriod);
    return service.getIndividualAlarmsInDataset(datasetId, billingPeriod);
  }

  @GetMapping("/alarms/{billingPeriod}/account")
  public List<Alarm> getAccountAlarm(
      @PathVariable UUID datasetId, @BillingPeriod @PathVariable String billingPeriod) {
    log.info("GET /datasets/{}/alarms/{}/account called.", datasetId, billingPeriod);
    return service.getAccountAlarm(datasetId, billingPeriod);
  }
}
