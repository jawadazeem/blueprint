package com.azeem.billing.exception;

public class BillingDataIngestionException extends RuntimeException {
    public BillingDataIngestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
