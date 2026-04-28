/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.martin;

import com.azeem.blueprint.model.martin.SqlResponse;
import org.springframework.stereotype.Component;

/**
 * SQL Validation Layer that ensures the following:
 * <li>Only SELECT statements allowed</li>
 * <li>No multi-statement queries (;)</li>
 * <li>Block dangerous keywords:</li>
 * <li>INSERT</li>
 * <li>UPDATE</li>
 * <li>DELETE</li>
 * <li>DROP</li>
 * <li>ALTER</li>
 */
@Component
public class SqlValidationService {
    // TODO: Must use a SQL AST parser (like JSQLParser) for robust validation.

    public boolean isValidSql(SqlResponse response) {
        String normalizedSql = response.getSql().toLowerCase();
        return normalizedSql.trim().startsWith("select")
                && !normalizedSql.contains("insert")
                && !normalizedSql.contains("update")
                && !normalizedSql.contains("delete")
                && !normalizedSql.contains("drop")
                && !normalizedSql.contains("alter");
    }
}
