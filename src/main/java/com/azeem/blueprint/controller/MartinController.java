/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.controller;

import com.azeem.blueprint.model.martin.MartinRequest;
import com.azeem.blueprint.model.martin.MartinResponse;
import com.azeem.blueprint.service.martin.MartinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MartinController {
  private static final Logger log = LoggerFactory.getLogger(MartinController.class);
  private final MartinService martinService;

  public MartinController(MartinService martinService) {
    this.martinService = martinService;
  }

  @PostMapping("/martin")
  public ResponseEntity<MartinResponse> chat(@RequestBody MartinRequest request) {
    MartinResponse response = martinService.ask(request.getPrompt(), request.getPeriod());
    return ResponseEntity.ok(response);
  }
}
