/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.service.martin;

import org.springframework.stereotype.Service;

@Service
public class SchemaService {
    public String getSchema() {
        return """
                Tables:
                    1) billing_records:
                        Columns: id (BIGINT), department, account_name, employee_id, phone_number, 
                        billing_period, minutes_used, data_gb_used, sms_count, 
                        total_charge (DOUBLE).
                        
                    2) alarms:
                        Columns: id (UUID), business_key (UUID), alarm_scope (INT/STRING), 
                        billing_period, alarm_type, alarm_severity (INT/STRING), 
                        explanation (TEXT), timestamp (TIMESTAMPTZ), employee_id, phone_number, 
                        department.
                """;
    }
}
