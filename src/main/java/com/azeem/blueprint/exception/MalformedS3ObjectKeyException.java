/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.exception;

public class MalformedS3ObjectKeyException extends RuntimeException {
  public MalformedS3ObjectKeyException(String message) {
    super(message);
  }

  public MalformedS3ObjectKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
