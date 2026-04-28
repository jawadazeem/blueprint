/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.exception;

/**
 * Exception thrown when there is a general error loading billing data.
 */

public class BillingDataLoadException extends RuntimeException {

    public BillingDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
