/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.etl;

import com.azeem.blueprint.util.BillingFileReader;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads billing data from a comma-separated values (CSV) file and returns raw rows as string
 * arrays.
 *
 * <p>This class represents the <b>Extract</b> phase of the ingestion pipeline. It is responsible
 * only for file I/O and CSV mechanics.
 *
 * <p>The reader performs no domain mapping, validation, or type conversion. Each row is returned
 * exactly as read from the file.
 *
 * <p>This component is immutable and intended for single-use ingestion jobs.
 *
 * <h3>Responsibilities</h3>
 *
 * <ul>
 *   <li>Open and read CSV files
 *   <li>Handle header row skipping if configured
 *   <li>Return raw tabular data
 * </ul>
 *
 * <p>Failures during reading result in a runtime exception and halt ingestion.
 */
public class CsvBillingReader implements BillingFileReader, AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(CsvBillingReader.class);

  private final InputStream inputStream;
  private final CSVReader csvReader;
  private final boolean hasHeader;
  private boolean headerSkipped = false;

  public CsvBillingReader(InputStream inputStream, boolean hasHeader) {
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
