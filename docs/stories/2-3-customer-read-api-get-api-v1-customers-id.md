# Story 2.3: Customer Read API (GET /api/v1/customers/{id})

Status: drafted

## Story

As a Customer Service Agent,
I want to retrieve customer details by ID,
So that I can view customer information.

## Acceptance Criteria

1. GET /api/v1/customers/{customerId} endpoint implemented
2. Returns 200 OK with customer object including all fields: customerId, firstName, lastName, dateOfBirth, email, phone, address, city, state, zipCode, status, createdAt, updatedAt, createdBy, updatedBy
3. Customer not found returns 404 Not Found with message: "Customer {customerId} not found"
4. Response includes all customer fields including timestamps and audit info
5. Audit entry created (READ operations logged for compliance)
6. Performance: response < 100ms for typical query
7. Invalid UUID format returns 400 Bad Request
8. Can include related resources link: `"_links": {"policies": "/api/v1/policies?customerId=..."}`

## Tasks / Subtasks

- [ ] Task 1: Implement GET endpoint in CustomerController (AC: #1, #2, #7)
  - [ ] Add GET `/api/v1/customers/{customerId}` endpoint with @GetMapping("/{customerId}")
  - [ ] Accept customerId as @PathVariable UUID parameter
  - [ ] Call customerService.getCustomer(customerId) to retrieve data
  - [ ] Return ResponseEntity<ApiResponse<CustomerResponse>> with status 200 OK
  - [ ] Handle invalid UUID format: throw IllegalArgumentException with 400 response
  - [ ] No authorization required (read-only operation available to authenticated users)

- [ ] Task 2: Implement retrieval logic in CustomerService (AC: #2, #4)
  - [ ] Implement getCustomer(UUID customerId) method
  - [ ] Call customerRepository.findById(customerId)
  - [ ] Map Customer entity to CustomerResponse DTO with all fields
  - [ ] Include timestamps (createdAt, updatedAt) and audit fields (createdBy, updatedBy)
  - [ ] Return CustomerResponse wrapped in ApiResponse

- [ ] Task 3: Implement 404 Not Found error handling (AC: #3)
  - [ ] In CustomerService.getCustomer(): If customer not found
  - [ ] Throw CustomerNotFoundException with message: "Customer {customerId} not found"
  - [ ] In @ControllerAdvice: Catch CustomerNotFoundException
  - [ ] Return ResponseEntity with status 404 NOT FOUND
  - [ ] Include error message in response body

- [ ] Task 4: Implement audit logging for READ operations (AC: #5)
  - [ ] In CustomerService.getCustomer(): After successful retrieval
  - [ ] Call auditService.createAuditEntry(OPERATION.READ, CUSTOMER, customerId, null, user)
  - [ ] Include traceId in audit entry (from MDC)
  - [ ] Log read access for compliance purposes (no sensitive data in logs)
  - [ ] Test: Verify audit entry is created after successful GET

- [ ] Task 5: Optimize query performance (AC: #6)
  - [ ] Ensure database indexes from Story 2.1 are in place (primary key, email, phone)
  - [ ] Profile GET endpoint response time with JMeter or Apache Bench
  - [ ] Target: < 100ms response time for typical query
  - [ ] No N+1 query issues: Use JPA @EntityGraph or eager loading if needed
  - [ ] Test: Benchmark 100+ requests to endpoint
  - [ ] Verify response times stay < 100ms

- [ ] Task 6: Add HATEOAS links for related resources (AC: #8 - optional)
  - [ ] Add `_links` section to response with link to related policies
  - [ ] Example: `"_links": {"policies": "/api/v1/policies?customerId={customerId}"}`
  - [ ] Allows clients to discover related APIs (Story 2.4)
  - [ ] Makes API more discoverable and RESTful

- [ ] Task 7: Implement structured JSON logging (AC: #2)
  - [ ] Log GET /api/v1/customers/{customerId} request (info level)
  - [ ] Log response with customer ID and retrieval timestamp
  - [ ] Use SLF4J with MDC for correlation ID (X-Trace-Id)
  - [ ] Format all logs as JSON with: timestamp, level, logger, message, traceId, userId, customerId
  - [ ] Do not log sensitive data (PII)

- [ ] Task 8: Add OpenAPI/Swagger documentation (AC: #1, #2)
  - [ ] Add @GetMapping OpenAPI annotations:
    - @Operation(summary = "Get customer by ID")
    - @Parameter(description = "Customer ID (UUID)")
    - @ApiResponse(responseCode = "200", description = "Customer found")
    - @ApiResponse(responseCode = "404", description = "Customer not found")
    - @ApiResponse(responseCode = "400", description = "Invalid customer ID format")
  - [ ] Verify Swagger UI shows GET /api/v1/customers/{customerId}
  - [ ] Example customer response in Swagger documentation

- [ ] Task 9: Integration tests for GET endpoint (AC: #1-8)
  - [ ] Create `src/test/java/com/example/cicsgenapp/controller/CustomerControllerGetTest.java` or add to CustomerControllerTest.java
  - [ ] Test 1: GET with valid customerId → verify 200 response with customer object
  - [ ] Test 2: GET with non-existent customerId → verify 404 response with error message
  - [ ] Test 3: GET with invalid UUID format → verify 400 response
  - [ ] Test 4: Verify response includes all customer fields (firstName, lastName, email, etc.)
  - [ ] Test 5: Verify response includes timestamps and audit fields
  - [ ] Test 6: Verify audit entry is created after successful GET
  - [ ] Test 7: Verify response time < 100ms
  - [ ] Test 8: Verify _links section present (if HATEOAS implemented)
  - [ ] Test 9: Verify structured JSON logging
  - [ ] Use @SpringBootTest with MockMvc or TestContainers for PostgreSQL

## Dev Notes

### Architecture Context

Story 2.3 implements the READ operation for customer management, completing the basic CRUD set (Create in 2.2, Read in 2.3). This is a simple retrieval endpoint that fetches customer data by ID.

**Key Design Patterns:**
- **Service Layer**: CustomerService encapsulates retrieval logic and audit logging
- **Error Handling**: Global @ControllerAdvice handles 404 and 400 errors
- **Audit Trail**: READ operations logged for compliance (important for sensitive data)
- **Performance**: Optimized query with proper indexing (< 100ms target)
- **HATEOAS** (Optional): Links to related resources (policies)

**Relationship to Previous Stories:**
- **Story 2.1** provides Customer JPA entity and CustomerRepository
- **Story 2.2** establishes CustomerController and CustomerService patterns (reuse these)
- **Story 1.2** establishes Spring Boot REST infrastructure with error handling
- **Story 1.7** provides structured logging patterns (follow JSON logging established)

### Technical Requirements for This Story

1. **Spring Web MVC**: @GetMapping, @PathVariable, ResponseEntity
2. **Spring Data JPA**: CustomerRepository for database retrieval
3. **Error Handling**: Global @ControllerAdvice, exception mapping (404, 400)
4. **Audit Logging**: AuditService for READ operations
5. **API Documentation**: Spring Doc OpenAPI / Swagger annotations
6. **Performance Optimization**: Query optimization, database indexes

### Constraints & Requirements

- **Java 17 LTS**: Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS**: Established (Story 1.2)
- **PostgreSQL 15+**: Database (Story 1.3)
- **Response time**: < 100ms (Performance requirement AC #6)
- **Audit Trail**: All read operations logged for compliance
- **Error Handling**: Clear error messages for invalid ID format and not found cases

### Testing Standards Summary

Story 2.3 requires comprehensive testing of the READ API:
- **Unit Tests**: Service layer retrieval logic
- **Integration Tests**: Full API flow with database, error handling
- **Error Path Tests**: 404 not found, 400 invalid UUID format
- **Performance Tests**: Response time < 100ms
- **Audit Tests**: Verify READ operations are logged

Target: 85%+ test coverage for controller and service classes

**Key Test Cases (Task 9):**
1. Successful retrieval: 200 with customer object
2. Not found: 404 with error message
3. Invalid UUID: 400 Bad Request
4. Response includes all fields
5. Timestamps and audit fields present
6. Audit entry created
7. Response time < 100ms
8. HATEOAS links present (if implemented)
9. Structured JSON logging
10. Proper correlation IDs

### Project Structure Notes

**Alignment with unified-project-structure.md**:
```
src/main/java/com/example/cicsgenapp/
├── controller/
│   └── CustomerController.java (MODIFIED - add GET endpoint)
├── service/
│   ├── CustomerService.java (MODIFIED - add getCustomer method)
│   └── AuditService.java (from Story 2.2)
├── dto/
│   └── CustomerResponse.java (from Story 2.2, reuse)
├── entity/
│   └── Customer.java (from Story 2.1)
└── exception/
    └── CustomerNotFoundException.java (NEW - for 404 errors)

src/test/java/com/example/cicsgenapp/
├── controller/
│   └── CustomerControllerGetTest.java (NEW - GET endpoint tests)
└── service/
    └── CustomerServiceTest.java (MODIFIED - add getCustomer tests)
```

**Reuses from Story 2.2:**
- CustomerController (extends with GET endpoint)
- CustomerService (adds getCustomer method)
- CustomerResponse DTO (no changes needed)
- ApiResponse wrapper (reuse)

### References

- [Spring Web MVC - Handler Mapping](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JPA - Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods)
- [REST API - GET Method Best Practices](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/GET)
- [HATEOAS / HAL Format](https://en.wikipedia.org/wiki/Hypertext_Application_Language)
- [Spring Doc OpenAPI - GET Endpoint Documentation](https://springdoc.org/)
- [Source: docs/epics.md#epic-2-customer-service-api Story 2.3]
- [Source: stories/2-2-customer-create-api-post-api-v1-customers.md - Pattern reference]
- [Source: stories/2-1-customer-domain-model-and-postgresql-schema.md - Customer entity]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 2.3 CREATED from Epic 2 (third story in customer service epic)
- 2025-11-03: Previous story (2-2) is drafted
- 2025-11-03: Extracted requirements from epics.md Story 2.3
- 2025-11-03: Reusing CustomerController, CustomerService, and CustomerResponse patterns from Story 2.2

### Completion Notes List

*To be filled by dev agent during implementation*

### File List

*To be filled by dev agent during implementation*

## Change Log

- **2025-11-03 [14:20 UTC]:** Story 2.3 DRAFTED - Customer Read API (GET /api/v1/customers/{id})

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.5: OIDC Authentication with Zitadel Integration (✓ COMPLETED)
- Story 2.1: Customer Domain Model and PostgreSQL Schema (drafted, will be completed before this story starts)
- Story 2.2: Customer Create API (drafted, provides CustomerController and CustomerService patterns)

## Story Type

REST API Implementation - READ Operation (CRUD)

## Story Points (Estimate)

8 points

## Epic

Epic 2: Customer Service API & Spring Boot Backend
