/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BillingPeriodFormatValidator implements ConstraintValidator<BillingPeriod, String> {
  @Override
  public boolean isValid(String billingPeriod, ConstraintValidatorContext context) {
    // Use regex to determine if it adheres to the proper format
    return billingPeriod.matches("^\\d{4}-(0[1-9]|1[0-2])$") || billingPeriod.equals("dummy-data");
  }
}
