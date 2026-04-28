/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.etl;

import com.azeem.blueprint.util.BillingFileReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Reads billing data from a tab-separated values (TSV) file and returns
 * raw rows as string arrays.
 *
 * <p>This reader behaves identically to {@link CsvBillingReader} but uses
 * tab-delimited input instead of commas.</p>
 *
 * <p>The class exists to isolate file format differences and avoid
 * conditional parsing logic.</p>
 *
 * <p>All data is returned in raw string form and must be transformed
 * by a downstream assembler.</p>
 *
 * <h3>Design Notes</h3>
 * <ul>
 *   <li>Format-specific reader implementation</li>
 *   <li>No domain awareness</li>
 *   <li>Fail-fast on malformed input</li>
 * </ul>
 */
public class TsvBillingReader implements BillingFileReader, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TsvBillingReader.class);

    private final CSVReader reader;
    private final boolean hasHeader;
    private boolean headerSkipped = false;

    public TsvBillingReader(InputStream inputStream, boolean hasHeader) {

        this.reader = new CSVReaderBuilder(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )
                .withSkipLines(0)
                .withCSVParser(
                        new com.opencsv.CSVParserBuilder()
                                .withSeparator('\t')
                                .build()
                )
                .build();

        this.hasHeader = hasHeader;
    }

    @Override
    public String[] parseNextRow() {
        try {
            if (hasHeader && !headerSkipped) {
                reader.readNext(); // skip header
                headerSkipped = true;
            }

            return reader.readNext();

        } catch (IOException | CsvValidationException e) {
            log.error("Error reading TSV file", e);
            throw new IllegalStateException("Failed to read TSV file", e);
        }
    }

    @Override
    public void close() {
        log.info("Closing TSV reader");
        try {
            reader.close();
        } catch (IOException e) {
            log.warn("Failed to close reader cleanly", e);
        }
    }
}