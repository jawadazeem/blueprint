/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.demo;

import com.azeem.billing.service.BillingIngestionService;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Class used for loading dummy data for demonstration purposes. Used by those who may not
 * have a properly formatted CSV file to run analytics on
 */
@Service
public class LoadDummyDataService {
    private final BillingIngestionService billingIngestionService;

    public LoadDummyDataService(BillingIngestionService billingIngestionService) {
        this.billingIngestionService = billingIngestionService;
    }

    public void loadDummyData() {
        File dummyData = new File("path/to/your/file.csv");
        billingIngestionService.ingestData();
    }
}
