# Error Handling & Validation Framework

## Overview

The CICS GenApp API implements a comprehensive error handling and validation framework that ensures all responses, including errors, follow a consistent structure. This guide documents all error codes, HTTP status codes, validation rules, and error response formats.

## Error Response Format

All error responses follow this consistent JSON structure:

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "User-friendly error message",
    "details": [
      {
        "field": "fieldName",
        "message": "Validation error message",
        "value": "rejected_value"
      }
    ]
  },
  "metadata": {
    "timestamp": "2025-11-03T14:30:45.123456",
    "traceId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

### Error Response Structure

- **error.code** (string): Machine-readable error code (e.g., VALIDATION_ERROR, RESOURCE_NOT_FOUND)
- **error.message** (string): User-friendly error message describing what went wrong
- **error.details** (array, optional): Field-level validation errors (only present for 400 Bad Request)
  - **field** (string): Name of the field with validation error
  - **message** (string): Validation error message
  - **value** (string): The rejected/invalid value that was submitted
- **metadata.timestamp** (ISO 8601): When the error occurred
- **metadata.traceId** (UUID): Correlation ID for tracking requests through logs

## HTTP Status Codes

### 200 OK
**Success**: Request succeeded and response body contains the result.

Example use case: GET /api/v1/customers/{id}

### 201 Created
**Success**: Resource was successfully created. Response includes the created resource.

Example use case: POST /api/v1/customers

### 400 Bad Request
**Client Error**: Request is invalid (validation errors, malformed JSON, invalid parameters).

**Common error codes**:
- `VALIDATION_ERROR` - Field validation failed
- `INVALID_FORMAT` - Invalid UUID format or data type

**Example response**:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": [
      {
        "field": "email",
        "message": "Email must be a valid email address",
        "value": "invalid-email"
      }
    ]
  },
  "metadata": { "timestamp": "...", "traceId": "..." }
}
```

### 401 Unauthorized
**Authentication Error**: Request lacks valid authentication credentials.

**Scenarios**:
- Missing OAuth2 token
- Expired or invalid JWT token
- Missing Bearer token header

**Response**: Handled by Spring Security (not via GlobalExceptionHandler)

### 403 Forbidden
**Authorization Error**: User is authenticated but lacks permission to access the resource.

**Scenarios**:
- User lacks required role (e.g., CUSTOMER_SERVICE_AGENT)
- User attempting to delete without COMPLIANCE_OFFICER role

**Response**: Handled by Spring Security (not via GlobalExceptionHandler)

### 404 Not Found
**Resource Error**: Requested resource does not exist.

**Common error codes**:
- `RESOURCE_NOT_FOUND` - Customer with given ID not found

**Example response**:
```json
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Customer 550e8400-e29b-41d4-a716-446655440000 not found",
    "details": null
  },
  "metadata": { "timestamp": "...", "traceId": "..." }
}
```

### 409 Conflict
**State Error**: Request conflicts with current system state or existing data.

**Common error codes**:
- `DUPLICATE_KEY` - Email already in use by another customer
- `OPTIMISTIC_LOCK_CONFLICT` - Concurrent update detected (version mismatch)

**Example response**:
```json
{
  "error": {
    "code": "DUPLICATE_KEY",
    "message": "Customer with this email already exists",
    "details": null
  },
  "metadata": { "timestamp": "...", "traceId": "..." }
}
```

### 500 Internal Server Error
**Server Error**: Unexpected error on the server. Details are logged but not exposed to client.

**Common error codes**:
- `INTERNAL_SERVER_ERROR` - Unexpected exception

**Response**: Generic message (actual error logged with traceId for support reference)

```json
{
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "An unexpected error occurred. Please contact support with trace ID.",
    "details": null
  },
  "metadata": { "timestamp": "...", "traceId": "550e8400-e29b-41d4-a716-446655440000" }
}
```

## Error Codes Reference

### Validation Errors (400 Bad Request)

| Error Code | Message | Trigger |
|-----------|---------|---------|
| VALIDATION_ERROR | Validation failed | Field validation constraint violated |
| INVALID_FORMAT | Invalid format | Invalid UUID, date, or data type |

### Business Logic Errors (409 Conflict)

| Error Code | Message | Trigger |
|-----------|---------|---------|
| DUPLICATE_KEY | Customer with this email already exists | Attempt to create/update with duplicate email |
| OPTIMISTIC_LOCK_CONFLICT | Resource was modified by another user. Please refresh and try again. | Concurrent update detected (version mismatch) |

### Resource Errors (404 Not Found)

| Error Code | Message | Trigger |
|-----------|---------|---------|
| RESOURCE_NOT_FOUND | Customer {id} not found | Attempt to read/update/delete non-existent customer |

### System Errors (500 Internal Server Error)

| Error Code | Message | Trigger |
|-----------|---------|---------|
| INTERNAL_SERVER_ERROR | An unexpected error occurred. Please contact support with trace ID. | Unexpected exception |

## Field Validation Rules

### Customer Entity & Request DTOs

#### firstName (required)
- **Annotation**: `@NotNull`, `@Size(min=1, max=100)`
- **Rules**: Required, 1-100 characters
- **Error Code (if validation fails)**: `NotNull`, `Size`
- **Example error message**: "First name must be between 1 and 100 characters"

#### lastName (required)
- **Annotation**: `@NotNull`, `@Size(min=1, max=100)`
- **Rules**: Required, 1-100 characters
- **Error Code**: `NotNull`, `Size`
- **Example error message**: "Last name must be between 1 and 100 characters"

#### email (required, unique)
- **Annotations**: `@NotNull`, `@Email`, `@ValidEmail` (custom)
- **Rules**: Required, valid RFC 5322 format, unique across system
- **Error Codes**: `NotNull`, `Email`
- **Example error messages**:
  - "Email is required"
  - "Email must be a valid email address"
  - "Email must be valid and unique" (via @ValidEmail)

#### dateOfBirth (optional)
- **Annotations**: `@PastOrPresent`, `@ValidAge` (custom)
- **Rules**: If provided, must be today or in the past, age must be >= 18
- **Error Codes**: `PastOrPresent`, `ValidAge`
- **Example error messages**:
  - "Date of birth must be in the past or today"
  - "Customer must be at least 18 years old"

#### phone (optional)
- **Annotation**: `@Pattern(regexp="^\\+?[1-9]\\d{1,14}$|^$")`
- **Rules**: If provided, must be valid E.164 international format or empty
- **Format Examples**:
  - `+12025551234` (US)
  - `+441234567890` (UK)
  - `+49301234567` (Germany)
  - Empty string (optional field)
- **Error Code**: `Pattern`
- **Example error message**: "Phone number must be in valid international format (E.164) or empty"

#### zipCode (optional)
- **Annotation**: `@Pattern(regexp="^\d{5,6}$")`
- **Rules**: If provided, must be 5-6 digits
- **Error Code**: `Pattern`
- **Example error message**: "Zip code must be 5-6 digits"

### CreateCustomerRequest
All constraints from Customer entity apply. All fields must meet validation before request is processed.

### UpdateCustomerRequest
All fields are optional (supports partial updates). Constraints apply only to fields provided in request.

## Custom Validators

### @ValidAge
**Purpose**: Validates that date of birth corresponds to age >= 18 years old

**Implementation**: `ValidAgeValidator`
- Checks if `age = today - dateOfBirth >= 18 years`
- Allows null values (use @NotNull if null not allowed)
- Returns false if age < 18

**Usage**:
```java
@ValidAge(message = "Customer must be at least 18 years old")
private LocalDate dateOfBirth;
```

### @ValidEmail
**Purpose**: Validates that email is unique across the system

**Implementation**: `ValidEmailValidator`
- Checks if email already exists in CustomerRepository
- Allows null values (use @NotNull if null not allowed)
- Queries database for email uniqueness

**Usage**:
```java
@NotNull
@Email
@ValidEmail(message = "Email must be valid and unique")
private String email;
```

## Tracing & Support Reference

Every error response includes a **traceId** (UUID) that correlates the error with logs. This enables:

1. **Support Reference**: Users can provide traceId when reporting issues
2. **Log Correlation**: Developers can search logs using traceId to see full request context
3. **Debugging**: Track request through entire system (API → Service → Repository)

### Using traceId for Debugging

**Client provides traceId**:
```
"The create customer API returned an error. My trace ID is: 550e8400-e29b-41d4-a716-446655440000"
```

**Developer looks up logs**:
```bash
# Search logs for traceId
grep "550e8400-e29b-41d4-a716-446655440000" app.log
```

**Log output** (JSON structured logging from Story 1.7):
```json
{
  "timestamp": "2025-11-03T14:30:45.123456",
  "level": "ERROR",
  "logger": "com.example.cicsgenapp.exception.GlobalExceptionHandler",
  "message": "Validation error",
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "exception": "...",
  "stackTrace": "..."
}
```

## Testing Error Scenarios

### Unit Tests (Validators)

```java
// Test age validation
ValidAgeValidator validator = new ValidAgeValidator();

