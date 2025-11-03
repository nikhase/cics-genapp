# CICS GenApp Customer API - Usage Guide

This guide demonstrates how to use the Customer REST API endpoints with example requests and responses.

## Base URL

```
http://localhost:8080/api/v1
```

## Authentication

All endpoints require a JWT bearer token from Zitadel OIDC provider.

### Header Format

```
Authorization: Bearer <JWT_TOKEN>
```

### Example with curl

```bash
TOKEN="your_jwt_token_here"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/customers
```

## Endpoints

### 1. Create a Customer (POST)

Creates a new customer record.

**Endpoint:** `POST /api/v1/customers`

**Required Role:** CUSTOMER_SERVICE_AGENT or ADMIN

**Request Body:**

```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "dateOfBirth": "1990-05-15",
  "email": "jane.smith@example.com",
  "phone": "+1-555-123-4567",
  "address": "123 Main Street",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62701"
}
```

**curl Example:**

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phone": "+1-555-123-4567"
  }'
```

**Success Response (201 Created):**

```json
{
  "data": {
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Jane",
    "lastName": "Smith",
    "dateOfBirth": "1990-05-15",
    "email": "jane.smith@example.com",
    "phone": "+1-555-123-4567",
    "address": "123 Main Street",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "status": "ACTIVE",
    "createdAt": "2025-11-03T14:45:00",
    "updatedAt": "2025-11-03T14:45:00",
    "createdBy": "user@example.com",
    "updatedBy": "user@example.com"
  },
  "metadata": {
    "timestamp": "2025-11-03T14:45:00",
    "version": "v1",
    "operation": "CREATE"
  }
}
```

**Error Response (409 Conflict - Email Already Exists):**

```json
{
  "data": null,
  "metadata": {
    "error": "Customer with this email already exists",
    "timestamp": "2025-11-03T14:46:00",
    "version": "v1"
  }
}
```

### 2. Get Customer by ID (GET)

Retrieves a specific customer by ID.

**Endpoint:** `GET /api/v1/customers/{customerId}`

**Required Role:** Any authenticated user (read-only)

**curl Example:**

```bash
CUSTOMER_ID="550e8400-e29b-41d4-a716-446655440000"
curl -X GET "http://localhost:8080/api/v1/customers/$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN"
```

**Success Response (200 OK):**

```json
{
  "data": {
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Jane",
    "lastName": "Smith",
    "dateOfBirth": "1990-05-15",
    "email": "jane.smith@example.com",
    "phone": "+1-555-123-4567",
    "address": "123 Main Street",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "status": "ACTIVE",
    "createdAt": "2025-11-03T14:45:00",
    "updatedAt": "2025-11-03T14:45:00",
    "createdBy": "user@example.com",
    "updatedBy": "user@example.com"
  },
  "metadata": {
    "timestamp": "2025-11-03T14:45:00",
    "version": "v1",
    "operation": "READ"
  }
}
```

**Error Response (404 Not Found):**

```json
{
  "data": null,
  "metadata": {
    "error": "Customer not found",
    "timestamp": "2025-11-03T14:46:00",
    "version": "v1"
  }
}
```

### 3. Search/List Customers (GET)

Searches for customers with pagination, filtering, and sorting.

**Endpoint:** `GET /api/v1/customers`

**Query Parameters:**

- `query` (optional): Search string (searches firstName, lastName, email, phone)
- `status` (optional): Filter by ACTIVE or INACTIVE
- `limit` (optional, default 50, max 100): Page size
- `offset` (optional, default 0): Pagination offset
- `sortBy` (optional, default "lastName"): Field to sort by
- `sortOrder` (optional, default "ASC"): Sort direction (ASC or DESC)

**curl Example:**

```bash
curl -X GET "http://localhost:8080/api/v1/customers?query=Smith&status=ACTIVE&limit=10&offset=0&sortBy=lastName&sortOrder=ASC" \
  -H "Authorization: Bearer $TOKEN"
