# Story 2.6: Customer Soft-Delete API (DELETE /api/v1/customers/{id})

Status: ready-for-dev

## Story

As a Compliance Officer,
I want to soft-delete customers (mark inactive, preserve history),
So that customer records are retained for audit purposes.

## Acceptance Criteria

1. DELETE /api/v1/customers/{customerId} endpoint implemented
2. Sets customer status to INACTIVE (soft delete, not hard delete)
3. Returns 200 OK or 204 No Content
4. Customer not found returns 404 Not Found
5. Attempting to delete already-inactive customer returns 200 OK (idempotent)
6. Audit entry created with deletion reason (optional request parameter)
7. Soft-deleted customer still queryable via GET (visible to compliance roles, hidden from normal search)
8. Associated policies linked to deleted customer handled per business rule: policies remain ACTIVE (customer status is separate)
9. Policies can only be linked to ACTIVE customers for new creations
10. Hard delete not possible via API (compliance requirement)
11. Requires ROLE_COMPLIANCE_OFFICER or ROLE_ADMIN authorization

## Tasks / Subtasks

- [ ] Task 1: Implement DELETE endpoint in CustomerController (AC: #1, #11)
  - [ ] Add DELETE `/api/v1/customers/{customerId}` endpoint with @DeleteMapping("/{customerId}")
  - [ ] Accept customerId as @PathVariable UUID parameter
  - [ ] Accept optional request body with deletion reason field
  - [ ] Add @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'ADMIN')") authorization
  - [ ] Call customerService.deleteCustomer(customerId, reason) to soft-delete
  - [ ] Return ResponseEntity with status 200 OK (or 204 No Content per REST conventions)

