package com.azeem.billing.etl;

import com.azeem.billing.util.BillingFileReader;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads billing data from a comma-separated values (CSV) file and returns
 * raw rows as string arrays.
 *
 * <p>This class represents the <b>Extract</b> phase of the ingestion pipeline.
 * It is responsible only for file I/O and CSV mechanics.</p>
 *
 * <p>The reader performs no domain mapping, validation, or type conversion.
 * Each row is returned exactly as read from the file.</p>
 *
 * <p>This component is immutable and intended for single-use ingestion jobs.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Open and read CSV files</li>
 *   <li>Handle header row skipping if configured</li>
 *   <li>Return raw tabular data</li>
 * </ul>
 *
 * <p>Failures during reading result in a runtime exception and halt ingestion.</p>
 */

public class CsvBillingReader implements BillingFileReader, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(CsvBillingReader.class);

    private final InputStream inputStream;
    private final CSVReader csvReader;
    private final boolean hasHeader;
    private boolean headerSkipped = false;

    public CsvBillingReader(InputStream inputStream,
                            boolean hasHeader) {
        this.csvReader = new CSVReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
        this.inputStream = inputStream;
        this.hasHeader = hasHeader;
    }

    @Override
    public String[] parseNextRow() {
        try {
            if (hasHeader && !headerSkipped) {
                // Skip header row
                csvReader.readNext();
                headerSkipped = true;
            }

            return csvReader.readNext();

        } catch (IOException | CsvValidationException e) {
            log.error("Error loading CSV file", e);
            throw new IllegalStateException("Failed to load CSV file", e);
        }
    }

    @Override
    public void close() throws Exception {
        log.info("Closing CSV reader");
        try {
            csvReader.close();
        } catch (IOException e) {
            log.warn("Failed to close reader cleanly", e);
        }
    }
}
