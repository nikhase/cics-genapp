# Story 2.2: Customer Create API (POST /api/v1/customers)

Status: ready-for-dev

## Story

As a Customer Service Agent,
I want to create a new customer via REST API,
So that I can add new customers to the system.

## Acceptance Criteria

1. POST /api/v1/customers endpoint implemented with @PostMapping("/customers")
2. Request body schema accepts: firstName, lastName, dateOfBirth, email, phone, address, city, state, zipCode
3. Returns 201 Created with response body containing: customerId, firstName, lastName, email, phone, status, createdAt, plus metadata (timestamp, version)
4. Validation errors return 400 Bad Request with field-level errors
5. Duplicate email returns 409 Conflict with message: "Customer with this email already exists"
6. Request/response logged in structured JSON format (info level)
7. Audit entry created automatically (operation: CREATE, user from JWT token, all field values)
8. Default status set to ACTIVE
9. Requires ROLE_CUSTOMER_SERVICE_AGENT or higher authorization
10. Response time < 200ms typical

## Tasks / Subtasks

- [ ] Task 1: Create CustomerController with POST endpoint (AC: #1, #9)
  - [ ] Create `src/main/java/com/example/cicsgenapp/controller/CustomerController.java`
  - [ ] Implement POST `/api/v1/customers` endpoint with @PostMapping("/customers")
  - [ ] Add @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_AGENT', 'ADMIN')") authorization check
  - [ ] Accept CreateCustomerRequest DTO in request body
  - [ ] Inject CustomerService dependency
  - [ ] Call customerService.createCustomer(request) to delegate to service layer
  - [ ] Return ResponseEntity<ApiResponse<CustomerResponse>> with status 201 CREATED
  - [ ] Include proper HTTP status code in response

- [ ] Task 2: Create CustomerService with create business logic (AC: #2, #3, #7, #8)
  - [ ] Create `src/main/java/com/example/cicsgenapp/service/CustomerService.java`
  - [ ] Implement createCustomer(CreateCustomerRequest request) method
  - [ ] Map CreateCustomerRequest DTO to Customer entity
  - [ ] Set default status = ACTIVE (AC #8)
  - [ ] Call customerRepository.save(customer) to persist
  - [ ] Handle data persistence exceptions (catch DataIntegrityViolationException for duplicate key)
  - [ ] Create audit entry via auditService.createAuditEntry(OPERATION.CREATE, customer, user)
  - [ ] Return created Customer entity (or wrap in response DTO)
  - [ ] Return HTTP 201 with created customer object

- [ ] Task 3: Create request/response DTOs (AC: #2, #3)
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/CreateCustomerRequest.java`
    - Fields: firstName (required, 1-100 chars), lastName (required, 1-100 chars), dateOfBirth (optional, Date), email (required, email format), phone (optional, international format), address (optional), city (optional), state (optional), zipCode (optional)
    - Add validation annotations (@NotNull, @Email, @Pattern, @Size)
    - Add getter/setter methods
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/CustomerResponse.java`
    - Fields: customerId (UUID), firstName, lastName, email, phone, status, createdAt, updatedAt
    - Map from Customer entity
  - [ ] Create `src/main/java/com/example/cicsgenapp/dto/ApiResponse.java` (generic response wrapper if not exists)
    - Generic fields: data (T), metadata (Map<String,Object>)
    - Include timestamp, version in metadata

- [ ] Task 4: Implement duplicate email detection and conflict handling (AC: #5)
  - [ ] In CustomerService.createCustomer(): Add email uniqueness check before saving
  - [ ] Use customerRepository.findByEmail(email) to check for existing customer
  - [ ] If exists, throw CustomerAlreadyExistsException with message: "Customer with this email already exists"
  - [ ] In global @ControllerAdvice exception handler: Catch CustomerAlreadyExistsException
  - [ ] Return ResponseEntity with status 409 CONFLICT and error message
  - [ ] Test: Attempt to create duplicate email and verify 409 response

- [ ] Task 5: Implement request/response structured JSON logging (AC: #6)
  - [ ] Update GatewayLoggingFilter (from Story 1.4) if not already structured JSON
  - [ ] Create CustomerControllerAdvice or enhance existing @ControllerAdvice for error handling
  - [ ] Add request logging: Log POST /api/v1/customers with request body (sanitized)
  - [ ] Add response logging: Log 201 response with customer ID and creation timestamp
  - [ ] Use SLF4J with MDC for correlation ID (X-Trace-Id from Story 1.4)
  - [ ] Format all logs as JSON with: timestamp, level, logger, message, traceId, userId, customerId
  - [ ] Test: Create customer and verify JSON logs in application output

- [ ] Task 6: Implement automatic audit entry creation (AC: #7)
  - [ ] Create or enhance `src/main/java/com/example/cicsgenapp/service/AuditService.java`
  - [ ] Implement createAuditEntry(operation, entityType, entityId, changes, user) method
  - [ ] Extract userId from Spring SecurityContext (JWT token)
  - [ ] Create AuditLog entity with:
    - auditId (UUID)
    - timestamp (LocalDateTime.now())
    - userId (from JWT)
    - operation (CREATE)
    - entityType (CUSTOMER)
    - entityId (customerId)
    - changes (JSON: all customer fields and their values)
    - ipAddress (from HttpServletRequest)
    - userAgent (from request header)
  - [ ] Persist AuditLog via AuditLogRepository
  - [ ] Test: Create customer and verify audit entry is created

- [ ] Task 7: Implement authorization checks (AC: #9)
  - [ ] Ensure Spring Security is configured (from Story 1.5)
  - [ ] Add @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_AGENT', 'ADMIN')") on POST endpoint
  - [ ] Test: Attempt to create customer without CUSTOMER_SERVICE_AGENT role
  - [ ] Verify 403 Forbidden response is returned
  - [ ] Test: Create customer with CUSTOMER_SERVICE_AGENT role succeeds

- [ ] Task 8: Performance testing and optimization (AC: #10)
  - [ ] Benchmark POST /api/v1/customers response time with sample data
  - [ ] Target: < 200ms typical response time
  - [ ] Ensure database indexes from Story 2.1 are applied (email index for uniqueness check)
  - [ ] Profile with JMeter or Apache Bench: POST 1000+ requests to endpoint
  - [ ] Verify response times stay < 200ms
  - [ ] Test with various payload sizes (minimal to maximum fields)
  - [ ] Document performance characteristics in implementation notes

- [ ] Task 9: Implement error handling and validation (AC: #4)
  - [ ] In CreateCustomerRequest DTO: Add all validation annotations (@NotNull, @Email, @Pattern, @Size)
  - [ ] In CustomerController: Use @Valid on request parameter to trigger validation
  - [ ] In @ControllerAdvice: Catch MethodArgumentNotValidException
  - [ ] Extract field-level errors from BindingResult
  - [ ] Return 400 Bad Request with error details:
    ```json
    {
      "error": {
        "code": "VALIDATION_ERROR",
        "message": "Validation failed",
        "details": [
          {"field": "email", "message": "Invalid email format", "value": "not-an-email"}
        ]
      }
    }
    ```
  - [ ] Test: POST with invalid email → verify 400 response with field error details
  - [ ] Test: POST with missing firstName → verify 400 response

- [ ] Task 10: Integration tests for full API flow (AC: #1-10)
  - [ ] Create `src/test/java/com/example/cicsgenapp/controller/CustomerControllerTest.java`
  - [ ] Test 1: POST with valid data → verify 201 response with customerId
  - [ ] Test 2: POST with invalid email → verify 400 response with field error
  - [ ] Test 3: POST with duplicate email → verify 409 response with conflict message
  - [ ] Test 4: POST without authorization → verify 403 response
  - [ ] Test 5: POST with authorized user → verify 201 response
  - [ ] Test 6: Verify request/response are logged in JSON format
  - [ ] Test 7: Verify audit entry is created after successful POST
  - [ ] Test 8: Verify created customer has status=ACTIVE (default)
  - [ ] Test 9: Verify response time < 200ms
  - [ ] Test 10: Verify response includes correct metadata (timestamp, version)
  - [ ] Use @WebMvcTest or @SpringBootTest with MockMvc for testing
  - [ ] Use TestContainers for PostgreSQL in integration tests

- [ ] Task 11: Documentation and API contract (AC: #1-3)
  - [ ] Add @PostMapping OpenAPI/Swagger annotations on endpoint:
    - @Operation(summary = "Create a new customer")
    - @RequestBody documentation
    - @ApiResponse(responseCode = "201", description = "Customer created")
    - @ApiResponse(responseCode = "400", description = "Validation error")
    - @ApiResponse(responseCode = "409", description = "Email already exists")
  - [ ] Generate OpenAPI spec via springdoc-openapi (if not auto-generated)
  - [ ] Verify Swagger UI shows POST /api/v1/customers with correct schema
  - [ ] Document error codes: VALIDATION_ERROR, DUPLICATE_EMAIL, UNAUTHORIZED
  - [ ] Document example request/response in README or API guide

## Dev Notes

### Architecture Context

Story 2.2 implements the first REST API endpoint for the modernized application, replacing the legacy COBOL SSC1 transaction. This endpoint allows agents to create customer records via HTTP POST instead of 3270 terminal interface.

**Key Design Patterns:**
- **Request/Response DTOs**: CreateCustomerRequest and CustomerResponse separate API contract from entity model
- **Service Layer**: CustomerService encapsulates business logic (email uniqueness check, audit logging)
- **Authorization**: @PreAuthorize enforces role-based access control (CUSTOMER_SERVICE_AGENT or ADMIN)
- **Error Handling**: Global @ControllerAdvice handles validation errors and business exceptions
- **Audit Trail**: Automatic audit entry creation for compliance (Story 2.8 extends this)

**Relationship to Previous Stories:**
- **Story 2.1** provides Customer JPA entity and CustomerRepository (uses these for persistence)
- **Story 1.2** establishes Spring Boot REST infrastructure with error handling
- **Story 1.4** provides Spring Cloud Gateway for routing (this endpoint will be routed via gateway)
- **Story 1.5** provides JWT authentication (createdBy extracted from JWT token)
- **Story 1.7** provides structured logging patterns (follows JSON logging established)

### Technical Requirements for This Story

1. **Spring Web MVC**: @PostMapping, @RequestBody, @Valid, ResponseEntity
2. **Spring Data JPA**: CustomerRepository for database persistence
3. **Spring Security**: @PreAuthorize for authorization checks
4. **Bean Validation**: @Email, @NotNull, @Pattern annotations
5. **Error Handling**: Global @ControllerAdvice, exception mapping
6. **Audit Logging**: AuditService, AuditLog entity, structured JSON logging
7. **API Documentation**: Spring Doc OpenAPI / Swagger annotations

### Constraints & Requirements

- **Java 17 LTS**: Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS**: Established (Story 1.2)
- **PostgreSQL 15+**: Database (Story 1.3)
- **Email uniqueness**: Business rule (enforced at DB level in Story 2.1)
- **JWT Authentication**: User context (Story 1.5)
- **Response time**: < 200ms (Performance requirement AC #10)

### Testing Standards Summary

Story 2.2 requires comprehensive testing of the REST API:
- **Unit Tests**: DTOs, validation, business logic
- **Integration Tests**: Full API flow with database, authorization, error handling
- **API Contract Tests**: Request/response schema validation
- **Performance Tests**: Response time < 200ms
- **Error Path Tests**: Validation errors, duplicate email, unauthorized access

Target: 85%+ test coverage for controller and service classes

**Key Test Cases (Task 10):**
1. Successful creation: 201 with customerId
2. Invalid email: 400 with field error
3. Duplicate email: 409 with conflict message
4. Missing authorization: 403 Forbidden
5. Authorized user: 201 success
6. JSON logging verification
7. Audit entry creation
8. Default status = ACTIVE
9. Response time < 200ms
10. Response metadata present

### Project Structure Notes

**Alignment with unified-project-structure.md** (from Story 1.1):
```
src/main/java/com/example/cicsgenapp/
├── controller/
│   └── CustomerController.java (NEW - REST endpoint)
├── service/
│   ├── CustomerService.java (NEW - business logic)
│   └── AuditService.java (NEW - audit trail)
├── dto/
│   ├── CreateCustomerRequest.java (NEW - API request)
│   ├── CustomerResponse.java (NEW - API response)
│   └── ApiResponse.java (NEW or MODIFIED - generic wrapper)
└── entity/
    └── Customer.java (from Story 2.1)

src/main/resources/
└── db/migration/
    ├── V1__initial_schema.sql (Story 1.3)
    └── V2__create_customer_table.sql (Story 2.1)

src/test/java/com/example/cicsgenapp/
├── controller/
│   └── CustomerControllerTest.java (NEW - API tests)
├── service/
│   └── CustomerServiceTest.java (NEW - service logic tests)
└── integration/
    └── CustomerApiIntegrationTest.java (NEW - full flow tests)
```

**Maven module**: `genapp-backend` (established Story 1.1)

### References

- [Spring Web MVC Documentation](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Security - Authorization](https://docs.spring.io/spring-security/reference/servlet/authorization/index.html)
- [Spring Data JPA - Repositories](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Bean Validation (Jakarta Validation)](https://beanvalidation.org/)
- [Spring Doc OpenAPI - Swagger Integration](https://springdoc.org/)
- [REST API Best Practices - HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [Source: docs/epics.md#epic-2-customer-service-api--spring-boot-backend Story 2.2]
- [Source: docs/architecture.md - Architectural Layers for API design]
- [Source: stories/2-1-customer-domain-model-and-postgresql-schema.md - Customer entity definition]
- [Source: stories/1-2-spring-boot-starter-project-with-core-configuration.md - Error handling framework]
- [Source: stories/1-5-oidc-authentication-with-zitadel-integration.md - JWT authentication]

## Dev Agent Record

### Context Reference

- docs/stories/2-2-customer-create-api-post-api-v1-customers.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 2.2 CREATED from Epic 2 (second story in customer service epic)
- 2025-11-03: Previous story (2-1) is drafted, not completed
- 2025-11-03: Extracted requirements from epics.md Story 2.2

### Completion Notes List

*To be filled by dev agent during implementation*

### File List

*To be filled by dev agent during implementation*

## Change Log

- **2025-11-03 [14:15 UTC]:** Story 2.2 DRAFTED - Customer Create API (POST /api/v1/customers)

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.5: OIDC Authentication with Zitadel Integration (✓ COMPLETED)
- Story 2.1: Customer Domain Model and PostgreSQL Schema (drafted, will be completed before this story starts)

## Story Type

REST API Implementation - CRUD Operation

## Story Points (Estimate)

13 points

## Epic

Epic 2: Customer Service API & Spring Boot Backend
