/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.exception;

public class QueryLimitExceededException extends ApiException {
  public QueryLimitExceededException(String message) {
    super(message);
  }
}
