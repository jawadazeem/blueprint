/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.util;


/**
 * Interface for reading and parsing billing files.
 */
public interface BillingFileReader {
    String[] parseNextRow();
}