```

**Success Response (200 OK):**

```json
{
  "data": {
    "data": [
      {
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "firstName": "Jane",
        "lastName": "Smith",
        "email": "jane.smith@example.com",
        "status": "ACTIVE",
        "createdAt": "2025-11-03T14:45:00"
      },
      {
        "customerId": "660e8400-e29b-41d4-a716-446655440001",
        "firstName": "John",
        "lastName": "Smith",
        "email": "john.smith@example.com",
        "status": "ACTIVE",
        "createdAt": "2025-11-03T14:46:00"
      }
    ],
    "pagination": {
      "limit": 10,
      "offset": 0,
      "total": 2,
      "hasMore": false
    }
  },
  "metadata": {
    "timestamp": "2025-11-03T14:46:00",
    "version": "v1",
    "operation": "SEARCH",
    "resultCount": 2
  }
}
```

### 4. Update a Customer (PUT)

Updates an existing customer record. Only non-null fields in the request are updated.

**Endpoint:** `PUT /api/v1/customers/{customerId}`

**Required Role:** CUSTOMER_SERVICE_AGENT or ADMIN

**Request Body (all fields optional):**

```json
{
  "firstName": "Jane",
  "lastName": "Smith-Johnson",
  "email": "jane.smith.johnson@example.com",
  "phone": "+1-555-123-4568"
}
```

**curl Example:**

```bash
CUSTOMER_ID="550e8400-e29b-41d4-a716-446655440000"
curl -X PUT "http://localhost:8080/api/v1/customers/$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "lastName": "Smith-Johnson"
  }'
```

**Success Response (200 OK):**

```json
{
  "data": {
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Jane",
    "lastName": "Smith-Johnson",
    "dateOfBirth": "1990-05-15",
    "email": "jane.smith@example.com",
    "phone": "+1-555-123-4567",
    "status": "ACTIVE",
    "createdAt": "2025-11-03T14:45:00",
    "updatedAt": "2025-11-03T15:00:00",
    "createdBy": "user@example.com",
    "updatedBy": "user@example.com"
  },
  "metadata": {
    "timestamp": "2025-11-03T15:00:00",
    "version": "v1",
    "operation": "UPDATE"
  }
}
```

### 5. Delete a Customer (Soft Delete) (DELETE)

Soft-deletes a customer by marking status as INACTIVE. Data is preserved for audit/compliance.

**Endpoint:** `DELETE /api/v1/customers/{customerId}`

**Required Role:** COMPLIANCE_OFFICER or ADMIN

**Optional Request Body:**

```json
{
  "reason": "Customer requested account closure"
}
```

**curl Example:**

```bash
CUSTOMER_ID="550e8400-e29b-41d4-a716-446655440000"
curl -X DELETE "http://localhost:8080/api/v1/customers/$CUSTOMER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Customer requested account closure"
  }'
```

**Success Response (200 OK):**

```json
{
  "data": {
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "status": "INACTIVE",
    "createdAt": "2025-11-03T14:45:00",
    "updatedAt": "2025-11-03T15:05:00"
  },
  "metadata": {
    "timestamp": "2025-11-03T15:05:00",
    "version": "v1",
    "operation": "DELETE",
    "deletionType": "SOFT_DELETE"
  }
}
```

## Audit Log Endpoints

### 1. Query Audit Logs (GET)

Queries audit logs with optional filtering. Restricted to ADMIN and COMPLIANCE_OFFICER roles.

**Endpoint:** `GET /api/v1/audit`

**Query Parameters:**

- `entity` (optional): Filter by entity type (CUSTOMER, POLICY)
- `entityId` (optional): Filter by specific entity ID
- `operation` (optional): Filter by operation (CREATE, READ, UPDATE, DELETE)
- `limit` (optional, default 100, max 500): Page size
- `offset` (optional, default 0): Pagination offset

**curl Example:**

```bash
curl -X GET "http://localhost:8080/api/v1/audit?entity=CUSTOMER&limit=10" \
  -H "Authorization: Bearer $TOKEN"
