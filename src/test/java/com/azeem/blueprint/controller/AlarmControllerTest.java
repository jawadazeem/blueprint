/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.azeem.blueprint.service.alarm.AlarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean AlarmService alarmService;

  @Test
  void shouldReturnAlarmsByBillingPeriod() throws Exception {
    // act
    mockMvc.perform(get("/alarms/2026-01")).andExpect(status().isOk());

    // assert
    verify(alarmService).getAllAlarms("2026-01");
  }

  @Test
  void shouldReturnAllDepartmentAlarmsByBillingPeriod() throws Exception {
    // act
    mockMvc.perform(get("/alarms/department/2026-01")).andExpect(status().isOk());

    // assert
    verify(alarmService).getDepartmentAlarms("2026-01");
  }

  @Test
  void shouldReturnAllIndividualAlarmsByBillingPeriod() throws Exception {
    // act
    mockMvc.perform(get("/alarms/individual/2026-01")).andExpect(status().isOk());

    // assert
    verify(alarmService).getIndividualAlarms("2026-01");
  }

  @Test
  void shouldReturnAllAccountAlarmsByBillingPeriod() throws Exception {
    // act
    mockMvc.perform(get("/alarms/account/2026-01")).andExpect(status().isOk());

    // assert
    verify(alarmService).getAccountAlarm("2026-01");
  }
}
