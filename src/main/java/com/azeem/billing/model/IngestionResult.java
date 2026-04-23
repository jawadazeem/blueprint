/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.model;

public record IngestionResult(
        String billingPeriod,
        int successCount,
        int failureCount,
        String errorLog
) {}
