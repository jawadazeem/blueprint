/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.martin;

import org.springframework.stereotype.Service;

@Service
public class SchemaService {
  public String getSchema() {
    return """
            DATABASE SCHEMA

            TABLE billing_records (
                id BIGINT PRIMARY KEY,
                department TEXT,
                account_name TEXT,
                employee_id TEXT,
                phone_number TEXT,
                billing_period TEXT,
                minutes_used INT,
                data_gb_used DOUBLE,
                sms_count INT,
                total_charge DOUBLE
            );

            TABLE alarms (
                id UUID PRIMARY KEY,
                business_key UUID,
                alarm_scope TEXT,
                billing_period DATE,
                alarm_type TEXT,
                alarm_severity TEXT,
                explanation TEXT,
                timestamp TIMESTAMPTZ,
                employee_id TEXT,
                phone_number TEXT,
                department TEXT
            );
            """;
  }
}