// Age = 18: Valid
assertTrue(validator.isValid(LocalDate.now().minusYears(18), context));

// Age = 17: Invalid
assertFalse(validator.isValid(LocalDate.now().minusYears(17), context));

// Null: Valid (allow null unless @NotNull present)
assertTrue(validator.isValid(null, context));
```

### Integration Tests (API Endpoints)

```java
// Test validation error
mockMvc.perform(post("/api/v1/customers")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(invalidRequest)))
  .andExpect(status().isBadRequest())
  .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")))
  .andExpect(jsonPath("$.error.details[0].field", is("email")))
  .andExpect(jsonPath("$.metadata.traceId", notNullValue()));

// Test duplicate key error
mockMvc.perform(post("/api/v1/customers")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
  .andExpect(status().isConflict())
  .andExpect(jsonPath("$.error.code", is("DUPLICATE_KEY")));
```

## Implementation Files

| File | Purpose |
|------|---------|
| `exception/GlobalExceptionHandler.java` | Central exception handler (@ControllerAdvice) |
| `exception/ResourceNotFoundException.java` | Custom exception for 404 errors |
| `exception/DuplicateKeyException.java` | Custom exception for 409 conflicts |
| `exception/CustomerAlreadyExistsException.java` | Extends DuplicateKeyException for email conflicts |
| `exception/ValidationException.java` | Custom exception for validation errors |
| `exception/OptimisticLockException.java` | Custom exception for concurrent update conflicts |
| `validator/ValidAge.java` | Age validation annotation |
| `validator/ValidAgeValidator.java` | Age validation implementation |
| `validator/ValidEmail.java` | Email uniqueness annotation |
| `validator/ValidEmailValidator.java` | Email uniqueness implementation |
| `util/MDCUtil.java` | Utility for traceId MDC management |

## Configuration Notes

### Spring Validation Setup

**pom.xml** (already configured):
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Enabling validation in Controller**:
```java
@PostMapping("/api/v1/customers")
public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
    @Valid @RequestBody CreateCustomerRequest request) {
  // @Valid triggers validation of request DTO
}
```

### MDC Configuration

**LoggingFilter** (sets traceId for all requests):
```java
MDC.put("traceId", UUID.randomUUID().toString());
// ... process request ...
MDC.clear();
```

**GlobalExceptionHandler** (includes traceId in errors):
```java
String traceId = MDC.get("traceId");
if (traceId == null) {
  traceId = UUID.randomUUID().toString();
}
return new ErrorResponse(details, new ErrorMetadata(LocalDateTime.now(), traceId));
```

## API Documentation (Swagger/OpenAPI)

All error responses are documented in OpenAPI:

```java
@PostMapping("/api/v1/customers")
@Operation(summary = "Create a new customer")
@ApiResponse(responseCode = "201", description = "Customer created successfully")
@ApiResponse(responseCode = "400", description = "Validation error",
  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "409", description = "Duplicate email",
  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
    @Valid @RequestBody CreateCustomerRequest request) {
  // ...
}
```

## Security Notes

### No PII in Error Messages

Sensitive data is NEVER included in error responses or logs:

❌ Bad: "Email john.doe@example.com already exists"
✅ Good: "Customer with this email already exists"

❌ Bad: "Invalid phone number +12025551234"
✅ Good: "Phone number must be in valid international format"

### Stack Traces

- **Development**: Stack traces included in logs (for debugging)
- **Production**: Stack traces in logs only, generic message in error response
- **Client Response**: Never includes stack trace (security & user experience)

## Related Stories & Documentation

- **Story 1.2**: Spring Boot Starter Project (basic exception handling foundation)
- **Story 1.7**: Structured Logging & Observability (JSON logging with traceId)
- **Story 2.2-2.6**: Customer CRUD APIs (use this validation & error handling framework)
- **Story 2.1**: Customer Domain Model (entity-level validation annotations)

## Version History

| Date | Version | Changes |
|------|---------|---------|
| 2025-11-03 | 1.0.0 | Initial documentation for validation and error handling framework |
