package com.azeem.billing.exception;

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
