# Story 2.7: Input Validation and Error Handling Framework

Status: ready-for-dev

## Story

As a Backend Developer,
I want consistent error handling and validation across all customer APIs,
So that clients receive clear, actionable error messages.

## Acceptance Criteria

1. Global exception handler (@ControllerAdvice) created for all endpoints
2. Validation errors return structured response with field-level error details
3. Field-level validation rules enforced:
   - firstName/lastName: required, 1-100 characters
   - email: required, valid email format, unique constraint
   - phone: optional, valid phone format (regex: \+?[\d\-\s()]{7,})
   - dateOfBirth: optional, valid date, age >= 18
   - zipCode: optional, valid format (5-6 digits)
4. Business logic validation:
   - Cannot create customer with duplicate email
   - Cannot update customer to INACTIVE if they are referenced by active entities (future validation)
5. HTTP status codes used correctly:
   - 200 OK: success
   - 201 Created: resource created
   - 400 Bad Request: validation/client error
   - 401 Unauthorized: not authenticated
   - 403 Forbidden: not authorized
   - 404 Not Found: resource not found
   - 409 Conflict: duplicate key or state conflict
   - 500 Internal Server Error: unexpected error
6. Error response includes traceId (correlation ID) for support reference
7. Validation annotations (@NotNull, @Email, @Pattern) used in entity
8. Custom validators for complex rules (age validation, phone format, etc.)

## Tasks / Subtasks