```

**Success Response (200 OK):**

```json
{
  "data": {
    "data": [
      {
        "auditId": "770e8400-e29b-41d4-a716-446655440002",
        "timestamp": "2025-11-03T15:00:00",
        "userId": "compliance_officer@example.com",
        "operation": "CREATE",
        "entityType": "CUSTOMER",
        "entityId": "550e8400-e29b-41d4-a716-446655440000",
        "changes": "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\"}",
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0..."
      },
      {
        "auditId": "880e8400-e29b-41d4-a716-446655440003",
        "timestamp": "2025-11-03T15:05:00",
        "userId": "agent@example.com",
        "operation": "UPDATE",
        "entityType": "CUSTOMER",
        "entityId": "550e8400-e29b-41d4-a716-446655440000",
        "changes": "{\"lastName\":{\"before\":\"Smith\",\"after\":\"Smith-Johnson\"}}",
        "ipAddress": "192.168.1.101",
        "userAgent": "Mozilla/5.0..."
      }
    ],
    "pagination": {
      "limit": 10,
      "offset": 0,
      "total": 2,
      "hasMore": false
    }
  },
  "metadata": {
    "timestamp": "2025-11-03T15:05:00",
    "version": "v1",
    "operation": "QUERY_AUDIT",
    "resultCount": 2,
    "totalCount": 2
  }
}
```

### 2. Get Audit Log by ID (GET)

Retrieves a specific audit log entry by ID.

**Endpoint:** `GET /api/v1/audit/{auditId}`

**curl Example:**

```bash
AUDIT_ID="770e8400-e29b-41d4-a716-446655440002"
curl -X GET "http://localhost:8080/api/v1/audit/$AUDIT_ID" \
  -H "Authorization: Bearer $TOKEN"
```

**Success Response (200 OK):**

```json
{
  "data": {
    "auditId": "770e8400-e29b-41d4-a716-446655440002",
    "timestamp": "2025-11-03T15:00:00",
    "userId": "user@example.com",
    "operation": "CREATE",
    "entityType": "CUSTOMER",
    "entityId": "550e8400-e29b-41d4-a716-446655440000",
    "changes": "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\"}",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0..."
  },
  "metadata": {
    "timestamp": "2025-11-03T15:05:00",
    "version": "v1",
    "operation": "READ_AUDIT"
  }
}
```

## HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Request successful |
| 201 | Created | New resource created |
| 400 | Bad Request | Invalid parameters or validation failed |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Insufficient permissions for the operation |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Email already exists or version conflict |
| 500 | Internal Server Error | Server error |

## Error Response Format

All error responses follow this format:

```json
{
  "data": null,
  "metadata": {
    "error": "Error message describing what went wrong",
    "timestamp": "2025-11-03T15:05:00",
    "version": "v1",
    "status": 400
  }
}
```

## Validation Errors

When validation fails (400 Bad Request), the response includes field-level errors:

```json
{
  "data": null,
  "metadata": {
    "error": "Validation failed",
    "timestamp": "2025-11-03T15:05:00",
    "version": "v1",
    "status": 400,
    "fieldErrors": [
      {
        "field": "email",
        "message": "Email must be a valid email address"
      },
      {
        "field": "firstName",
        "message": "First name is required"
      }
    ]
  }
}
```

## Rate Limiting

Endpoints support unlimited requests currently, but this may be rate-limited in future versions.

## Pagination

List endpoints support pagination with `limit` and `offset` parameters:

- `limit`: Number of items per page (default 50, max 100 for customer search, max 500 for audit queries)
- `offset`: Zero-based starting position
- `hasMore`: Boolean indicating if more results are available

## Sorting

Search endpoints support sorting with `sortBy` and `sortOrder` parameters:

- `sortBy`: Field to sort by (e.g., "lastName", "email", "createdAt")
- `sortOrder`: Direction - "ASC" (ascending) or "DESC" (descending)

## Interactive API Documentation

Visit Swagger UI for interactive endpoint documentation:

```
http://localhost:8080/api/docs
```

## OpenAPI Specification

The full OpenAPI 3.0 specification is available at:

```
http://localhost:8080/v3/api-docs
```
