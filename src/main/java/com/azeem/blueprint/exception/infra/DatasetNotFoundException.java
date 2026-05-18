/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.exception.infra;

public class DatasetNotFoundException extends RuntimeException {
  public DatasetNotFoundException(String message) {
    super(message);
  }
}
