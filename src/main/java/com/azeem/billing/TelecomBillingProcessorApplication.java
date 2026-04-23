/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing;

import com.azeem.billing.service.BillingIngestionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.io.File;
import java.io.FileInputStream;

@SpringBootApplication
public class TelecomBillingProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelecomBillingProcessorApplication.class, args);
    }
}
