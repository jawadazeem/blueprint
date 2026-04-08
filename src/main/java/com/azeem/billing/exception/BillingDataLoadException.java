package com.azeem.billing.exception;

/**
 * Exception thrown when there is a general error loading billing data.
 */

public class BillingDataLoadException extends RuntimeException {

    public BillingDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
