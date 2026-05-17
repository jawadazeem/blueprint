/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.model.billing;

import java.util.UUID;

public record IngestionResult(
    UUID datasetId, String billingPeriod, int successCount, int failureCount, String errorLog) {}
