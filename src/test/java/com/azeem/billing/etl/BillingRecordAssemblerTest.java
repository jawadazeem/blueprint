/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.etl;

import com.azeem.billing.model.BillingRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BillingRecordAssembler}.
 *
 * Uses JUnit 5 and focuses on deterministic, small-scope tests that
 * exercise both successful parsing and common failure modes.
 */
public class BillingRecordAssemblerTest {

    private final BillingRecordAssembler assembler = new BillingRecordAssembler();

    @Test
    @DisplayName("assembleRecord should convert a valid row into a BillingRecord")
    void assembleRecord_validRow_createsBillingRecord() {
        String[] row = {
            "Acme Corp",         // accountName
            "E12345",            // employeeId
            "Engineering",       // department
            "555-0100",          // phoneNumber
            "2026-01",           // billingPeriod
            "120",               // minutesUsed
            "1.5",               // dataGbUsed
            "10",                // smsCount
            "45.75"              // totalCharge
        };

        BillingRecord record = assembler.assembleRecord(row);

        assertAll("Verify assembled record fields",
            () -> assertEquals("Acme Corp", record.accountName()),
            () -> assertEquals("E12345", record.employeeId()),
            () -> assertEquals("Engineering", record.department()),
            () -> assertEquals("555-0100", record.phoneNumber()),
            () -> assertEquals("2026-01", record.billingPeriod()),
            () -> assertEquals(120, record.minutesUsed()),
            () -> assertEquals(1.5, record.dataGbUsed(), 1e-9),
            () -> assertEquals(10, record.smsCount()),
            () -> assertEquals(45.75, record.totalCharge(), 1e-9)
        );
    }

    @Test
    @DisplayName("assembleRecords should convert multiple rows into a List of BillingRecord")
    void assembleRecords_multipleRows_returnsList() {
        List<String[]> rows = List.of(
            new String[] { "A", "id1", "DeptA", "111", "2026-01", "10", "0.1", "1", "2.5" },
            new String[] { "B", "id2", "DeptB", "222", "2026-02", "20", "0.2", "2", "5.0" }
        );

        List<BillingRecord> records = assembler.assembleRecords(rows);

        assertEquals(2, records.size());
        assertEquals("id1", records.get(0).employeeId());
        assertEquals("DeptB", records.get(1).department());
    }

    @Nested
    @DisplayName("Failure modes")
    class FailureModes {

        @Test
        @DisplayName("assembleRecord should throw ArrayIndexOutOfBoundsException for too few columns")
        void assembleRecord_tooFewColumns_throws() {
            String[] shortRow = { "Only", "a", "few" }; // intentionally too short
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> assembler.assembleRecord(shortRow));
        }

        @Test
        @DisplayName("assembleRecord should throw NumberFormatException for invalid numeric fields")
        void assembleRecord_invalidNumber_throws() {
            String[] badNumbers = {
                "Acme", "E1", "D", "000", "2026-01",
                "not-a-number", // minutesUsed invalid
                "0.0", "0", "0.0"
            };
            assertThrows(NumberFormatException.class, () -> assembler.assembleRecord(badNumbers));
        }
    }
}
