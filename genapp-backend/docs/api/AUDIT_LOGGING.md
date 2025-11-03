# Audit Logging - Implementation Guide

## Overview

The CICS GenApp Customer API implements comprehensive audit logging for compliance and regulatory requirements. All customer operations (CREATE, READ, UPDATE, DELETE) are automatically logged with full context including user identity, IP address, timestamp, and detailed change information.

## Purpose

Audit logging serves multiple critical functions:

1. **Compliance**: Maintain immutable records of all data mutations for regulatory compliance
2. **Security**: Track who accessed what data and when for forensic analysis
3. **Debugging**: Identify root causes of data inconsistencies
4. **Accountability**: Ensure user actions are traceable and attributed
5. **Audit Trails**: Support audits and investigations with complete operation history

## Architecture

### Components

1. **AuditLog Entity**: JPA entity mapping to `audit_log` PostgreSQL table
2. **AuditService**: Service layer handling audit entry creation
3. **AuditLogRepository**: Spring Data JPA repository for audit log queries
4. **AuditController**: REST API controller for compliance officers to query audit logs

### Data Flow

```
User Action (CREATE/READ/UPDATE/DELETE)
    ↓
CustomerController Endpoint
    ↓
CustomerService Business Logic
    ↓
AuditService.createAuditEntry()
    ↓
AuditLog Entity (saved to PostgreSQL)
    ↓
Immutable Audit Trail (no updates/deletes allowed)
```

## Logged Operations

### CREATE Operation

When a new customer is created:

