/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CsvFileValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BillingPeriod {
  String message() default
      """
      Only the YYYY-MM billing period format is allowed. The only exception is dummy data,
      which is for demonstration purposes only.
      """;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
