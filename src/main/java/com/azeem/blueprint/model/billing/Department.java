/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.billing;

public enum Department {
  ENGINEERING,
  FINANCE,
  HR,
  IT,
  LEGAL,
  MARKETING,
  OPERATIONS,
  SALES,
  SUPPORT,
  PROCUREMENT;

  public static Department fromString(String value) {
    return Department.valueOf(value.toUpperCase());
  }
}
