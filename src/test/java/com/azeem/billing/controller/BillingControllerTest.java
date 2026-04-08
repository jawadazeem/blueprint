package com.azeem.billing.controller;

import com.azeem.billing.model.BillingRecord;
import com.azeem.billing.model.BillingSummary;
import com.azeem.billing.service.BillingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillingController.class)
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillingService service; // Spring injects the mock defined below

    @TestConfiguration
    static class MockConfig {

        @Bean
        BillingService billingService() {
            return Mockito.mock(BillingService.class);
        }
    }

    @Test
    void testSummaryEndpoint() throws Exception {
        BillingSummary summary = new BillingSummary();
        summary.setTotalRecords(5);
        summary.setTotalCharges(300.0);

        // Define mock behavior
        when(service.generateSummary()).thenReturn(summary);

        // Perform + assert JSON response
        mockMvc.perform(get("/summary"))
                .andExpect(status().isOk()) // HTTP status must be ok
                .andExpect(jsonPath("$.totalRecords").value(5))
                .andExpect(jsonPath("$.totalCharges").value(300.0));
    }

    @Test
    void testRecordsEndpoint() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<BillingRecord> list = List.of(
                new BillingRecord("Account1", "S1", "CA", "555-1234", "2025-Jan",
                        100, 1.5, 20, 50.0)
        );

        // Use PageImpl to wrap the list
        Page<BillingRecord> fakeRecords = new PageImpl<>(list, pageable, list.size());

        // Mock the paginated service method (page 0, size 20 default)
        when(service.getAllRecords(0, 20)).thenReturn(fakeRecords);

        // Perform + assert JSON response (Page serialized with 'content')
        mockMvc.perform(get("/records"))
                .andExpect(status().isOk()) // HTTP status must be ok
                .andExpect(jsonPath("$.content[0].accountName").value("Account1"))
                .andExpect(jsonPath("$.content[0].department").value("CA"))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testFilterByStateEndpoint() throws Exception {
        List<BillingRecord> fakeRecords = List.of(
                new BillingRecord("Account1", "S1", "CA", "555-1234", "2025-Jan",
                        100, 5.5, 20, 150.0),
                new BillingRecord("Account2", "S2", "VA", "111-1234", "2025-Jan",
                        300, 2.5, 10, 70.0),
                new BillingRecord("Account3", "S3", "MD", "222-1234", "2025-Jan",
                        150, 1.5, 30, 10.0)
        );

        // Wrap the single record for VA in a Page
        Page<BillingRecord> vaPage = new PageImpl<>(fakeRecords.subList(1,2), PageRequest.of(0,20), 1);

        when(service.getRecordsByDepartment("VA", 0, 20)).thenReturn(vaPage);

        // call GET /records/department/VA
        mockMvc.perform(get ("/records/department/VA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountName").value("Account2"))
                .andExpect(jsonPath("$.content[0].department").value("VA"));
    }

    @Test
    void testDepartmentsAndPeriods() throws Exception {
        List<String> depts = List.of("CA", "MD", "VA");
        List<String> periods = List.of("2025-Jan", "2025-Feb", "2025-Mar");

        when(service.getDistinctDepartments()).thenReturn(depts);
        when(service.getDistinctBillingPeriods()).thenReturn(periods);

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("CA"));

        mockMvc.perform(get("/periods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2]").value("2025-Mar"));
    }
}
