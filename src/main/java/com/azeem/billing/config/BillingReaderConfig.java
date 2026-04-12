package com.azeem.billing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "billing-reader")
@lombok.Data
public class BillingReaderConfig {
    private boolean hasHeader;
    private int batchSize;

    public boolean hasHeader() {
        return hasHeader;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }
}