- [ ] Task 1: Create global @ControllerAdvice exception handler (AC: #1, #5, #6)
  - [ ] Create `src/main/java/com/example/cicsgenapp/exception/GlobalExceptionHandler.java`
  - [ ] Annotate with @ControllerAdvice and @ResponseBody
  - [ ] Implement handler methods for all exception types:
    - Handle MethodArgumentNotValidException → extract field errors → return 400 with details
    - Handle CustomerNotFoundException → return 404 with message
    - Handle CustomerAlreadyExistsException → return 409 with message
    - Handle OptimisticLockException (from Story 2.5) → return 409 with message
    - Handle IllegalArgumentException → return 400 Bad Request
    - Handle Exception (catch-all) → return 500 Internal Server Error
  - [ ] Each handler should:
    - Extract traceId from MDC (X-Trace-Id header)
    - Construct StandardErrorResponse with error code, message, details, traceId, timestamp
    - Return ResponseEntity with appropriate HTTP status code

- [ ] Task 2: Create standard error response DTOs (AC: #1, #2, #5)
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/StandardErrorResponse.java`
    - Fields: error (ErrorDetails), metadata (Map with timestamp, traceId)
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/ErrorDetails.java`
    - Fields: code (String), message (String), details (List<FieldError> or null)
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/FieldError.java`
    - Fields: field (String), message (String), value (Object), code (String)
  - [ ] Example response:
    ```json
    {
      "error": {
        "code": "VALIDATION_ERROR",
        "message": "Validation failed",
        "details": [
          {
            "field": "email",
            "message": "Invalid email format",
            "value": "not-an-email",
            "code": "INVALID_FORMAT"
          }
        ]
      },
      "metadata": { "timestamp": "2025-11-01T10:15:00Z", "traceId": "abc123..." }
    }
    ```

- [ ] Task 3: Create custom exception classes (AC: #5)
  - [ ] Create `src/main/java/com/example/cicsgenapp/exception/CustomerNotFoundException.java` extends RuntimeException
  - [ ] Create `src/main/java/com/example/cicsgenapp/exception/CustomerAlreadyExistsException.java` extends RuntimeException
  - [ ] Create `src/main/java/com/example/cicsgenapp/exception/ValidationException.java` extends RuntimeException
  - [ ] Each exception should accept message and optional cause

- [ ] Task 4: Implement field-level validation annotations in DTOs (AC: #3, #7)
  - [ ] In CreateCustomerRequest DTO:
    - @NotNull on firstName, lastName, email
    - @Size(min=1, max=100) on firstName, lastName
    - @Email on email field
    - @Pattern(regexp="\+?[\d\-\s()]{7,}") on phone field (optional)
    - @PastOrPresent on dateOfBirth (optional)
    - @Pattern(regexp="^\d{5,6}$") on zipCode (optional)
  - [ ] In UpdateCustomerRequest DTO: same annotations (all fields optional)
  - [ ] Add custom error messages for each validation: @Size(message="First name must be 1-100 characters")

- [ ] Task 5: Create custom validators for complex business rules (AC: #4, #8)
  - [ ] Create custom validator annotations:
    - `@ValidEmail` - validates email format and uniqueness (async validation)
    - `@ValidAge` - validates age >= 18
    - `@ValidPhoneFormat` - validates phone format with international support
  - [ ] Implement corresponding validator classes:
    - `ValidEmailValidator` - checks email format and queries database
    - `ValidAgeValidator` - calculates age from dateOfBirth
    - `ValidPhoneFormatValidator` - regex pattern matching

- [ ] Task 6: Implement duplicate email validation (AC: #4)
  - [ ] In CustomerService.createCustomer(): Before saving
    - Call customerRepository.findByEmail(email)
    - If exists, throw CustomerAlreadyExistsException with message: "Customer with this email already exists"
  - [ ] In CustomerService.updateCustomer(): If email updated
    - Check new email doesn't exist for different customer
    - Throw CustomerAlreadyExistsException if conflict
  - [ ] Test: Attempt to create/update with existing email, verify 409 response

- [ ] Task 7: Implement validation error extraction and formatting (AC: #2)
  - [ ] In GlobalExceptionHandler.handleMethodArgumentNotValid():
    - Extract BindingResult from MethodArgumentNotValidException
    - Iterate through field errors
    - For each error:
      - field: fieldError.getField()
      - message: fieldError.getDefaultMessage()
      - value: fieldError.getRejectedValue()
      - code: fieldError.getCode() (e.g., "NotNull", "Email", "Size")
    - Build list of FieldError objects
    - Return 400 Bad Request with error details

- [ ] Task 8: Implement business logic validation in service layer (AC: #4)
  - [ ] In CustomerService: Add validation methods for business rules
    - validateUniqueEmail(email, excludeCustomerId) - checks uniqueness
    - validateAge(dateOfBirth) - ensures age >= 18
    - validateCustomerCanBeModified(customerId) - checks if customer can be updated (future: active policies)
  - [ ] Call validation methods in create/update/delete operations
  - [ ] Throw appropriate exception if validation fails
  - [ ] Include clear error message for each validation failure

- [ ] Task 9: Ensure proper HTTP status codes (AC: #5)
  - [ ] Configure exception handler to use correct status codes:
    - 400 Bad Request: validation errors, invalid input, bad format
    - 401 Unauthorized: missing/invalid JWT token (handled by Spring Security)
    - 403 Forbidden: user lacks required role (handled by Spring Security)
    - 404 Not Found: resource doesn't exist
    - 409 Conflict: duplicate email, optimistic lock conflict
    - 500 Internal Server Error: unexpected exceptions
  - [ ] Test each status code with appropriate error scenario
  - [ ] Verify Spring Security returns 401/403 correctly

- [ ] Task 10: Implement traceId propagation in error responses (AC: #6)
  - [ ] In GlobalExceptionHandler: Extract traceId from MDC
    - Use MDCUtil.getTraceId() or MDC.get("X-Trace-Id")
    - If not present, generate new UUID and set in MDC
  - [ ] Include traceId in all error responses (StandardErrorResponse)
  - [ ] Allow clients to reference traceId in support requests
  - [ ] Test: Verify traceId present in all error responses

- [ ] Task 11: Implement logging of validation errors (AC: #1)
  - [ ] In GlobalExceptionHandler: Log all exceptions
    - ERROR level for 400/500 errors
    - WARN level for 404/409 errors
    - Include traceId, error code, message, and field details in logs
    - Format as JSON structured logs (from Story 1.7)
  - [ ] Do not log sensitive data (passwords, full SSNs, credit card numbers)
  - [ ] Test: Verify errors logged with correct level and format

- [ ] Task 12: Add documentation for error handling (AC: #1, #2)
  - [ ] Create `docs/api/ERROR_HANDLING.md` with:
    - List of all error codes and meanings (VALIDATION_ERROR, CUSTOMER_NOT_FOUND, etc.)
    - HTTP status code mappings
    - Example error responses for each scenario
    - traceId usage for support reference
    - Field validation error codes (NotNull, Email, Size, etc.)
  - [ ] Add @ExceptionHandler OpenAPI documentation:
    - Document error responses in Swagger
    - Include example error payloads
  - [ ] Update README or API guide

- [ ] Task 13: Integration tests for error handling (AC: #1-8)
  - [ ] Create `src/test/java/com/example/cicsgenapp/exception/GlobalExceptionHandlerTest.java`
  - [ ] Test 1: Invalid email format → 400 with VALIDATION_ERROR and field details
  - [ ] Test 2: Missing required field → 400 with VALIDATION_ERROR
  - [ ] Test 3: Email too long (> 100 chars) → 400 with Size validation error
  - [ ] Test 4: Invalid phone format → 400 with validation error
  - [ ] Test 5: Age < 18 → 400 with age validation error
  - [ ] Test 6: Duplicate email → 409 with DUPLICATE_EMAIL code
  - [ ] Test 7: Non-existent customer ID → 404 with CUSTOMER_NOT_FOUND
  - [ ] Test 8: Invalid UUID format → 400 with INVALID_FORMAT
  - [ ] Test 9: Verify error response includes traceId
  - [ ] Test 10: Verify error response includes timestamp
  - [ ] Test 11: Verify error response includes field-level details (if applicable)
  - [ ] Test 12: Test all HTTP status codes (400, 401, 403, 404, 409, 500)
  - [ ] Test 13: Verify errors are logged with correct level and format
  - [ ] Test 14: Create customers with valid/invalid data and verify validation

## Dev Notes

### Architecture Context

Story 2.7 implements a comprehensive validation and error handling framework that ensures all customer APIs return consistent, clear error responses. This is a cross-cutting concern that improves API usability and compliance.

**Key Design Patterns:**
- **Global Exception Handler**: Centralized error handling via @ControllerAdvice
- **Custom Exceptions**: Domain-specific exceptions for different error scenarios
- **Validation Framework**: Combination of Bean Validation annotations and custom validators
- **Structured Errors**: Consistent error response format with field-level details
- **Correlation IDs**: traceId included in all error responses for support reference

**Relationship to Previous Stories:**
- **Story 2.1** provides Customer entity (add validation annotations)
- **Story 2.2-2.6** use validation and error handling (built on this framework)
- **Story 1.2** establishes Spring Boot error handling infrastructure (extends this)
- **Story 1.7** provides structured JSON logging (errors logged via this framework)

### Technical Requirements

1. **Spring Framework**: @ControllerAdvice, @ExceptionHandler
2. **Bean Validation (Jakarta Validation)**: @NotNull, @Email, @Pattern, @Size, custom validators
3. **Custom Validators**: Implementation of ConstraintValidator interface
4. **Error DTOs**: Consistent error response structure
5. **Logging**: SLF4J with MDC for structured error logging
6. **Correlation IDs**: traceId propagation in error responses

### Constraints & Requirements

- **Java 17 LTS**: Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS**: Established (Story 1.2)
- **Bean Validation API**: Latest version compatible with Spring Boot 3.3
- **Consistent Status Codes**: HTTP status codes must follow REST conventions
- **No PII in Errors**: Sensitive data never logged or returned in error responses
- **Performance**: Validation should be lightweight (< 100ms)
- **Localization**: Error messages should support internationalization (future enhancement)

### Testing Standards Summary

Story 2.7 requires comprehensive testing of error handling and validation:
- **Validation Tests**: All field validators work correctly
- **Error Response Tests**: Correct format and status codes
- **Edge Case Tests**: Empty strings, null values, boundary values
- **Integration Tests**: Full API flow with validation
- **Logging Tests**: Errors logged with correct format and level
- **Correlation ID Tests**: traceId present in all error responses

Target: 90%+ test coverage for exception handler and validators

**Key Test Cases (Task 13):**
1. Invalid email: 400 with field error
2. Missing field: 400 with field error
3. Field too long: 400 with size error
4. Invalid phone: 400 with validation error
5. Age < 18: 400 with age error
6. Duplicate email: 409 conflict
7. Not found: 404 error
8. Invalid UUID: 400 error
9. Error includes traceId
10. Error includes timestamp
11. Error includes field details
12. All status codes correct
13. Errors logged correctly
14. Validation working end-to-end

### Project Structure Notes

```
src/main/java/com/example/cicsgenapp/
├── exception/
│   ├── GlobalExceptionHandler.java (NEW - @ControllerAdvice)
│   ├── CustomerNotFoundException.java (NEW - custom exception)
│   ├── CustomerAlreadyExistsException.java (NEW - custom exception)
│   └── ValidationException.java (NEW - custom exception)
├── validator/
│   ├── ValidEmailValidator.java (NEW - custom validator)
│   ├── ValidAgeValidator.java (NEW - custom validator)
│   ├── ValidPhoneFormatValidator.java (NEW - custom validator)
│   ├── ValidEmail.java (NEW - annotation)
│   ├── ValidAge.java (NEW - annotation)
│   └── ValidPhoneFormat.java (NEW - annotation)
├── dto/
│   ├── StandardErrorResponse.java (NEW - error response)
│   ├── ErrorDetails.java (NEW - error details)
│   └── FieldError.java (NEW - field error)
├── service/
│   └── CustomerService.java (MODIFIED - add validation methods)
└── util/
    └── MDCUtil.java (NEW - traceId utilities, if not exists)

docs/api/
└── ERROR_HANDLING.md (NEW - error handling documentation)

src/test/java/com/example/cicsgenapp/
├── exception/
│   └── GlobalExceptionHandlerTest.java (NEW - exception handler tests)
└── validator/
    ├── ValidEmailValidatorTest.java (NEW - email validator tests)
    ├── ValidAgeValidatorTest.java (NEW - age validator tests)
    └── ValidPhoneFormatValidatorTest.java (NEW - phone validator tests)
```

### References

- [Spring Validation Documentation](https://spring.io/guides/gs/validating-form-input/)
- [Bean Validation / Jakarta Validation](https://beanvalidation.org/)
- [Creating Custom Validators](https://www.baeldung.com/spring-custom-validation-annotation)
- [Spring @ControllerAdvice](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)
- [REST API Error Handling Best Practices](https://www.rfc-editor.org/rfc/rfc7807)
- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [Source: docs/epics.md#epic-2-customer-service-api Story 2.7]
- [Source: stories/1-2-spring-boot-starter-project-with-core-configuration.md - Error handling foundation]
- [Source: stories/1-7-structured-logging-and-observability-setup.md - Logging patterns]

## Dev Agent Record

### Context Reference

- docs/stories/2-7-input-validation-and-error-handling-framework.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 2.7 CREATED from Epic 2
- 2025-11-03: Cross-cutting framework story, used by Stories 2.2-2.6

### Completion Notes List

*To be filled by dev agent during implementation*

### File List

*To be filled by dev agent during implementation*

## Change Log

- **2025-11-03 [14:40 UTC]:** Story 2.7 DRAFTED - Input Validation and Error Handling Framework

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 2.1: Customer Domain Model and PostgreSQL Schema (drafted)
- Story 2.2-2.6: Customer CRUD APIs (drafted)

## Story Type

Framework / Infrastructure - Cross-cutting concern

## Story Points (Estimate)

13 points

## Epic

Epic 2: Customer Service API & Spring Boot Backend
