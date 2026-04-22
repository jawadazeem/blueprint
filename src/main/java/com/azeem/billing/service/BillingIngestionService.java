/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.service;

import com.azeem.billing.config.BillingReaderConfig;
import com.azeem.billing.entity.BillingRecordEntity;
import com.azeem.billing.exception.BillingDataIngestionException;
import com.azeem.billing.model.BillingRecord;
import com.azeem.billing.repository.BillingRecordRepository;
import com.azeem.billing.util.BillingFileReader;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.azeem.billing.etl.CsvBillingReader;
import com.azeem.billing.etl.BillingRecordAssembler;
import com.azeem.billing.mapper.BillingRecordMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service responsible for ingesting billing files into the database.
 *
 * <p>
 * Orchestrates the write path of the ETL pipeline:
 * <ul>
 *   <li>Reads raw billing data from a CSV file</li>
 *   <li>Assembles domain {@link BillingRecord} objects</li>
 *   <li>Persists data as {@link BillingRecordEntity}</li>
 * </ul>
 *
 * <p>
 * This service is stateless and write-focused. After ingestion,
 * the database is the single source of truth.
 * </p>
 */

@Service
@Validated
public class BillingIngestionService {
    private static final Logger log = LoggerFactory.getLogger(BillingIngestionService.class);
    private final BillingRecordMapper mapper;
    private final BillingRecordRepository repository;
    private final BillingRecordAssembler billingRecordAssembler;
    private final BillingReaderConfig billingReaderConfig;
    private final AlarmService alarmService;

    public BillingIngestionService(BillingRecordAssembler billingRecordAssembler,
                                   BillingRecordRepository repository,
                                   BillingRecordMapper mapper,
                                   BillingReaderConfig billingReaderConfig,
                                   AlarmService alarmService) {
        this.repository = repository;
        this.billingRecordAssembler = billingRecordAssembler;
        this.billingReaderConfig = billingReaderConfig;
        this.mapper = mapper;
        this.alarmService = alarmService;
    }

    public void ingestData(@NotNull String billingPeriod, @NotNull InputStream inputStream) {
        int successCount = 0;
        int failureCount = 0;

        try (CsvBillingReader reader = new CsvBillingReader(inputStream, billingReaderConfig.hasHeader())) {
            List<BillingRecordEntity> batch = new ArrayList<>();
            String[] row;

            while ((row = reader.parseNextRow()) != null) {
                try {
                    BillingRecord domain = billingRecordAssembler.assembleRecord(row);
                    batch.add(mapper.mapToEntity(domain));

                    if (batch.size() >= billingReaderConfig.getBatchSize()) {
                        repository.saveAll(batch);
                        repository.flush();
                        successCount += batch.size();
                        batch.clear();
                    }
                } catch (Exception e) {
                    failureCount++;
                    log.error(
                            "Resilience Triggered: Skipping corrupted row in period {}. Error: {}"
                            , billingPeriod, e.getMessage()
                    );
                    /*
                     * TODO: Save bad rows in a path in the S3 bucket for later analysis.
                     *  This would be a separate service that the ingestion service calls
                     *  when it encounters a bad row. The bad row would be saved with a
                     *  timestamp and the error message for later analysis.
                     */
                }
            }

            if (!batch.isEmpty()) {
                try {
                    repository.saveAll(batch);
                    successCount += batch.size();
                } catch (Exception e) {
                    failureCount += batch.size();
                    log.error("Final batch failed at database level.");
                }
            }

            alarmService.detectAndPersistAlarms(billingPeriod);
            log.info("Ingestion Complete for {}: {} Success, {} Failures", billingPeriod, successCount, failureCount);

        } catch (Exception e) {
            log.error("System-Level Failure: The S3 stream or Reader encountered a fatal error.", e);
            // This is the only place where throwing makes sense, as the whole process is dead.
        }
    }
}
