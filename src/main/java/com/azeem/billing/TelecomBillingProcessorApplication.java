package com.azeem.billing;

import com.azeem.billing.service.BillingIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.io.File;
import java.io.FileInputStream;

@SpringBootApplication
public class TelecomBillingProcessorApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TelecomBillingProcessorApplication.class);

        app.addListeners(event -> {
            if (event instanceof ApplicationReadyEvent readyEvent) {
                File file = new File("src/main/resources/azeemcom_telecom_usage_2025_01.csv");
                // try-with-resources ensures the file is closed properly
                try (FileInputStream fis = new FileInputStream(file)) {
                    readyEvent.getApplicationContext()
                            .getBean(BillingIngestionService.class)
                            .ingestData("2025-01", fis);
                } catch (Exception e) {
                    System.err.println("Bootstrap ingestion failed");
                }
            }
        });

        app.run(args);
    }
}
