/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.exception;

/**
 * Exception thrown when there is an error loading billing data such that the data was not found.
 */
public class BillingDataNotFoundException extends RuntimeException {
  public BillingDataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public BillingDataNotFoundException(String message) {
    super(message);
  }
}
