## Future Architecture Considerations

These are natural next steps based on the current codebase, not implemented features.

### Harden AI Query Execution

- Replace string checks with SQL AST parsing.
- Enforce table and column allowlists.
- Enforce `billing_period` predicate programmatically.
- Add query timeout and row limit.
- Consider read-only DB user for Martin queries.

### Add Real Authentication

- Add Spring Security only when product needs protected access.
- Decide between session auth and token auth.
- Remove credential logging from `login.js`.
- Protect upload and Martin endpoints before public exposure.

### Optimize Bulk Ingestion Pipeline

- Replace the current parse → domain model → JPA entity flow with a two-path architecture.
- Retain JPA for transactional CRUD operations and single-record business logic.
- Introduce a dedicated bulk path using PostgreSQL `COPY` via `CopyManager` for mass CSV ingestion.
- Use `DataSourceUtils.getConnection()` instead of raw `DataSource.getConnection()` to keep bulk loads within Spring's transaction manager.
- Evaluate Spring Batch for chunked reads, parallel processing, and restart/retry on ingestion failure.
- Normalize the billing schema into `departments`, `employees`, and `billing_records` tables to reduce INSERT payload size and improve query performance.
- Disable indexes during bulk load and rebuild post-ingestion to eliminate per-row index maintenance overhead.


### Splunk SIEM Integration

- Ship structured application logs to Splunk via the HTTP Event Collector (HEC).
- Define log schemas for key pipeline events: ingestion start/end, row counts, RAG query execution, and AI response latency.
- Create Splunk alerts for anomalous ingestion volumes, query failures, and slow retrieval times.
- Tag all events with `billing_period`, `department`, and `employee_id` where applicable for correlation across dashboards.
- Explore Splunk SOAR integration to trigger automated responses on detected pipeline anomalies or security events.

### Add Migrations

- Introduce Flyway or Liquibase.
- Stop relying on `ddl-auto` for persistent environments.
- Fix production-like Compose to use `prod` profile or a safer profile.

### Improve Frontend Robustness

- Add error states for failed API calls.
- Add loading states for dashboard sections.
- Add frontend tests if the static UI grows.
- Keep the no-build approach only while UI complexity stays small.