```json
{
  "operation": "CREATE",
  "entityType": "CUSTOMER",
  "entityId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user@example.com",
  "timestamp": "2025-11-03T14:45:00",
  "changes": {
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phone": "+1-555-123-4567",
    "status": "ACTIVE"
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

**Logged by:** CustomerService.createCustomer()

### READ Operation

When a customer is retrieved:

```json
{
  "operation": "READ",
  "entityType": "CUSTOMER",
  "entityId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user@example.com",
  "timestamp": "2025-11-03T14:46:00",
  "changes": {
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "status": "ACTIVE"
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

**Logged by:** CustomerService.getCustomer()

**Note:** READ operations are logged for security and compliance, allowing tracking of who accessed sensitive customer data.

### UPDATE Operation

When a customer is updated:

```json
{
  "operation": "UPDATE",
  "entityType": "CUSTOMER",
  "entityId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user@example.com",
  "timestamp": "2025-11-03T14:50:00",
  "changes": {
    "lastName": {
      "before": "Smith",
      "after": "Smith-Johnson"
    },
    "email": {
      "before": "jane.smith@example.com",
      "after": "jane.smith.johnson@example.com"
    }
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

**Logged by:** CustomerService.updateCustomer()

**Key Features:**
- Only changed fields are recorded (not the entire object)
- Before/after values captured for compliance verification
- Enables reconstruction of historical state

### DELETE Operation (Soft Delete)

When a customer is marked as INACTIVE:

```json
{
  "operation": "DELETE",
  "entityType": "CUSTOMER",
  "entityId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user@example.com",
  "timestamp": "2025-11-03T15:00:00",
  "changes": {
    "status": "INACTIVE",
    "deletedAt": "2025-11-03T15:00:00",
    "deletionReason": "Customer requested account closure"
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

**Logged by:** CustomerService.deleteCustomer()

**Note:** Soft delete preserves the customer record with reason for audit compliance.

## Audit Log Table Schema

```sql
CREATE TABLE audit_log (
    audit_id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255),
    operation VARCHAR(20) NOT NULL,  -- CREATE, READ, UPDATE, DELETE
    entity_type VARCHAR(100) NOT NULL,  -- CUSTOMER, POLICY
    entity_id UUID,
    changes JSONB,  -- JSON object with change details
    ip_address VARCHAR(45),  -- IPv4 or IPv6
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for common queries
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_operation ON audit_log(operation);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp DESC);
```

## Querying Audit Logs

### Via API (Recommended)

Only ADMIN and COMPLIANCE_OFFICER roles can query audit logs via the REST API:

```bash
# Query all audit logs for a specific customer
curl -X GET "http://localhost:8080/api/v1/audit?entity=CUSTOMER&entityId=550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN"

# Query all CREATE operations
curl -X GET "http://localhost:8080/api/v1/audit?operation=CREATE" \
  -H "Authorization: Bearer $TOKEN"

# Query all audit logs with pagination
curl -X GET "http://localhost:8080/api/v1/audit?limit=50&offset=0" \
  -H "Authorization: Bearer $TOKEN"
```

### Via SQL (Advanced)

Direct SQL queries against the audit_log table:

```sql
-- Get all operations for a specific customer
SELECT * FROM audit_log
WHERE entity_type = 'CUSTOMER'
  AND entity_id = '550e8400-e29b-41d4-a716-446655440000'
ORDER BY timestamp DESC;

-- Get all modifications made by a user
SELECT * FROM audit_log
WHERE user_id = 'user@example.com'
  AND operation IN ('CREATE', 'UPDATE', 'DELETE')
ORDER BY timestamp DESC;

-- Get audit entries within a date range
SELECT * FROM audit_log
WHERE timestamp >= '2025-11-01' AND timestamp <= '2025-11-30'
ORDER BY timestamp DESC;

-- Analyze change patterns
SELECT operation, entity_type, COUNT(*) as count
FROM audit_log
WHERE timestamp >= NOW() - INTERVAL '7 days'
GROUP BY operation, entity_type;
```

## Immutability

Audit logs are designed to be immutable to ensure integrity:

1. **No Updates**: AuditLog entity has no setter methods for audit-critical fields
2. **No Deletes**: No delete operation is allowed on audit_log table
3. **Insert Only**: Only INSERT operations are performed on the table
4. **Timestamp**: `timestamp` field is set at creation and cannot be modified

### Preventing Modifications

- The AuditLog entity uses constructor-based initialization with immutable fields
- Spring Data JPA prevents PATCH/PUT operations on the entity
- Database constraints ensure row-level immutability (future enhancement)

## Security Considerations

### User ID Extraction

User ID is extracted from JWT token claims:

```java
// In AuditService.extractUserId()
Object principal = SecurityContextHolder.getContext()
    .getAuthentication()
    .getPrincipal();
```

This ensures user accountability for all operations.

### IP Address Tracking

Client IP address is extracted from HTTP headers (with proxy support):

```
X-Forwarded-For (preferred for proxied requests)
X-Real-IP (alternative)
request.getRemoteAddr() (fallback)
```

This provides geographic/network context for security analysis.

### User Agent Logging

User-Agent header is captured for:

- Device/browser identification
- API client tracking
- Detecting unusual access patterns

## Retention Policy

**Current Policy:** Audit logs are retained indefinitely.

**Recommended Policy (Future):**
- Transactional audit logs: Retain indefinitely
- READ operation logs: Retain for 7 years (regulatory compliance)
- Archive older logs to cold storage after 2 years

## Compliance Standards

Audit logging supports compliance with:

- **GDPR**: Data access tracking for subject access requests
- **HIPAA**: Audit trails for healthcare data (if applicable)
- **SOC 2**: User access and change logging
- **PCI-DSS**: Cardholder data access audit trails
- **General**: Financial/insurance transaction audit requirements

## Performance Impact

Audit logging is optimized for minimal performance impact:

1. **Asynchronous Logging**: Audit entries are saved within the transaction boundary (not async to avoid loss)
2. **Efficient Storage**: JSON format in JSONB column supports compression
3. **Indexed Queries**: Common query patterns are indexed (entity, operation, user, timestamp)
4. **Row-Level Indexing**: Multiple indexes prevent full table scans

## Troubleshooting

### Audit Entry Not Created

If an audit entry is missing:

1. **Check Permissions**: User must have necessary role (CUSTOMER_SERVICE_AGENT, ADMIN, COMPLIANCE_OFFICER)
2. **Check Database**: Verify audit_log table exists and is accessible
3. **Check Logs**: Look for errors in application logs during the operation
4. **Check Transaction**: Audit entries are created within the same transaction - rollbacks cancel logging

### Querying Issues

If audit queries return no results:

1. **Check Role**: Verify user has ADMIN or COMPLIANCE_OFFICER role
2. **Check Time Range**: Timestamps are in UTC - verify time filters
3. **Check Entity Type**: Must match exactly (case-sensitive: "CUSTOMER", "POLICY")
4. **Check Indexes**: Run `EXPLAIN ANALYZE` on slow queries

### Permission Denied Errors

If you get 403 Forbidden on audit queries:

```
"Forbidden - requires ADMIN or COMPLIANCE_OFFICER role"
```

Solution: Request appropriate role assignment from security team.

## Testing

### Unit Tests

Audit logging includes unit tests verifying:

- Audit entries created for each operation type
- Correct user ID, IP, and timestamp extraction
- Change tracking for UPDATE operations
- Immutability of audit entries

### Integration Tests

Full-stack tests verify:

- End-to-end audit logging through API
- Persistence to database
- Audit log retrieval via API
- Role-based access control

### Manual Testing

```bash
# 1. Create a customer (generates CREATE audit)
CUSTOMER_RESPONSE=$(curl -X POST http://localhost:8080/api/v1/customers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com"}')

CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | jq -r '.data.customerId')

# 2. Get the customer (generates READ audit)
curl -X GET "http://localhost:8080/api/v1/customers/$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN"

# 3. Update the customer (generates UPDATE audit)
curl -X PUT "http://localhost:8080/api/v1/customers/$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"lastName":"UpdatedUser"}'

# 4. Query audit logs to verify all operations were logged
curl -X GET "http://localhost:8080/api/v1/audit?entity=CUSTOMER&entityId=$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN"

# 5. Delete the customer (generates DELETE audit)
curl -X DELETE "http://localhost:8080/api/v1/customers/$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"reason":"Testing"}'

# 6. Verify deletion was logged
curl -X GET "http://localhost:8080/api/v1/audit?entity=CUSTOMER&entityId=$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN"
```

## Future Enhancements

Planned improvements to audit logging:

1. **Correlation IDs**: Link related operations across microservices
2. **Change Notifications**: Alert on sensitive operations (e.g., DELETE)
3. **Retention Policies**: Automatic archival of old audit logs
4. **Audit Reports**: Pre-built reports for compliance teams
5. **Anonymization**: PII redaction in audit logs (GDPR right to be forgotten)
6. **Event Streaming**: Stream audit events to external SIEM systems

## References

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [PostgreSQL JSONB Type](https://www.postgresql.org/docs/current/datatype-json.html)
- [Audit Trail Best Practices](https://en.wikipedia.org/wiki/Audit_trail)
- [OWASP: Logging Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html)
