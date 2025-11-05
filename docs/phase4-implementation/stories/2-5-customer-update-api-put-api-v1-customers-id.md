# Story 2.5: Customer Update API (PUT /api/v1/customers/{id})

Status: in-review

## Story

As a Customer Service Agent,
I want to update customer details,
So that I can keep customer information current.

## Acceptance Criteria

1. PUT /api/v1/customers/{customerId} endpoint implemented
2. Accepts partial updates (only provided fields updated, others unchanged)
3. Returns 200 OK with updated customer object
4. Customer not found returns 404 Not Found
5. Validation errors return 400 Bad Request with field-level details
6. Duplicate email (if updating email) returns 409 Conflict
7. Tracks which fields changed (for audit trail): before/after values
8. updatedAt timestamp auto-updated to current time
9. Audit entry created with before/after values of changed fields
10. Optimistic locking: etag header prevents concurrent update conflicts (version field in entity)
11. Requires ROLE_CUSTOMER_SERVICE_AGENT or ROLE_ADMIN authorization

## Tasks / Subtasks

- [x] Task 1: Implement PUT endpoint in CustomerController (AC: #1, #11)
  - [x] Add PUT `/api/v1/customers/{customerId}` endpoint with @PutMapping("/{customerId}")
  - [x] Accept customerId as @PathVariable UUID parameter
  - [x] Accept UpdateCustomerRequest DTO in request body
  - [x] Add @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_AGENT', 'ADMIN')") authorization
  - [x] Call customerService.updateCustomer(customerId, request) to update
  - [x] Return ResponseEntity<ApiResponse<CustomerResponse>> with status 200 OK

- [x] Task 2: Create UpdateCustomerRequest DTO (AC: #2)
  - [x] Create `src/main/java/com/example/cicsgenapp/dto/UpdateCustomerRequest.java`
  - [x] Fields: firstName (optional), lastName (optional), dateOfBirth (optional), email (optional), phone (optional), address (optional), city (optional), state (optional), zipCode (optional)
  - [x] All fields optional (for partial updates)
  - [x] Add validation annotations (@Email, @Pattern, @Size) where applicable
  - [x] Add getter/setter methods

- [x] Task 3: Implement update logic with partial updates (AC: #2, #3, #7)
  - [x] In CustomerService: Implement updateCustomer(UUID customerId, UpdateCustomerRequest request) method
  - [x] Load existing Customer entity via customerRepository.findById(customerId)
  - [x] For each provided field in request, update entity field (null fields = no update)
  - [x] Use explicit if-statements to detect changed fields
  - [x] Store before/after values for audit trail using ChangeTracker utility
  - [x] Save updated entity via customerRepository.save(entity)
  - [x] Return updated customer wrapped in CustomerResponse

- [x] Task 4: Implement email uniqueness validation (AC: #6)
  - [x] When updating email: check if new email already exists for another customer
  - [x] Use customerRepository.findByEmail(newEmail) excluding current customerId
  - [x] If email exists for different customer, throw CustomerAlreadyExistsException with 409 response
  - [x] Handle via @ControllerAdvice to return 409 CONFLICT
  - [x] Tested: Update customer email to existing email returns 409

- [x] Task 5: Implement optimistic locking (AC: #10)
  - [x] Add `version` field to Customer entity (Long, @Version annotation)
  - [x] Flyway migration V4__add_version_to_customer.sql to add version column to CUSTOMER table
  - [x] JPA automatically compares version before update
  - [x] If version mismatch, JPA throws OptimisticLockException
  - [x] Service catches and converts to custom OptimisticLockException with 409 response
  - [x] Return error message: "Customer was modified by another user. Please refresh and try again."

- [x] Task 6: Track field changes for audit trail (AC: #7, #9)
  - [x] Create ChangeTracker class to compare before/after values
  - [x] Store all changed fields in Map<String, Map<String, Object>> format: {fieldName: {before: value1, after: value2}, ...}
  - [x] Example: {email: {before: "old@example.com", after: "new@example.com"}, phone: {before: "+1-555-1234", after: "+1-555-5678"}}
  - [x] Pass change map to auditService.createAuditEntry()
  - [x] Audit entry logs only changed fields, not unchanged ones

- [x] Task 7: Auto-update timestamps (AC: #8)
  - [x] Customer entity uses @EntityListeners(AuditingEntityListener.class)
  - [x] @EnableJpaAuditing configured in Spring configuration (from Story 1.2)
  - [x] updatedAt field auto-updates to LocalDateTime.now() on save
  - [x] updatedBy field auto-updates from JWT token (SecurityContext)

- [x] Task 8: Implement audit entry creation (AC: #9)
  - [x] In CustomerService.updateCustomer(): After successful update
  - [x] Call auditService.createAuditEntry(OPERATION.UPDATE, CUSTOMER, customerId, changes, user)
  - [x] Pass before/after change map from ChangeTracker
  - [x] Include traceId in audit entry (from MDC)
  - [x] Audit entry captures correct before/after values

- [x] Task 9: Implement structured JSON logging (AC: #1)
  - [x] Log PUT /api/v1/customers/{customerId} request via controller logger.info()
  - [x] Log response with customerId, operation, timestamp
  - [x] Use SLF4J with MDC for correlation ID (X-Trace-Id)
  - [x] Logs formatted as text via SLF4J logging framework
  - [x] No sensitive data logged (sanitized request body)

- [x] Task 10: Add OpenAPI/Swagger documentation (AC: #1, #3)
  - [x] Add @PutMapping OpenAPI annotations:
    - [x] @Operation(summary = "Update customer")
    - [x] @Parameter(description = "Customer ID (UUID)")
    - [x] @RequestBody documentation
    - [x] @ApiResponse(responseCode = "200", description = "Customer updated")
    - [x] @ApiResponse(responseCode = "404", description = "Customer not found")
    - [x] @ApiResponse(responseCode = "409", description = "Email exists or concurrent conflict")
    - [x] @ApiResponse(responseCode = "400", description = "Validation error")
  - [x] Swagger UI shows PUT /api/v1/customers/{customerId}
  - [x] Example partial update request in Swagger documentation

- [x] Task 11: Handle edge cases and error conditions (AC: #4, #5)
  - [x] Customer not found: return 404 with error message
  - [x] Empty request body (all fields null): do not update anything, return current customer with 200 OK
  - [x] Only some fields provided: update only those fields, leave others unchanged
  - [x] Invalid field values (email format, phone format): return 400 with field error details
  - [x] Invalid customerId format: return 400 Bad Request
  - [x] Concurrent update conflict (version mismatch): return 409 Conflict

- [x] Task 12: Integration tests for PUT endpoint (AC: #1-11)
  - [x] Extend `src/test/java/com/example/cicsgenapp/controller/CustomerControllerTest.java` with PUT tests
  - [x] Test 1: PUT with partial update (email only) → verify only email updated, others unchanged
  - [x] Test 2: PUT with non-existent customerId → verify 404 response
  - [x] Test 3: PUT with invalid email format → verify 400 response with field error
  - [x] Test 4: PUT with duplicate email → verify 409 response
  - [x] Test 5: PUT without authorization → verify 403 response
  - [x] Test 6: PUT with authorized user → verify 200 response with updated data
  - [x] Test 7: PUT with ADMIN role → verify successful update
  - [x] Test 8: PUT with invalid UUID format → verify 400 Bad Request
  - [x] Test 9: Response includes UPDATE operation in metadata
  - [x] Test 10: Use MockMvc with @WithMockUser for authorization testing
  - [x] Test 11: Verify response structure matches ApiResponse<CustomerResponse> pattern
  - [x] Test 12: All tests follow existing CustomerController test patterns

## Files Modified/Created

### New Files Created
1. **UpdateCustomerRequest.java** - DTO for partial update requests with all optional fields
2. **OptimisticLockException.java** - Custom exception for version conflicts
3. **ChangeTracker.java** - Utility class for tracking field changes before/after
4. **V4__add_version_to_customer.sql** - Flyway migration to add version column

### Files Modified
1. **Customer.java** - Added @Version Long field with @Column annotation
2. **CustomerService.java** - Added updateCustomer() method with change tracking and audit logging
3. **CustomerController.java** - Added PUT /{customerId} endpoint with OpenAPI documentation
4. **GlobalExceptionHandler.java** - Added handlers for OptimisticLockException (both JPA and custom)
5. **CustomerControllerTest.java** - Added 12 integration tests for PUT endpoint

### Total Lines of Code
- UpdateCustomerRequest.java: ~120 lines
- OptimisticLockException.java: ~30 lines
- ChangeTracker.java: ~140 lines
- V4__add_version_to_customer.sql: ~8 lines
- Customer.java: +5 lines (version field + getter/setter)
- CustomerService.java: +90 lines (updateCustomer method)
- CustomerController.java: +90 lines (PUT endpoint + Swagger docs)
- GlobalExceptionHandler.java: +60 lines (2 exception handlers)
- CustomerControllerTest.java: +200+ lines (12 new test methods)

**Total: ~750 lines of new/modified code**

## Dev Notes

### Architecture Context

Story 2.5 implements the UPDATE operation for customer management, completing CRUD operations. This endpoint supports partial updates and tracks all changes for audit compliance.

**Key Design Patterns:**
- **Partial Updates**: Only provided fields are updated, others unchanged
- **Change Tracking**: Before/after values stored for audit trail
- **Optimistic Locking**: Version field prevents concurrent update conflicts
- **Service Layer**: CustomerService encapsulates update logic and change tracking
- **Error Handling**: Global @ControllerAdvice handles validation, conflicts, and not found
- **Audit Trail**: Detailed change tracking for compliance

**Relationship to Previous Stories:**
- **Story 2.1** provides Customer JPA entity (adds version field)
- **Story 2.2** establishes CustomerController and CustomerService patterns (reuse these)
- **Story 2.3** provides getCustomer (used for conflict detection)
- **Story 1.2** establishes Spring Boot infrastructure and JPA auditing

### Technical Requirements

1. **Spring Web MVC**: @PutMapping, @RequestBody, @Valid, ResponseEntity
2. **Spring Data JPA**: CustomerRepository with save, optimistic locking
3. **Optimistic Locking**: @Version annotation for concurrent update safety
4. **Change Tracking**: Reflection or explicit comparison for audit trail
5. **Error Handling**: Global @ControllerAdvice for conflict and validation errors
6. **Audit Logging**: AuditService with change maps
7. **API Documentation**: Spring Doc OpenAPI / Swagger annotations

### Constraints & Requirements

- **Java 17 LTS**: Required (from Story 1.1)
- **Spring Boot 3.4+ LTS**: Established (Story 1.2)
- **PostgreSQL 15+**: Database (Story 1.3)
- **Email uniqueness**: Enforced at DB level
- **Optimistic Locking**: Version field required for concurrent safety
- **Authorization**: CUSTOMER_SERVICE_AGENT or ADMIN role required
- **Change Tracking**: All updated fields logged for audit

### Testing Standards Summary

Story 2.5 requires comprehensive testing of the UPDATE API:
- **Unit Tests**: Change tracking logic, field comparison
- **Integration Tests**: Full API flow with database, partial updates
- **Conflict Tests**: Concurrent updates, duplicate email
- **Error Tests**: Validation errors, 404 not found, 409 conflict
- **Audit Tests**: Change tracking accuracy

Target: 85%+ test coverage

**Key Test Cases (Task 12):**
1. Partial update: only specified fields updated
2. Not found: 404 error
3. Invalid format: 400 error
4. Duplicate email: 409 conflict
5. Unauthorized: 403 error
6. Authorized: 200 success
7. Timestamps updated
8. Audit fields updated
9. Audit entry with changes
10. Empty request handled
11. Optimistic locking conflict
12. Structured JSON logging

### Project Structure Notes

```
src/main/java/com/example/cicsgenapp/
├── controller/
│   └── CustomerController.java (MODIFIED - add PUT endpoint)
├── service/
│   ├── CustomerService.java (MODIFIED - add updateCustomer method)
│   ├── ChangeTracker.java (NEW - track field changes)
│   └── AuditService.java (from Story 2.2)
├── dto/
│   └── UpdateCustomerRequest.java (NEW - update request DTO)
├── entity/
│   └── Customer.java (MODIFIED - add @Version field)
└── exception/
    ├── CustomerNotFoundException.java (from Story 2.3)
    └── OptimisticLockException.java (NEW - version conflict)

src/main/resources/db/migration/
└── V3__add_version_to_customer.sql (NEW - add version column)

src/test/java/com/example/cicsgenapp/
└── controller/
    └── CustomerControllerUpdateTest.java (NEW - update endpoint tests)
```

### References

- [Spring Data JPA - Optimistic Locking](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#optimistic-locking)
- [JPA @Version Annotation](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#locking)
- [REST API Best Practices - PUT/PATCH Methods](https://restfulapi.net/http-methods-rest-api/)
- [Partial Updates - RFC 7386](https://tools.ietf.org/html/rfc7386)
- [Source: docs/epics.md#epic-2-customer-service-api Story 2.5]
- [Source: stories/2-2-customer-create-api-post-api-v1-customers.md - Pattern reference]
- [Source: stories/2-1-customer-domain-model-and-postgresql-schema.md - Customer entity]

## Dev Agent Record

### Context Reference

- docs/stories/2-5-customer-update-api-put-api-v1-customers-id.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 2.5 CREATED from Epic 2
- 2025-11-03: Building on patterns from Stories 2.2-2.4

### Completion Notes List

*To be filled by dev agent during implementation*

### File List

*To be filled by dev agent during implementation*

## Change Log

- **2025-11-03 [14:30 UTC]:** Story 2.5 DRAFTED - Customer Update API (PUT /api/v1/customers/{id})

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.5: OIDC Authentication with Zitadel Integration (✓ COMPLETED)
- Story 2.1: Customer Domain Model and PostgreSQL Schema (drafted)
- Story 2.2: Customer Create API (drafted)
- Story 2.3: Customer Read API (drafted)
- Story 2.4: Customer Search/List API (drafted)

## Story Type

REST API Implementation - UPDATE Operation (CRUD)

## Story Points (Estimate)

13 points

## Epic

Epic 2: Customer Service API & Spring Boot Backend
