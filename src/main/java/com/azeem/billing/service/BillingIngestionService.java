package com.azeem.billing.service;

import com.azeem.billing.config.BillingReaderConfig;
import com.azeem.billing.entity.BillingRecordEntity;
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

import java.io.InputStream;
import java.util.ArrayList;
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

    public void ingestData(@NotNull String billingPeriod,
                           @NotNull InputStream inputStream
    ) {
        BillingFileReader billingFileReader = new CsvBillingReader(
                inputStream,
                billingReaderConfig.hasHeader()
        );

        List<BillingRecord> domainRecords = billingRecordAssembler
                .assembleRecord(billingFileReader.parse());
        List<BillingRecordEntity> entityList = new ArrayList<>();

        for (BillingRecord domainRecord : domainRecords) {
            entityList.add(mapper.mapToEntity(domainRecord));
        }

        saveEntities(entityList, billingPeriod);
    }

    @Transactional
    public void saveEntities(List<BillingRecordEntity> entityList, String billingPeriod) {
        repository.saveAll(entityList);
        log.info("Billing data ingested successfully with {} records.", entityList.size());

        alarmService.detectAndPersistAlarms(billingPeriod);
        log.info("Alarm data persisted successfully into DB");
    }
}
