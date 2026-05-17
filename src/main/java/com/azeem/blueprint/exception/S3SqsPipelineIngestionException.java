/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.exception;

public class S3SqsPipelineIngestionException extends RuntimeException {
  public S3SqsPipelineIngestionException(String message) {
    super(message);
  }

  public S3SqsPipelineIngestionException(String message, Throwable cause) {
    super(message, cause);
  }
}
