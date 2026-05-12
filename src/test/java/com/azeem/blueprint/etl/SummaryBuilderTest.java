/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.etl;

import static org.junit.jupiter.api.Assertions.*;

import com.azeem.blueprint.model.billing.BillingRecord;
import com.azeem.blueprint.model.billing.BillingSummary;
import java.util.LinkedList;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Enterprise-style unit tests for {@link SummaryBuilder}.
 *
 * <p>Verifies aggregation behavior, edge cases (empty input), and grouping semantics.
 */
public class SummaryBuilderTest {

  @Test
  @DisplayName("build() produces correct summary for a small dataset")
  void build_happyPath_producesCorrectSummary() {
    LinkedList<BillingRecord> records = new LinkedList<>();
    records.add(
        new BillingRecord("Acme", "E1", "Engineering", "555-0001", "2026-01", 100, 1.5, 10, 45.50));
    records.add(new BillingRecord("Beta", "E2", "Sales", "555-0002", "2026-01", 50, 0.5, 5, 10.00));
    records.add(
        new BillingRecord("Acme", "E3", "Engineering", "555-0003", "2026-01", 10, 0.1, 1, 5.25));

    SummaryBuilder builder = new SummaryBuilder(records);
    BillingSummary summary = builder.build();

    assertNotNull(summary);
    assertEquals(3, summary.getTotalRecords(), "totalRecords should match number of input records");

    // total charges = 45.50 + 10.00 + 5.25 = 60.75
    assertEquals(
        60.75,
        summary.getTotalCharges(),
        1e-9,
        "totalCharges should sum all charges and round to 2 decimals");

    // highest charge record should be the first record (45.50)
    assertNotNull(summary.getHighestChargeRecord());
    assertEquals(45.50, summary.getHighestChargeRecord().totalCharge(), 1e-9);

    // average charge = 60.75 / 3 = 20.25
    assertEquals(20.25, summary.getAverageCharge(), 1e-9);

    Map<String, Double> byDept = summary.getChargesByState();
    assertEquals(2, byDept.size(), "There should be two departments in the map");
    assertEquals(50.75, byDept.get("Engineering"), 1e-9);
    assertEquals(10.00, byDept.get("Sales"), 1e-9);
  }

  @Test
  @DisplayName("build() on empty list returns zeroed summary")
  void build_emptyList_returnsZeroedSummary() {
    LinkedList<BillingRecord> records = new LinkedList<>();

    SummaryBuilder builder = new SummaryBuilder(records);
    BillingSummary summary = builder.build();

    assertNotNull(summary);
    assertEquals(0, summary.getTotalRecords());
    assertEquals(0.0, summary.getTotalCharges(), 1e-9);
    assertEquals(0.0, summary.getAverageCharge(), 1e-9);
    assertTrue(summary.getChargesByState().isEmpty());
    assertNull(
        summary.getHighestChargeRecord(), "highestChargeRecord should be null for empty input");
  }

  @Nested
  @DisplayName("Aggregation edge-cases")
  class AggregationEdgeCases {

    @Test
    @DisplayName("handles ties for highest charge by selecting the first encountered")
    void tiesForHighestCharge_selectsFirstEncountered() {
      LinkedList<BillingRecord> records = new LinkedList<>();
      records.add(new BillingRecord("A", "id1", "D1", "p1", "2026-01", 1, 0.0, 0, 100.00));
      records.add(new BillingRecord("B", "id2", "D2", "p2", "2026-01", 1, 0.0, 0, 100.00));

      SummaryBuilder builder = new SummaryBuilder(records);
      BillingSummary summary = builder.build();

      assertNotNull(summary.getHighestChargeRecord());
      assertEquals("id1", summary.getHighestChargeRecord().employeeId());
    }

    @Test
    @DisplayName("aggregate charges by department with single record per department")
    void chargesByDepartment_singleRecordEach() {
      LinkedList<BillingRecord> records = new LinkedList<>();
      records.add(new BillingRecord("A", "id1", "D1", "p1", "2026-01", 1, 0.0, 0, 1.00));
      records.add(new BillingRecord("B", "id2", "D2", "p2", "2026-01", 1, 0.0, 0, 2.50));

      SummaryBuilder builder = new SummaryBuilder(records);
      BillingSummary summary = builder.build();

      Map<String, Double> byDept = summary.getChargesByState();
      assertEquals(2, byDept.size());
      assertEquals(1.00, byDept.get("D1"), 1e-9);
      assertEquals(2.50, byDept.get("D2"), 1e-9);
    }
  }
}
