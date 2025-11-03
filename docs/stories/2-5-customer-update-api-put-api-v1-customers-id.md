# Story 2.5: Customer Update API (PUT /api/v1/customers/{id})

Status: ready-for-dev

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

- [ ] Task 1: Implement PUT endpoint in CustomerController (AC: #1, #11)
  - [ ] Add PUT `/api/v1/customers/{customerId}` endpoint with @PutMapping("/{customerId}")
  - [ ] Accept customerId as @PathVariable UUID parameter
  - [ ] Accept UpdateCustomerRequest DTO in request body
  - [ ] Add @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_AGENT', 'ADMIN')") authorization
  - [ ] Call customerService.updateCustomer(customerId, request) to update
  - [ ] Return ResponseEntity<ApiResponse<CustomerResponse>> with status 200 OK

- [ ] Task 2: Create UpdateCustomerRequest DTO (AC: #2)
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/UpdateCustomerRequest.java`
  - [ ] Fields: firstName (optional), lastName (optional), dateOfBirth (optional), email (optional), phone (optional), address (optional), city (optional), state (optional), zipCode (optional)
  - [ ] All fields optional (for partial updates)
  - [ ] Add validation annotations (@Email, @Pattern, @Size) where applicable
  - [ ] Add getter/setter methods

- [ ] Task 3: Implement update logic with partial updates (AC: #2, #3, #7)
  - [ ] In CustomerService: Implement updateCustomer(UUID customerId, UpdateCustomerRequest request) method
  - [ ] Load existing Customer entity via customerRepository.findById(customerId)
  - [ ] For each provided field in request, update entity field (null fields = no update)
  - [ ] Use reflection or explicit if-statements to detect changed fields
  - [ ] Store before/after values for audit trail
  - [ ] Save updated entity via customerRepository.save(entity)
  - [ ] Return updated customer wrapped in CustomerResponse

- [ ] Task 4: Implement email uniqueness validation (AC: #6)
  - [ ] When updating email: check if new email already exists for another customer
  - [ ] Use customerRepository.findByEmail(newEmail) excluding current customerId
  - [ ] If email exists for different customer, throw CustomerAlreadyExistsException with 409 response
  - [ ] Handle via @ControllerAdvice to return 409 CONFLICT
  - [ ] Test: Attempt to update customer email to existing email

- [ ] Task 5: Implement optimistic locking (AC: #10)
  - [ ] Add `version` field to Customer entity (Long, @Version annotation)
  - [ ] Flyway migration to add version column to CUSTOMER table
  - [ ] When updating: include if-match header with etag (version hash)
  - [ ] Before save, compare provided etag with current entity version
  - [ ] If version mismatch, throw OptimisticLockException with 409 response
  - [ ] Return error message: "Customer was modified by another user. Please refresh and try again."
  - [ ] Alternative: Use request header If-Match with entity version tag

- [ ] Task 6: Track field changes for audit trail (AC: #7, #9)
  - [ ] Create ChangeTracker class to compare before/after values
  - [ ] Store all changed fields in Map<String, Map<String, Object>> format: {fieldName: {before: value1, after: value2}, ...}
  - [ ] Example: {email: {before: "old@example.com", after: "new@example.com"}, phone: {before: "+1-555-1234", after: "+1-555-5678"}}
  - [ ] Pass change map to auditService.createAuditEntry()
  - [ ] Audit entry should log only changed fields, not unchanged ones

- [ ] Task 7: Auto-update timestamps (AC: #8)
  - [ ] Ensure Customer entity uses @EntityListeners(AuditingEntityListener.class) or similar
  - [ ] Configure @EnableJpaAuditing in Spring configuration (from Story 1.2)
  - [ ] updatedAt field should auto-update to LocalDateTime.now() on save
  - [ ] updatedBy field should auto-update from JWT token (SecurityContext)

- [ ] Task 8: Implement audit entry creation (AC: #9)
  - [ ] In CustomerService.updateCustomer(): After successful update
  - [ ] Call auditService.createAuditEntry(OPERATION.UPDATE, CUSTOMER, customerId, changes, user)
  - [ ] Pass before/after change map from Task 6
  - [ ] Include traceId in audit entry (from MDC)
  - [ ] Test: Verify audit entry captures correct before/after values

- [ ] Task 9: Implement structured JSON logging (AC: #1)
  - [ ] Log PUT /api/v1/customers/{customerId} request with request body (sanitized)
  - [ ] Log response with customerId, updated fields, timestamp
  - [ ] Use SLF4J with MDC for correlation ID (X-Trace-Id)
  - [ ] Format all logs as JSON with: timestamp, level, logger, message, traceId, userId, customerId, changedFields
  - [ ] Do not log sensitive data (full before/after values of PII fields)

- [ ] Task 10: Add OpenAPI/Swagger documentation (AC: #1, #3)
  - [ ] Add @PutMapping OpenAPI annotations:
    - @Operation(summary = "Update customer")
    - @Parameter(description = "Customer ID (UUID)")
    - @RequestBody documentation
    - @ApiResponse(responseCode = "200", description = "Customer updated")
    - @ApiResponse(responseCode = "404", description = "Customer not found")
    - @ApiResponse(responseCode = "409", description = "Email exists or conflict")
    - @ApiResponse(responseCode = "400", description = "Validation error")
  - [ ] Verify Swagger UI shows PUT /api/v1/customers/{customerId}
  - [ ] Example partial update request in Swagger documentation

- [ ] Task 11: Handle edge cases and error conditions (AC: #4, #5)
  - [ ] Customer not found: return 404 with error message
  - [ ] Empty request body (all fields null): should not update anything, return current customer with 200 OK
  - [ ] Only some fields provided: update only those fields, leave others unchanged
  - [ ] Invalid field values (email format, phone format): return 400 with field error details
  - [ ] Invalid customerId format: return 400 Bad Request

- [ ] Task 12: Integration tests for PUT endpoint (AC: #1-11)
  - [ ] Create `src/test/java/com/example/cicsgenapp/controller/CustomerControllerUpdateTest.java` or extend CustomerControllerTest.java
  - [ ] Test 1: PUT with partial update (email only) → verify only email updated, others unchanged
  - [ ] Test 2: PUT with non-existent customerId → verify 404 response
  - [ ] Test 3: PUT with invalid email format → verify 400 response with field error
  - [ ] Test 4: PUT with duplicate email → verify 409 response
  - [ ] Test 5: PUT without authorization → verify 403 response
  - [ ] Test 6: PUT with authorized user → verify 200 response with updated data
  - [ ] Test 7: Verify updatedAt timestamp is updated to current time
  - [ ] Test 8: Verify updatedBy field is updated from JWT token
  - [ ] Test 9: Verify audit entry created with correct before/after values
  - [ ] Test 10: PUT with all fields null (empty request) → verify 200 OK with unchanged customer
  - [ ] Test 11: Optimistic locking: concurrent updates → verify conflict detection
  - [ ] Test 12: Verify structured JSON logging with changed fields
  - [ ] Use @SpringBootTest with MockMvc, TestContainers for PostgreSQL

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
