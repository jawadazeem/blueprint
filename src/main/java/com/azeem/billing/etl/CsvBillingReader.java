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
import java.util.ArrayList;
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

public class CsvBillingReader implements BillingFileReader {
    private static final Logger log = LoggerFactory.getLogger(CsvBillingReader.class);

    private final InputStream inputStream;
    private final boolean hasHeader;

    public CsvBillingReader(InputStream inputStream,
                            boolean hasHeader) {
        this.inputStream = inputStream;
        this.hasHeader = hasHeader;
    }

    @Override
    public List<String[]> parse() {

        log.info("Loading CSV");

        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(
                        inputStream,
                        Charset.defaultCharset()
                )
        )) {
            if (hasHeader) {
                // Skip header row
                csvReader.readNext();
            }

            String[] tokens;
            List<String[]> entries = new ArrayList<>();

            while ((tokens = csvReader.readNext()) != null) {
                entries.add(tokens);
            }

            log.info("Successfully loaded from CSV. {} records were parsed.", entries.size());
            return entries;

        } catch (IOException | CsvValidationException e) {
            log.error("Error loading CSV file", e);
            throw new IllegalStateException("Failed to load CSV file", e);
        }
    }
}