- [ ] Task 2: Create DeleteCustomerRequest DTO (AC: #6)
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/DeleteCustomerRequest.java`
  - [ ] Fields: reason (String, optional - max 500 chars for deletion reason)
  - [ ] Example: "Customer requested deletion", "Inactive customer cleanup", "Data correction"
  - [ ] Add getter/setter methods

- [ ] Task 3: Implement soft-delete logic (AC: #2, #3, #5)
  - [ ] In CustomerService: Implement deleteCustomer(UUID customerId, String reason) method
  - [ ] Load existing Customer entity via customerRepository.findById(customerId)
  - [ ] If customer not found, throw CustomerNotFoundException with 404 response (AC #4)
  - [ ] Set customer.status = CustomerStatus.INACTIVE
  - [ ] Set customer.deletedAt = LocalDateTime.now() (add field to entity if needed)
  - [ ] Set customer.deletionReason = reason (optional, for audit purposes)
  - [ ] Save updated entity via customerRepository.save(entity)
  - [ ] If customer already INACTIVE, still return 200 OK (idempotent, AC #5)
  - [ ] Do NOT delete any data from database (soft delete only)

- [ ] Task 4: Implement audit entry creation with deletion reason (AC: #6)
  - [ ] After soft-delete: call auditService.createAuditEntry(OPERATION.DELETE, CUSTOMER, customerId, reason, user)
  - [ ] Audit entry should include:
    - operation: DELETE
    - entityId: customerId
    - reason: deletion reason from request
    - timestamp: LocalDateTime.now()
    - userId: from JWT token
    - ipAddress: from request
  - [ ] Test: Verify audit entry created with deletion reason recorded
  - [ ] Audit entry immutable: no updates after creation

- [ ] Task 5: Handle associated policies (AC: #8, #9)
  - [ ] When deleting customer: do NOT delete or modify related policies
  - [ ] Policies remain ACTIVE (their status is independent of customer status)
  - [ ] Document in code: "Customer deletion does not affect associated policies"
  - [ ] Add validation on policy creation: if customerId provided, verify customer is ACTIVE
  - [ ] This is a safeguard for Story 2.2: prevent linking policies to inactive customers
  - [ ] Consider adding to Story 2.2 post-implementation if needed

- [ ] Task 6: Implement search filter for soft-deleted customers (AC: #7)
  - [ ] Modify CustomerRepository or search service (from Story 2.4)
  - [ ] Add option to include/exclude inactive customers in search results
  - [ ] Default behavior: exclude INACTIVE customers from normal search (Story 2.4)
  - [ ] Add flag to include soft-deleted: `includeDeleted=true` query parameter in Story 2.4
  - [ ] Only COMPLIANCE_OFFICER or ADMIN roles can see deleted customers
  - [ ] Implement role-based filtering: if not COMPLIANCE_OFFICER/ADMIN, filter out INACTIVE customers
  - [ ] Test: Verify deleted customer not in normal search, visible to compliance roles with flag

- [ ] Task 7: Implement structured JSON logging (AC: #1)
  - [ ] Log DELETE /api/v1/customers/{customerId} request with deletion reason
  - [ ] Log response with customerId, deletion timestamp, status change
  - [ ] Use SLF4J with MDC for correlation ID (X-Trace-Id)
  - [ ] Format all logs as JSON with: timestamp, level, logger, message, traceId, userId, customerId, reason
  - [ ] Do not log sensitive customer data

- [ ] Task 8: Add OpenAPI/Swagger documentation (AC: #1, #3)
  - [ ] Add @DeleteMapping OpenAPI annotations:
    - @Operation(summary = "Soft-delete customer (mark as inactive)")
    - @Parameter(description = "Customer ID (UUID)")
    - @RequestBody documentation (optional deletion reason)
    - @ApiResponse(responseCode = "200", description = "Customer deleted (soft)")
    - @ApiResponse(responseCode = "204", description = "No content")
    - @ApiResponse(responseCode = "404", description = "Customer not found")
  - [ ] Document in API spec: "Soft-delete only - data is retained for audit purposes"
  - [ ] Verify Swagger UI shows DELETE /api/v1/customers/{customerId}

- [ ] Task 9: Handle edge cases and error conditions (AC: #4, #5, #10)
  - [ ] Customer not found: return 404 with error message
  - [ ] Already inactive customer: return 200 OK (idempotent)
  - [ ] Invalid customerId format: return 400 Bad Request
  - [ ] Hard delete attempted: not available via API (no such endpoint exists)
  - [ ] Test compliance: verify no SQL DELETE ever executes on CUSTOMER table via this endpoint

- [ ] Task 10: Integration tests for DELETE endpoint (AC: #1-11)
  - [ ] Create `src/test/java/com/example/cicsgenapp/controller/CustomerControllerDeleteTest.java` or extend CustomerControllerTest.java
  - [ ] Test 1: DELETE with valid customerId → verify 200 OK (or 204), customer marked INACTIVE
  - [ ] Test 2: DELETE with non-existent customerId → verify 404 response
  - [ ] Test 3: DELETE with invalid customerId format → verify 400 Bad Request
  - [ ] Test 4: DELETE without authorization → verify 403 Forbidden
  - [ ] Test 5: DELETE with COMPLIANCE_OFFICER role → verify 200 OK
  - [ ] Test 6: DELETE with ADMIN role → verify 200 OK
  - [ ] Test 7: Verify audit entry created with deletion reason
  - [ ] Test 8: DELETE already-inactive customer → verify 200 OK (idempotent)
  - [ ] Test 9: Verify customer still queryable via GET (after soft-delete)
  - [ ] Test 10: Verify soft-deleted customer hidden from normal search (Story 2.4)
  - [ ] Test 11: Verify soft-deleted customer visible to COMPLIANCE_OFFICER with flag
  - [ ] Test 12: Verify associated policies remain ACTIVE after customer deletion
  - [ ] Test 13: Verify structured JSON logging includes deletion reason
  - [ ] Use @SpringBootTest with MockMvc, TestContainers for PostgreSQL

## Dev Notes

### Architecture Context

Story 2.6 implements the DELETE operation for customer management, but as a soft-delete to preserve audit history and meet compliance requirements. Hard deletes are never allowed via API.

**Key Design Patterns:**
- **Soft Delete**: Mark inactive instead of hard delete
- **Audit Trail**: Deletion reason recorded for compliance
- **Data Preservation**: All customer data retained (history, relationships)
- **Role-Based Visibility**: Soft-deleted customers hidden from normal queries, visible to compliance roles
- **Idempotency**: Deleting already-deleted customer succeeds (idempotent)
- **Service Layer**: CustomerService encapsulates soft-delete logic

**Relationship to Previous Stories:**
- **Story 2.1** provides Customer JPA entity with status field
- **Story 2.2** establishes CustomerController and CustomerService patterns (reuse these)
- **Story 2.3** provides getCustomer (still works on soft-deleted customers)
- **Story 2.4** provides search (needs modification to filter soft-deleted)
- **Story 2.5** provides update patterns

### Technical Requirements

1. **Spring Web MVC**: @DeleteMapping, ResponseEntity
2. **Spring Data JPA**: CustomerRepository save for status update
3. **Error Handling**: Global @ControllerAdvice for 404 and 400 errors
4. **Audit Logging**: AuditService with deletion reason tracking
5. **Role-Based Authorization**: @PreAuthorize for COMPLIANCE_OFFICER/ADMIN
6. **API Documentation**: Spring Doc OpenAPI / Swagger annotations

### Constraints & Requirements

- **Java 17 LTS**: Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS**: Established (Story 1.2)
- **PostgreSQL 15+**: Database (Story 1.3)
- **Soft Delete Only**: Hard delete never allowed via API (compliance requirement)
- **Idempotent**: Multiple deletes of same customer succeed
- **Data Preservation**: All history and relationships preserved
- **Authorization**: COMPLIANCE_OFFICER or ADMIN role required
- **Audit Trail**: Deletion reason recorded and immutable

### Testing Standards Summary

Story 2.6 requires comprehensive testing of the DELETE API:
- **Soft Delete Tests**: Verify status changed to INACTIVE, no data deleted
- **Authorization Tests**: COMPLIANCE_OFFICER and ADMIN access, others denied
- **Error Tests**: 404 not found, 400 invalid format
- **Idempotency Tests**: Multiple deletes succeed
- **Integration Tests**: Verify related data preserved (policies)
- **Audit Tests**: Deletion reason recorded and immutable

Target: 85%+ test coverage

**Key Test Cases (Task 10):**
1. Successful soft-delete: 200 OK
2. Not found: 404 error
3. Invalid format: 400 error
4. Unauthorized: 403 error
5. Authorized: 200 success
6. Audit entry with reason
7. Idempotent: delete again succeeds
8. Still queryable via GET
9. Hidden from normal search
10. Visible to compliance roles
11. Policies unaffected
12. Structured JSON logging

### Project Structure Notes

```
src/main/java/com/example/cicsgenapp/
├── controller/
│   └── CustomerController.java (MODIFIED - add DELETE endpoint)
├── service/
│   ├── CustomerService.java (MODIFIED - add deleteCustomer method)
│   └── AuditService.java (from Story 2.2)
├── dto/
│   └── DeleteCustomerRequest.java (NEW - optional deletion reason)
├── entity/
│   └── Customer.java (MODIFIED - add deletedAt, deletionReason fields)
└── exception/
    └── CustomerNotFoundException.java (from Story 2.3)

src/main/resources/db/migration/
└── V4__add_soft_delete_fields_to_customer.sql (NEW - add deletedAt, deletionReason columns)

src/test/java/com/example/cicsgenapp/
└── controller/
    └── CustomerControllerDeleteTest.java (NEW - delete endpoint tests)
```

### References

- [REST API Best Practices - DELETE Method](https://restfulapi.net/http-methods-rest-api/)
- [Soft Delete Patterns in Databases](https://en.wikipedia.org/wiki/Soft_delete)
- [Audit Trail Best Practices](https://en.wikipedia.org/wiki/Audit_trail)
- [Spring Security - Authorization](https://docs.spring.io/spring-security/reference/servlet/authorization/index.html)
- [Source: docs/epics.md#epic-2-customer-service-api Story 2.6]
- [Source: stories/2-2-customer-create-api-post-api-v1-customers.md - Pattern reference]
- [Source: stories/2-1-customer-domain-model-and-postgresql-schema.md - Customer entity]

## Dev Agent Record

### Context Reference

- docs/stories/2-6-customer-soft-delete-api-delete-api-v1-customers-id.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 2.6 CREATED from Epic 2
- 2025-11-03: Building on patterns from Stories 2.2-2.5

### Completion Notes List

*To be filled by dev agent during implementation*

### File List

*To be filled by dev agent during implementation*

## Change Log

- **2025-11-03 [14:35 UTC]:** Story 2.6 DRAFTED - Customer Soft-Delete API (DELETE /api/v1/customers/{id})

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.5: OIDC Authentication with Zitadel Integration (✓ COMPLETED)
- Story 2.1: Customer Domain Model and PostgreSQL Schema (drafted)
- Story 2.2: Customer Create API (drafted)
- Story 2.3: Customer Read API (drafted)
- Story 2.4: Customer Search/List API (drafted)
- Story 2.5: Customer Update API (drafted)

## Story Type

REST API Implementation - DELETE Operation (CRUD - Soft Delete)

## Story Points (Estimate)

10 points

## Epic

Epic 2: Customer Service API & Spring Boot Backend
