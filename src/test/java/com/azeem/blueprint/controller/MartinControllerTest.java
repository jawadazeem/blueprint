/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import com.azeem.blueprint.model.martin.MartinRequest;
import com.azeem.blueprint.model.martin.MartinResponse;
import com.azeem.blueprint.service.martin.MartinService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MartinController.class)
public class MartinControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockitoBean
  private MartinService martinService;

  @Test
  void shouldReturnChatResponse() throws Exception {
    // arrange
    when(martinService.ask("testing", "2026-01"))
            .thenReturn(new MartinResponse("test response", "test SQL", "test reasoning"));
    MartinRequest request = new MartinRequest("testing", "2026-01");
    String jsonString = mapper.writeValueAsString(request);

    // act
    mockMvc.perform(post("/martin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isOk());

    // assert
    verify(martinService).ask(eq(request.getPrompt()), eq(request.getPeriod()));
  }
}
