/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.exception;

/**
 * Exception thrown when no billing records are found for a given department.
 */

public class DepartmentNotFoundException extends RuntimeException {

    public DepartmentNotFoundException(String department) {
        super("No billing records found for department: " + department);
    }
}
