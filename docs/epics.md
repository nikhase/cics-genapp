# CICS GenApp Cloud Modernization - Epic Breakdown

**Author:** Niklas
**Date:** October 31, 2025
**Project Level:** Level 3
**Target Scale:** Enterprise Platform with Phased Delivery

---

## Overview

This document provides the detailed epic breakdown for CICS GenApp Cloud Modernization, expanding on the high-level epic list in the [PRD](./PRD.md).

Each epic includes:

- Expanded goal and value proposition
- Complete story breakdown with user stories
- Acceptance criteria for each story
- Story sequencing and dependencies

**Epic Sequencing Principles:**

- Epic 1 establishes foundational infrastructure and initial functionality
- Subsequent epics build progressively, each delivering significant end-to-end value
- Stories within epics are vertically sliced and sequentially ordered
- No forward dependencies - each story builds only on previous work

---

## EPIC 1: Cloud Foundation & API Gateway Layer

**Expanded Goal:**

Establish the complete cloud-native foundation and deployment infrastructure needed to run the modernized application. This epic creates the Spring Boot starter project, configures API Gateway for strangler pattern routing, establishes database connectivity, implements feature toggle framework, and sets up CI/CD pipeline. Upon completion, the technical foundation is ready for business logic implementation in subsequent epics, and the team can deploy services to production environment.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 1-6

---

### Story 1.1: Project Repository Setup and Developer Environment

**As a** Development Team Lead,
**I want** to establish a standardized GitHub repository structure with build tooling configuration,
**So that** all developers have a consistent starting point and can quickly run local builds.

**Acceptance Criteria:**

1. GitHub repository created with clear folder structure (src/, tests/, docs/, config/)
2. Maven pom.xml configured with Spring Boot 3.x, all required dependencies (Spring Web, Data JPA, Security, Actuator, etc.)
3. .gitignore configured for Maven/IntelliJ/VS Code artifacts
4. README.md with developer setup instructions (Java 17+, Maven 3.8+, Git clone, mvn clean install)
5. Local development can run: `mvn clean install && mvn spring-boot:run`
6. Git workflow documentation (branch naming, commit conventions, PR process)
7. Java code style configuration (checkstyle, spotbugs) integrated into build

**Prerequisites:** None

---

### Story 1.2: Spring Boot Starter Project with Core Configuration

**As a** Backend Developer,
**I want** a working Spring Boot application with core Spring configuration (profiles, properties, bean definitions),
**So that** I can add business logic without worrying about framework setup.

**Acceptance Criteria:**

1. Spring Boot application boots successfully with embedded Tomcat on port 8080
2. Application properties file configured for dev/test/prod profiles (application-dev.yml, application-test.yml, application-prod.yml)
3. Spring Security configured with basic authentication/authorization framework (ready for JWT implementation)
4. Spring Data JPA configured with Hibernate dialect
5. Actuator endpoints exposed at /actuator (health, metrics, etc.) for monitoring
6. Error handling middleware (@ControllerAdvice, custom exception handlers) in place
7. Structured JSON logging configured using Logback/SLF4J
8. Application can be packaged as Docker image (Dockerfile, build script)

**Prerequisites:** Story 1.1

---

### Story 1.3: Database Connectivity and Schema Management

**As a** Database Administrator,
**I want** the Spring Boot application to connect to Db2 with connection pooling and schema management,
**So that** the application can reliably store and retrieve data.

**Acceptance Criteria:**

1. Db2 JDBC driver configured in Spring Boot (version 11.5+)
2. Connection pool configured (HikariCP: 10 min, 20 max connections)
3. JPA/Hibernate configured with Db2 dialect
4. Flyway or Liquibase migration tool integrated for schema versioning
5. Initial schema migration script created (tables structure empty, ready for domain models)
6. Environment-specific Db2 connection strings configured (dev uses test DB, prod uses production DB)
7. Connection health check working via Spring Actuator (/actuator/health/db)
8. Database connection tested with successful query execution

**Prerequisites:** Story 1.2

---

### Story 1.4: API Gateway and Strangler Pattern Routing

**As a** Architect,
**I want** to establish an API Gateway that routes requests to either new Spring Boot services or legacy COBOL system,
**So that** we can gradually migrate traffic without clients knowing about the backend change.

**Acceptance Criteria:**

1. API Gateway chosen and configured (Spring Cloud Gateway, Kong, or AWS API Gateway)
2. Routes configured:
   - `/api/v1/customers/*` → routes to Spring Boot (when feature toggle enabled) OR legacy COBOL
   - `/api/v1/policies/*` → routes to Spring Boot (when feature toggle enabled) OR legacy COBOL
   - `/api/v1/audit/*` → routes to Spring Boot
3. Request/response logging implemented at gateway level
4. Circuit breaker configured to fail over to legacy if new service is down
5. Timeout handling (5s timeout for downstream services)
6. CORS headers configured for React frontend
7. Request tracing ID added to all requests for distributed tracing
8. Gateway can be deployed independently and scales horizontally

**Prerequisites:** Story 1.2

---

### Story 1.5: Feature Toggle Framework Implementation

**As a** DevOps Engineer,
**I want** a feature toggle system that controls traffic routing and feature visibility without code deployment,
**So that** we can safely canary traffic to new services and quickly rollback if issues occur.

**Acceptance Criteria:**

1. Feature toggle framework integrated (LaunchDarkly, Unleash, or custom Redis-based implementation)
2. Toggles defined for:
   - `customer-api-enabled` (true = route to Spring Boot, false = route to legacy)
   - `policy-api-enabled` (true = route to Spring Boot, false = route to legacy)
   - `parallel-run-enabled` (true = enable dual-write validation)
3. Toggle can be updated at runtime without redeployment
4. Toggle state queryable via API (`GET /api/v1/toggles/{name}`)
5. Toggle state persisted in database (survives service restart)
6. Audit log created when toggle state changes (who, when, from/to)
7. Default toggle values configured in each environment (dev, test, prod)
8. Performance: toggle check < 1ms (cached locally with periodic refresh)

**Prerequisites:** Story 1.2

---

### Story 1.6: Structured Logging and Observability Setup

**As a** Operations Engineer,
**I want** centralized, structured logging with correlation IDs for request tracing,
**So that** I can quickly diagnose issues and understand request flow across services.

**Acceptance Criteria:**

1. Structured JSON logging configured (all log lines output as JSON with consistent schema)
2. Log levels properly used (ERROR for failures, WARN for degradation, INFO for key events, DEBUG for details)
3. Correlation ID (request ID) added to all logs within a request context
4. Sensitive data masked in logs (no passwords, PII, API keys)
5. Log aggregation configured (logs sent to centralized store: ELK, Splunk, CloudWatch, or similar)
6. Sample log lines documented in README (show example ERROR, WARN, INFO, DEBUG)
7. Metrics exposed (request count, latency, error rate) via Micrometer
8. Logging doesn't degrade performance (async logging, ring buffer)

**Prerequisites:** Story 1.2

---

### Story 1.7: Containerization and Docker Setup

**As a** DevOps Engineer,
**I want** the Spring Boot application containerized in Docker for cloud deployment,
**So that** we can deploy consistently across dev, test, and production environments.

**Acceptance Criteria:**

1. Dockerfile created with multi-stage build (compile stage, runtime stage)
2. Base image chosen (openjdk:17-slim or similar, minimal)
3. Application JAR included in container
4. Docker image size < 500MB
5. Container starts Spring Boot application on port 8080
6. Health check configured in Dockerfile (curl /actuator/health)
7. Non-root user configured (security best practice)
8. Environment variables can be passed to container for configuration
9. docker build and docker run tested locally
10. Docker image pushed to container registry (ECR, DockerHub, or private registry)

**Prerequisites:** Story 1.2

---

### Story 1.8: CI/CD Pipeline Setup

**As a** Development Team,
**I want** an automated CI/CD pipeline that builds, tests, and deploys on every commit,
**So that** we can ship changes quickly with confidence.

**Acceptance Criteria:**

1. GitHub Actions workflow (or Jenkins/GitLab CI) configured
2. Pipeline triggers on push to main branch and PRs
3. Pipeline stages:
   - **Build:** mvn clean install (compiles, runs unit tests)
   - **Test:** mvn test (unit tests, integration tests)
   - **Package:** Create Docker image, run container security scan
   - **Deploy (auto):** Push Docker image to registry, deploy to test environment
   - **Deploy (manual approval):** Deploy to production environment on approval
4. Build artifacts (JAR, Docker image) versioned with git commit hash
5. Pipeline notifications sent to Slack/Teams on success/failure
6. Pipeline execution time < 10 minutes (fast feedback)
7. Failed pipelines block merges to main branch
8. Deployment can be rolled back via pipeline (redeploy previous image version)

**Prerequisites:** Stories 1.2, 1.7

---

### Story 1.9: Authentication Framework and JWT Implementation

**As a** Security Engineer,
**I want** to implement JWT-based authentication for API access,
**So that** users and services can securely identify themselves and be authorized.

**Acceptance Criteria:**

1. Spring Security configured with JWT authentication
2. Login endpoint implemented (POST /api/v1/auth/login) - accepts username/password
3. Returns JWT token with:
   - User ID in subject claim
   - Role(s) in custom claim
   - 1-hour expiration
   - Refresh token for obtaining new access token
4. JWT token validated on all API requests (custom filter in Spring Security chain)
5. Invalid/expired tokens return 401 Unauthorized with clear error message
6. Role-based access control configured (@PreAuthorize annotations)
7. Roles supported: AGENT, ADMIN, COMPLIANCE_OFFICER
8. Password hashing configured (bcrypt, min 10 rounds)
9. No passwords stored/logged in plain text

**Prerequisites:** Story 1.2

---

### Story 1.10: First Service Deployment to Cloud Environment

**As a** DevOps Engineer,
**I want** to deploy the Spring Boot application to a cloud environment,
**So that** we have a running service accessible for testing and parallel run validation.

**Acceptance Criteria:**

1. Cloud infrastructure provisioned (AWS ECS/EKS, Azure Container Instances, GCP Cloud Run, or on-prem K8s)
2. Service deployed and running on cloud platform
3. Service accessible via public HTTPS URL with valid certificate
4. Health check passing (/actuator/health returns 200)
5. Logs flowing to centralized logging system
6. Metrics visible in cloud monitoring dashboard
7. Database connection from cloud environment successful
8. Load balancer configured for service (auto-scaling, health-based routing)
9. Rollback procedure documented and tested
10. Team has access to cloud console for monitoring and troubleshooting

**Prerequisites:** Stories 1.2, 1.7, 1.8

---

## EPIC 2: Customer Service API & Spring Boot Layer

**Expanded Goal:**

Implement the complete customer management functionality in Spring Boot, exposing REST APIs that replace the legacy COBOL SSC1 transaction. This epic creates the customer domain model, implements all CRUD operations, adds validation and error handling, implements audit logging, and provides comprehensive API documentation. Upon completion, agents can begin parallel testing of customer operations (new Spring Boot system vs legacy COBOL), validating data consistency and building confidence in the new architecture.

**Story Count:** 8 stories | **Estimated Duration:** Weeks 7-10

---

### Story 2.1: Customer Domain Model and Database Schema

**As a** Backend Developer,
**I want** to define the Customer domain model and create the database schema,
**So that** I have a foundation for implementing customer CRUD operations.

**Acceptance Criteria:**

1. Customer JPA entity created with fields:
   - customerId (UUID, primary key)
   - firstName, lastName (string, required)
   - dateOfBirth (date)
   - email (string, required, unique)
   - phone (string)
   - address, city, state, zipCode (string)
   - createdAt, updatedAt (timestamp, auto-managed)
   - status (enum: ACTIVE, INACTIVE)
2. Database table `CUSTOMER` created with proper indexes (email, phone, status)
3. Flyway/Liquibase migration script created and tested
4. JPA repository interface created (Spring Data JPA)
5. Repository includes custom finder methods: findByEmail(), findByPhone(), findByLastNameContaining()
6. Entity validation annotations applied (@NotNull, @Email, @Pattern for phone, etc.)
7. Audit columns (createdAt, updatedAt) auto-managed by JPA @EntityListeners
8. Schema tested with sample data insert/select

**Prerequisites:** Story 1.3

---

### Story 2.2: Customer Create API (POST /api/v1/customers)

**As a** Customer Service Agent,
**I want** to create a new customer via REST API,
**So that** I can add new customers to the system.

**Acceptance Criteria:**

1. POST /api/v1/customers endpoint implemented
2. Request body schema:
   ```json
   {
     "firstName": "Jane",
     "lastName": "Smith",
     "dateOfBirth": "1985-03-15",
     "email": "jane.smith@example.com",
     "phone": "+1-555-0123",
     "address": "123 Main St",
     "city": "Portland",
     "state": "OR",
     "zipCode": "97201"
   }
   ```
3. Returns 201 Created with response body:
   ```json
   {
     "customerId": "550e8400-e29b-41d4-a716-446655440000",
     "firstName": "Jane",
     "lastName": "Smith",
     ...
     "createdAt": "2025-10-31T10:00:00Z"
   }
   ```
4. Validation errors return 400 Bad Request with detailed error message
5. Duplicate email returns 409 Conflict with clear message
6. Request/response logged in structured format
7. Audit entry created (API call, user, timestamp, changes)
8. Default status set to ACTIVE

**Prerequisites:** Stories 1.2, 2.1

---

### Story 2.3: Customer Read API (GET /api/v1/customers/{id})

**As a** Customer Service Agent,
**I want** to retrieve customer details by ID,
**So that** I can view customer information.

**Acceptance Criteria:**

1. GET /api/v1/customers/{customerId} endpoint implemented
2. Returns 200 OK with customer object
3. Customer not found returns 404 Not Found with clear message
4. Response includes all customer fields including createdAt, updatedAt
5. Audit entry created (read operations logged for compliance)
6. Performance: response < 100ms for typical query
7. Invalid UUID format returns 400 Bad Request
8. Response includes links to related resources (e.g., policies for this customer)

**Prerequisites:** Stories 1.2, 2.1

---

### Story 2.4: Customer Search/List API (GET /api/v1/customers?query=...&status=...&limit=...&offset=...)

**As a** Customer Service Agent,
**I want** to search for customers by name, email, or phone number with pagination,
**So that** I can quickly find customers in the system.

**Acceptance Criteria:**

1. GET /api/v1/customers endpoint with query parameters:
   - `query` (optional): searches firstName, lastName, email, phone (case-insensitive partial match)
   - `status` (optional): filters by status (ACTIVE, INACTIVE)
   - `limit` (optional, default 50, max 100): page size
   - `offset` (optional, default 0): pagination offset
2. Returns 200 OK with response body:
   ```json
   {
     "data": [
       { "customerId": "...", "firstName": "...", ... },
       ...
     ],
     "pagination": {
       "limit": 50,
       "offset": 0,
       "total": 237,
       "hasMore": true
     }
   }
   ```
3. Empty results return 200 OK with empty data array
4. Performance: response < 500ms for large datasets (1M+ customers)
5. Database query uses indexed columns (optimized)
6. Results sorted by lastName, firstName
7. Audit entry created for search operations (for compliance)

**Prerequisites:** Stories 1.2, 2.1

---

### Story 2.5: Customer Update API (PUT /api/v1/customers/{id})

**As a** Customer Service Agent,
**I want** to update customer details,
**So that** I can keep customer information current.

**Acceptance Criteria:**

1. PUT /api/v1/customers/{customerId} endpoint implemented
2. Accepts partial updates (only provided fields updated, others unchanged)
3. Request body example:
   ```json
   {
     "phone": "+1-555-0199",
     "address": "456 Oak Ave"
   }
   ```
4. Returns 200 OK with updated customer object
5. Customer not found returns 404 Not Found
6. Validation errors return 400 Bad Request
7. Duplicate email update returns 409 Conflict
8. Tracks which fields changed (for audit trail)
9. updatedAt timestamp auto-updated
10. Audit entry created with before/after values of changed fields
11. Optimistic locking: etag or version field prevents concurrent update conflicts

**Prerequisites:** Stories 1.2, 2.1

---

### Story 2.6: Customer Soft-Delete API (DELETE /api/v1/customers/{id})

**As a** Compliance Officer,
**I want** to soft-delete customers (mark inactive, preserve history),
**So that** customer records are retained for audit purposes.

**Acceptance Criteria:**

1. DELETE /api/v1/customers/{customerId} endpoint implemented
2. Sets customer status to INACTIVE (soft delete, not hard delete)
3. Returns 200 OK or 204 No Content
4. Customer not found returns 404 Not Found
5. Attempting to delete already-inactive customer returns 200 OK (idempotent)
6. Audit entry created with delete reason (optional request parameter)
7. Associated policies linked to deleted customer are handled:
   - Option 1: Soft-delete policies too
   - Option 2: Keep policies linked but customer marked inactive
   - (Clarify with team which approach)
8. Hard delete not possible via API (compliance requirement)

**Prerequisites:** Stories 1.2, 2.1

---

### Story 2.7: Input Validation and Error Handling Framework

**As a** Backend Developer,
**I want** consistent error handling and validation across all customer APIs,
**So that** clients receive clear, actionable error messages.

**Acceptance Criteria:**

1. Validation errors return structured response:
   ```json
   {
     "error": {
       "code": "VALIDATION_ERROR",
       "message": "Validation failed",
       "details": [
         {
           "field": "email",
           "message": "Invalid email format",
           "value": "not-an-email"
         },
         {
           "field": "phone",
           "message": "Phone must be 10+ digits",
           "value": "555"
         }
       ]
     }
   }
   ```
2. Field-level validation:
   - firstName/lastName: required, 1-100 characters
   - email: required, valid email format, unique
   - phone: optional, valid phone format (regex)
   - dateOfBirth: optional, valid date, age > 18
   - zipCode: optional, valid format (5 digits)
3. Business logic validation:
   - Cannot create customer with duplicate email
   - Cannot update to inactive status if customer has active policies (or warn)
4. HTTP status codes used correctly:
   - 200 OK: success
   - 201 Created: resource created
   - 400 Bad Request: validation error
   - 401 Unauthorized: not authenticated
   - 403 Forbidden: not authorized
   - 404 Not Found: resource not found
   - 409 Conflict: duplicate key
   - 500 Internal Server Error: unexpected error
5. Error response includes traceId for support (correlation ID)
6. Validation annotations (@NotNull, @Email, @Pattern) used in entity
7. Custom validators for complex rules (phone format, age calculation)

**Prerequisites:** Story 2.1

---

### Story 2.8: API Documentation and Audit Logging

**As a** Frontend Developer,
**I want** comprehensive OpenAPI documentation and audit logging for all customer APIs,
**So that** I know how to call the APIs and compliance has a record of all operations.

**Acceptance Criteria:**

1. OpenAPI 3.0 specification generated (springdoc-openapi library)
2. Swagger UI available at /api/docs (interactive API explorer)
3. Documentation includes:
   - All endpoints (POST, GET, PUT, DELETE)
   - Request/response schemas
   - Status codes and error responses
   - Required headers (Authorization)
   - Example requests/responses
   - Rate limiting info
4. Audit logging implemented for all customer operations:
   - Operation type (CREATE, READ, UPDATE, DELETE, SEARCH)
   - User ID (from JWT token)
   - Timestamp
   - Customer ID (if applicable)
   - Changes made (before/after values for UPDATE)
   - IP address
   - User agent
5. Audit entries stored in database (table: AUDIT_LOG)
6. Audit entries immutable (no delete/update, only insert)
7. Audit entries not returned to clients (internal only)
8. Audit query API (admin only): GET /api/v1/audit?entity=CUSTOMER&limit=100

**Prerequisites:** Stories 2.2-2.6

---

## EPIC 3: React Frontend - Customer & Policy Management

**Expanded Goal:**

Build the responsive React single-page application that replaces the 3270 terminal interface. This epic creates the dashboard, customer management screens (search, detail, create), policy management screens, implements the Clarity Enterprise Design System, integrates with Spring Boot APIs, and enables agents to begin using the modern web UI. Upon completion, agents can perform customer and policy operations via web UI while simultaneously validating against legacy COBOL system.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 11-14

---

### Story 3.1: React Project Setup with Routing and Material Design 3

**As a** Frontend Developer,
**I want** to set up a React SPA with routing, MUI (Material Design 3), and project structure,
**So that** I have a solid foundation for building UI components.

**Acceptance Criteria:**

1. React 18+ project created (Vite or Create React App)
2. TypeScript configured for type safety
3. React Router v6 configured for client-side routing
4. MUI (Material-UI) v5 installed with Clarity Enterprise Design System theme customization
5. Project structure:
   - src/components/ (reusable UI components)
   - src/pages/ (page-level components)
   - src/services/ (API client logic)
   - src/hooks/ (custom React hooks)
   - src/context/ (React context for state)
   - src/types/ (TypeScript interfaces)
6. ESLint and Prettier configured for code style
7. Public folder with favicon, index.html, robots.txt
8. Environment variables configured (.env.dev, .env.prod)
9. Build process tested (npm run build, build size optimized)
10. Dev server runs on http://localhost:3000

**Prerequisites:** Story 1.2

---

### Story 3.2: Login Page and Authentication Context

**As a** User,
**I want** to log in with username and password,
**So that** I can access the system securely.

**Acceptance Criteria:**

1. Login page created at route /login
2. Form fields: username, password, remember-me checkbox
3. Form validation:
   - Username/email required
   - Password required
   - Password > 8 characters
4. Submit button disables during request (loading state)
5. Error messages displayed on login failure (invalid credentials, server error)
6. On successful login:
   - JWT token received from API
   - Token stored in secure session storage (not localStorage for security)
   - User redirected to /dashboard
   - User context updated with user ID, name, role
7. Logout button clears token and redirects to /login
8. Protected routes redirect to /login if no valid token
9. Remember-me checkbox (optional): stores email in localStorage for convenience
10. Password reset link (UI only, functionality in future epic)

**Prerequisites:** Stories 1.2, 1.9, 3.1

---

### Story 3.3: Dashboard Page with Navigation and Quick Actions

**As a** Customer Service Agent,
**I want** to see a dashboard with overview of workload and quick actions,
**So that** I can quickly navigate to common tasks.

**Acceptance Criteria:**

1. Dashboard page created at route /dashboard
2. Header section:
   - App logo/title
   - User greeting ("Welcome, John")
   - User profile dropdown (settings, logout)
   - Time-of-day greeting
3. Sidebar navigation with menu items:
   - Dashboard (home icon)
   - Customers (people icon)
   - Policies (document icon)
   - Audit Log (clock icon) - visible to compliance officers only
   - Reports (chart icon)
   - Admin (settings icon) - visible to admins only
4. Main content area with quick actions:
   - Card: "New Customer" (button → create customer)
   - Card: "New Policy" (button → create policy)
   - Card: "Search Customer" (search box)
   - Card: "Search Policy" (search box)
5. Metrics/statistics (optional):
   - Total customers count
   - Total policies count
   - Recent activity
6. Responsive design: sidebar collapses on mobile (hamburger menu)
7. Active menu item highlighted
8. Keyboard shortcut hints (Ctrl+K opens search)

**Prerequisites:** Stories 1.2, 3.1, 3.2

---

### Story 3.4: Customer Search and List Page

**As a** Customer Service Agent,
**I want** to search for customers and see results in a list,
**So that** I can quickly find customers.

**Acceptance Criteria:**

1. Page created at route /customers/search (or /customers with search params)
2. Search form with fields:
   - Query input (searches name, email, phone)
   - Status filter dropdown (All / Active / Inactive)
   - Search button
   - Clear button (resets form)
3. Pressing Enter in search box submits search
4. Results displayed in data table:
   - Columns: ID, Name, Email, Phone, Status, Actions
   - Status shown with badge (green for Active, gray for Inactive)
   - Sortable columns (click header to sort name/email/id)
   - Pagination controls (prev/next buttons, page info "Page 1 of 5")
   - Rows per page selector (25, 50, 100)
5. Actions column with buttons:
   - View button → navigates to customer detail page
   - Edit button → opens customer detail page in edit mode
   - More actions menu (delete, etc.)
6. Empty state message if no results
7. Loading spinner while fetching
8. Error message if search fails
9. URL reflects search state (sharable URL: /customers/search?query=smith&status=ACTIVE)
10. Performance: < 2s load time for typical searches

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.4 (API)

---

### Story 3.5: Customer Detail and Edit Page

**As a** Customer Service Agent,
**I want** to view and edit customer details,
**So that** I can manage customer information.

**Acceptance Criteria:**

1. Page created at route /customers/:id
2. Page shows customer details in read-only mode initially:
   - Name (first/last)
   - Date of birth
   - Email
   - Phone
   - Address (street, city, state, zip)
   - Status
   - Created/updated timestamps
3. Edit button allows switching to edit mode
4. Edit mode shows form fields with current values
5. Form validation (real-time feedback):
   - Email valid format
   - Phone valid format
   - Name required, 1-100 chars
6. Save button submits changes
7. Cancel button discards changes and returns to read-only
8. Success message on save ("Customer updated successfully")
9. Error message on failure with details
10. Not found error (404) if customer doesn't exist
11. Optimistic locking: show conflict error if customer was updated by another user
12. Loading spinner while fetching/saving
13. Related policies listed (read-only section showing policies for this customer)

**Prerequisites:** Stories 1.2, 3.1, 3.3, Stories 2.3, 2.5 (APIs)

---

### Story 3.6: Customer Create Page with Form Wizard

**As a** Customer Service Agent,
**I want** to create a new customer using a guided form,
**So that** I can add customers with minimal errors.

**Acceptance Criteria:**

1. Page created at route /customers/create
2. Multi-step wizard (optional, or single form - clarify preference):
   - **Step 1:** Basic info (first name, last name, date of birth)
   - **Step 2:** Contact (email, phone)
   - **Step 3:** Address (street, city, state, zip)
   - **Step 4:** Review and confirm
3. Form fields:
   - firstName (text, required, 1-100 chars)
   - lastName (text, required, 1-100 chars)
   - dateOfBirth (date picker, optional, age > 18)
   - email (email input, required, unique)
   - phone (tel input, optional, format validation)
   - address (text, optional)
   - city (text, optional)
   - state (dropdown, optional)
   - zipCode (text, optional, 5 digits)
4. Real-time validation feedback (field-level error messages)
5. Submit button disabled if validation errors
6. Step navigation (Next/Previous buttons in wizard)
7. Progress indicator showing current step
8. Review step shows all entered data
9. Confirm button creates customer via API
10. Success message with customer ID and option to create another or view customer
11. Error handling (duplicate email, server error, etc.)

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.2 (API)

---

### Story 3.7: Policy Search and List Page

**As a** Customer Service Agent,
**I want** to search for policies and see results in a list,
**So that** I can find specific policies.

**Acceptance Criteria:**

1. Page created at route /policies/search
2. Search form with fields:
   - Query input (searches policy number, customer name)
   - Policy type filter (All / Motor / Endowment / House / Commercial)
   - Status filter (All / Active / Renewed / Lapsed)
   - Search button, Clear button
3. Results displayed in data table:
   - Columns: Policy ID, Customer Name, Type, Status, Start Date, Actions
   - Status shown with badges (green/orange/gray)
   - Type shown with icon (car for Motor, etc.)
   - Sortable columns
   - Pagination controls
4. Actions column:
   - View button → policy detail page
   - Edit button → policy detail page in edit mode
5. Empty state if no results
6. Loading spinner
7. Error message on failure
8. Performance: < 2s load time

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.6 (API for policy search, to be implemented in Epic 5)

---

### Story 3.8: Policy Detail and Edit Page

**As a** Customer Service Agent,
**I want** to view and edit policy details,
**So that** I can manage policy information.

**Acceptance Criteria:**

1. Page created at route /policies/:id
2. Displays policy details:
   - Policy number
   - Linked customer (clickable link to customer detail)
   - Policy type (Motor/Endowment/House/Commercial)
   - Status (Active/Renewed/Lapsed)
   - Start date, end date
   - Type-specific attributes (engine type for Motor, sum assured for Endowment, etc.)
3. Edit mode available:
   - Edit button switches to edit mode
   - Form fields for editable fields
   - Real-time validation
4. Status change dropdown:
   - Options: Active, Renewed, Lapsed
   - Validation: cannot lapse policy with current claims (or business rule)
5. Save button submits changes
6. Cancel button discards changes
7. Success/error messages
8. Not found error if policy doesn't exist
9. Loading spinner
10. Related transactions/history (read-only) if applicable

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.7 (API for policy detail, to be implemented in Epic 5)

---

### Story 3.9: Form Validation and Error Handling UI Components

**As a** Frontend Developer,
**I want** reusable form validation components and error handling UI,
**So that** forms provide consistent user experience across all pages.

**Acceptance Criteria:**

1. TextInput component with:
   - Real-time validation feedback (error message below field)
   - Visual error indicator (red border)
   - Disabled state
   - Required indicator (*)
   - Optional helper text
2. Select/dropdown component with same validation/error features
3. DatePicker component with:
   - Calendar UI
   - Validation (date format, age calculation)
   - Disabled dates handling
4. EmailInput component with:
   - Email validation
   - Async validation (check for duplicate via API)
5. PhoneInput component with:
   - Phone format validation
   - Format mask (user types, auto-formatted)
6. FormError component for form-level errors
7. SuccessAlert component for success messages
8. LoadingSpinner component with backdrop option
9. ConfirmDialog component for destructive actions (delete)
10. All components follow Material Design 3 design system

**Prerequisites:** Stories 1.2, 3.1

---

### Story 3.10: API Integration and Client Service

**As a** Frontend Developer,
**I want** a centralized API client service that handles authentication, error handling, and request/response,
**So that** components can easily call APIs without repetitive boilerplate.

**Acceptance Criteria:**

1. ApiClient service created with methods:
   - get(url, options)
   - post(url, body, options)
   - put(url, body, options)
   - delete(url, options)
2. Automatically attaches JWT token to Authorization header
3. Handles response status codes:
   - 2xx: success (resolve promise)
   - 4xx: client error (throw error with details)
   - 5xx: server error (throw error with message)
4. Implements retry logic for transient failures (max 3 retries with exponential backoff)
5. Handles 401 Unauthorized (token expired):
   - Attempts token refresh
   - If refresh fails, redirects to login
6. Logs all API calls in development mode
7. Includes correlation ID in all requests
8. Timeout handling (15s default timeout)
9. CustomerService wrapper methods:
   - createCustomer(data)
   - getCustomer(id)
   - searchCustomers(query, filters)
   - updateCustomer(id, data)
   - deleteCustomer(id)
10. PolicyService wrapper methods (for policies API)
11. Usage example in components:
    ```javascript
    const customers = await CustomerService.searchCustomers(query);
    ```

**Prerequisites:** Stories 1.2, 3.1, 3.2, Story 2.1 (APIs)

---

## EPIC 4: Parallel Run Validation Framework

**Expanded Goal:**

Implement the critical infrastructure for safe, gradual traffic cutover from legacy COBOL to Spring Boot. This epic creates the dual-write pattern (write to both systems simultaneously), implements data comparison/validation logic, builds the validation dashboard for monitoring match rates, implements circuit breaker patterns for failover, and creates feature toggle management UI. Upon completion, agents can run 50-100+ parallel operations validating that new system data matches legacy system, providing the objective metrics needed to confidently route production traffic to new services.

**Story Count:** 5 stories | **Estimated Duration:** Weeks 15-18

---

### Story 4.1: Dual-Write Pattern Implementation

**As a** Architect,
**I want** to implement dual-write logic that writes to Spring Boot first, then legacy COBOL,
**So that** data remains synchronized between new and legacy systems during transition.

**Acceptance Criteria:**

1. Dual-write wrapper service created:
   - Accepts customer/policy mutation (create, update, delete)
   - Writes to Spring Boot service (primary)
   - If successful, calls legacy COBOL service (secondary)
   - Returns success only if primary succeeds (secondary failure is non-fatal but logged)
2. Write sequence:
   - **Write to Spring Boot:** POST/PUT/DELETE to /api/v1/customers or /api/v1/policies
   - **Wait for confirmation:** 201 Created or 200 OK
   - **Write to COBOL:** Call COBOL via HTTP bridge (wrapper service)
   - **Log discrepancies:** If COBOL write fails, log and alert but don't fail user request
3. Dual-write only enabled when feature toggle `parallel-run-enabled = true`
4. If feature toggle disabled: write only to Spring Boot (normal operation after cutover)
5. Idempotency: dual-write operation is idempotent (retry-safe)
6. Atomicity: use Spring transactions (Spring Boot side) + COBOL 2PC coordination
7. Performance: dual-write adds < 100ms latency (secondary write is async if needed)
8. Audit trail tracks which system succeeded/failed
9. Monitoring: dual-write error rate < 0.1% acceptable threshold

**Prerequisites:** Stories 1.2, 1.4, 1.5, 2.2, 2.5

---

### Story 4.2: Data Comparison and Validation Service

**As a** QA Engineer,
**I want** an automated service that compares data between Spring Boot and COBOL to verify they match,
**So that** I can objectively measure readiness for traffic cutover.

**Acceptance Criteria:**

1. ValidationService created with methods:
   - validateCustomer(customerId): compares customer in Spring Boot vs COBOL
   - validatePolicy(policyId): compares policy in Spring Boot vs COBOL
   - compareDataStructures(newData, legacyData): detailed field-by-field comparison
2. Comparison logic:
   - Retrieves customer/policy from Spring Boot API
   - Retrieves same data from legacy COBOL API
   - Maps field names (Spring Boot field → COBOL field)
   - Compares values (handles data type conversions, date formats, etc.)
   - Reports match/mismatch on each field
3. Handles special cases:
   - ID format differences (CUST-2024-0847 vs 0847): marked as expected mapping
   - Date format differences (ISO 8601 vs legacy format): normalized before compare
   - Trailing spaces in COBOL strings: trimmed
   - Null/empty handling (NULL = blank string in COBOL)
4. Returns validation report:
   ```json
   {
     "customerId": "CUST-2024-0847",
     "status": "MATCH",
     "timestamp": "2025-10-31T10:15:00Z",
     "fields": [
       {
         "fieldName": "firstName",
         "newSystemValue": "Jane",
         "legacySystemValue": "Jane",
         "match": true
       },
       {
         "fieldName": "email",
         "newSystemValue": "jane@example.com",
         "legacySystemValue": "jane@example.com",
         "match": true
       }
     ],
     "overallMatch": true
   }
   ```
5. Validation report persisted in database (for analytics/audit)
6. Performance: validation < 2s for typical record

**Prerequisites:** Stories 1.2, 2.1, 2.3

---

### Story 4.3: Validation Dashboard UI

**As a** QA Manager,
**I want** a dashboard showing parallel run validation results and match rates,
**So that** I can monitor readiness for traffic cutover.

**Acceptance Criteria:**

1. Validation dashboard page created at route /admin/parallel-run-validation
2. Real-time metrics displayed:
   - Overall match rate (% of operations that validated successfully)
   - Operations validated (count)
   - Operations with mismatches (count)
   - Most recent operations (table of last 50 validation results)
3. Metrics by entity:
   - Customer operations: match rate %, count
   - Policy operations: match rate %, count
   - Policy by type: Motor, Endowment, House, Commercial (separate match rates)
4. Validation details table with columns:
   - Operation ID (link to full validation report)
   - Entity type (Customer/Policy)
   - Entity ID
   - Operation (Create/Update/Delete)
   - Timestamp
   - Status (✅ MATCH / ⚠️ MISMATCH / ❌ ERROR)
   - Fields matched (3/10 for mismatches)
5. Filtering/sorting:
   - Filter by date range
   - Filter by entity type
   - Filter by status (match/mismatch)
   - Sort by timestamp, status
6. Mismatch details (click on row):
   - Shows side-by-side comparison of fields
   - Highlights differences
   - Explains difference (e.g., "ID format expected, mapped correctly")
7. Export button: export validation report to CSV
8. Auto-refresh every 30s (real-time monitoring)
9. Alert threshold: if mismatch rate > 2%, show warning banner

**Prerequisites:** Stories 1.2, 3.1, 4.2

---

### Story 4.4: Circuit Breaker and Failover Pattern

**As a** Architect,
**I want** to implement circuit breaker pattern for API → COBOL routing,
**So that** if legacy COBOL system becomes unavailable, requests fail gracefully without overwhelming it.

**Acceptance Criteria:**

1. Circuit breaker implemented for COBOL service calls
2. States: CLOSED (normal), OPEN (failing, don't call), HALF_OPEN (test if recovered)
3. Thresholds configurable (via feature toggle or config):
   - Failure rate threshold: 50% (if 50%+ of requests fail, open circuit)
   - Request volume threshold: 5 requests (need at least 5 requests to measure)
   - Timeout threshold: 5s (requests taking > 5s are considered failures)
4. Circuit transitions:
   - **CLOSED → OPEN:** When failure threshold crossed, open circuit (stop calling legacy)
   - **OPEN → HALF_OPEN:** After 30s open, enter half-open (test with 1 request)
   - **HALF_OPEN → CLOSED:** If test request succeeds, close circuit (resume normal calls)
   - **HALF_OPEN → OPEN:** If test request fails, reopen circuit
5. When circuit OPEN:
   - Requests don't call legacy system
   - Return fallback response (or error with clear message)
   - If using dual-write: Spring Boot write succeeds, legacy write skipped (acceptable)
6. Monitoring circuit breaker:
   - Emit metric on state changes
   - Alert ops team if circuit opens
   - Expose /actuator/circuitbreakers endpoint
7. Config example (CircuitBreakerRegistry):
   ```yaml
   resilience4j:
     circuitbreaker:
       instances:
         cobolService:
           failureRateThreshold: 50
           slowCallRateThreshold: 50
           slowCallDurationThreshold: 5s
           waitDurationInOpenState: 30s
   ```

**Prerequisites:** Stories 1.2, 1.4

---

### Story 4.5: Feature Toggle Management UI and Advanced Routing

**As a** DevOps Engineer,
**I want** an admin UI to manage feature toggles without code deployment,
**So that** I can safely control traffic routing during parallel run.

**Acceptance Criteria:**

1. Feature toggle management page created at route /admin/feature-toggles
2. Page displays list of all toggles:
   - Toggle name (e.g., `customer-api-enabled`)
   - Current state (ON/OFF)
   - Description
   - Last changed (who, when)
   - Percentage traffic routed (for canary deployments: 10%, 50%, 100%)
3. Toggle controls:
   - Toggle on/off switch
   - Percentage slider (for canary: 0-100%, in 10% increments)
4. Update workflow:
   - Admin clicks toggle switch
   - Confirmation dialog: "Enable customer-api-enabled for 50% of traffic?" with reason field
   - Reason captured in audit trail
   - State persists to database
   - Notification sent to ops team
5. Toggle state propagates to:
   - API Gateway (routes requests based on toggle state)
   - Dual-write service (dual-write only if parallel-run-enabled = ON)
   - Client applications (can query toggle state for feature flags)
6. Advanced routing rules (optional, if using sophisticated feature toggle system):
   - Route by user group (e.g., "beta testers" to new system, others to legacy)
   - Route by time of day (e.g., run new system validation tests at 2am)
   - Route by request parameter (e.g., ?use-new-api=true for testing)
7. Audit trail for all toggle changes
8. Rollback toggle to previous state (undo button)

**Prerequisites:** Stories 1.2, 1.5, 3.1

---

## EPIC 5: Policy Management API & React UI

**Expanded Goal:**

Complete the modernized application by implementing the full policy management lifecycle in Spring Boot + React, supporting all 4 policy types (Motor, Endowment, House, Commercial). This epic creates the policy domain models with type-specific attributes, implements policy CRUD APIs with comprehensive validation, builds React UI for policy management including a type-specific create wizard, and integrates policies with customer management. Upon completion, the application achieves full feature parity with legacy system, agents can perform all customer and policy operations via web UI, and the system is ready for production traffic cutover.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 19-24

---

### Story 5.1: Policy Domain Model and Database Schema

**As a** Backend Developer,
**I want** to define the Policy domain model with support for 4 policy types,
**So that** I can implement policy CRUD operations.

**Acceptance Criteria:**

1. Policy base JPA entity created with fields:
   - policyId (UUID, primary key)
   - customerId (FK to Customer)
   - policyNumber (string, unique)
   - policyType (enum: MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)
   - status (enum: ACTIVE, RENEWED, LAPSED)
   - startDate (date)
   - endDate (date)
   - premiumAmount (decimal)
   - createdAt, updatedAt (timestamp)
   - notes (text)
2. Type-specific attribute handling (USING @Inheritance strategy):
   - Option A: Single-table inheritance (one POLICY table with null columns for unused type attributes)
   - Option B: Joined-table inheritance (POLICY table + MOTOR_POLICY, ENDOWMENT_POLICY, etc. tables)
   - (Recommend option A for simplicity, option B for normalization - clarify with team)
3. Type-specific attributes in subclasses:
   - **MotorPolicy:** registrationNumber, engineType, vehicleValue, driverAge
   - **EndowmentPolicy:** sumAssured, term (years), maturityAmount
   - **HousePolicy:** propertyAddress, propertyValue, coveredRisks
   - **CommercialPolicy:** businessType, annualRevenue, numberOfEmployees
4. Database tables created:
   - POLICY (base table with common fields)
   - MOTOR_POLICY, ENDOWMENT_POLICY, HOUSE_POLICY, COMMERCIAL_POLICY (type-specific tables OR columns in single table)
5. Flyway migration script created
6. JPA repository interfaces created for each type (PolicyRepository, MotorPolicyRepository, etc.)
7. Custom finder methods: findByCustomerId(), findByStatus(), findByPolicyType()
8. Indexes on customerId, policyNumber, status, startDate
9. Schema tested with sample data

**Prerequisites:** Stories 1.3, 2.1

---

### Story 5.2: Policy Create API - Generic Endpoint

**As a** Customer Service Agent,
**I want** to create a new policy of any type via REST API,
**So that** I can add policies to the system.

**Acceptance Criteria:**

1. POST /api/v1/policies endpoint implemented
2. Request body:
   ```json
   {
     "customerId": "CUST-2024-0847",
     "policyType": "MOTOR",
     "startDate": "2025-11-01",
     "endDate": "2026-10-31",
     "premiumAmount": 850.00,
     "notes": "New motor insurance",
     "typeSpecificData": {
       "registrationNumber": "AB21XYZ",
       "engineType": "petrol",
       "vehicleValue": 25000,
       "driverAge": 35
     }
   }
   ```
3. Returns 201 Created with policy object including policyNumber (auto-generated, format: P-YYYYMM-NNNNN)
4. Policy validation:
   - customerId must exist
   - policyType valid (MOTOR/ENDOWMENT/HOUSE/COMMERCIAL)
   - startDate <= endDate
   - premiumAmount > 0
   - Type-specific validation (see story 5.3)
5. Duplicate policy prevention: cannot create same policy twice (per business rule)
6. Audit entry created
7. Policy linked to customer automatically

**Prerequisites:** Stories 1.2, 1.3, 5.1, Story 2.1 (customer must exist)

---

### Story 5.3: Policy Type-Specific Validation

**As a** Backend Developer,
**I want** to implement validation rules specific to each policy type,
**So that** policies are created with valid data.

**Acceptance Criteria:**

1. **MotorPolicy validation:**
   - registrationNumber: required, valid format (e.g., 2-3 letters + 2 numbers + 1-3 letters)
   - engineType: required, enum (petrol, diesel, electric, hybrid)
   - vehicleValue: required, > 0
   - driverAge: required, >= 18 and <= 100
2. **EndowmentPolicy validation:**
   - sumAssured: required, > 0
   - term: required, 1-40 years
   - maturityAmount: required, >= sumAssured
3. **HousePolicy validation:**
   - propertyAddress: required, non-empty
   - propertyValue: required, > 0
   - coveredRisks: required, at least 1 selected (fire, theft, flood, etc.)
4. **CommercialPolicy validation:**
   - businessType: required, non-empty
   - annualRevenue: required, > 0
   - numberOfEmployees: required, >= 1
5. Custom validators for complex rules
6. Validation annotations (@NotNull, @Pattern, @Min, @Max) in entity
7. Error messages specific to each type
8. Validation error response:
   ```json
   {
     "error": {
       "code": "VALIDATION_ERROR",
       "details": [
         {
           "field": "driverAge",
           "message": "Driver age must be 18-100",
           "value": 15
         }
       ]
     }
   }
   ```

**Prerequisites:** Stories 1.2, 5.1

---

### Story 5.4: Policy Read/Search APIs

**As a** Customer Service Agent,
**I want** to retrieve policy details and search policies,
**So that** I can find and view policies.

**Acceptance Criteria:**

1. GET /api/v1/policies/{policyId} endpoint:
   - Returns 200 OK with policy object (including type-specific data)
   - Returns 404 if not found
   - Performance: < 100ms
2. GET /api/v1/policies endpoint (search/list) with parameters:
   - `customerId` (optional): filter by customer
   - `policyType` (optional): filter by type
   - `status` (optional): filter by status
   - `startDateFrom`, `startDateTo` (optional): date range filter
   - `query` (optional): search by policyNumber
   - `limit`, `offset`: pagination
3. Returns response with:
   - data array (list of policies)
   - pagination info (limit, offset, total, hasMore)
4. Sorting: by startDate DESC (newest first)
5. Performance: < 500ms for large datasets
6. Audit entry created for read operations

**Prerequisites:** Stories 1.2, 5.1

---

### Story 5.5: Policy Update/Status Change APIs

**As a** Customer Service Agent,
**I want** to update policy details and change policy status,
**So that** I can manage policy lifecycle.

**Acceptance Criteria:**

1. PUT /api/v1/policies/{policyId} endpoint:
   - Accepts partial updates
   - Allows updating: notes, premiumAmount, type-specific attributes
   - Does NOT allow changing: policyNumber, customerId, policyType, startDate (immutable)
   - Returns 200 OK with updated policy
   - Audit entry tracks changed fields
2. PATCH /api/v1/policies/{policyId}/status endpoint:
   - Changes policy status (ACTIVE → RENEWED/LAPSED)
   - Request body: { "status": "RENEWED" }
   - Validates status transition (business rules)
   - Returns 200 OK
   - Audit entry created with status change
3. Optimistic locking: version/etag prevents concurrent updates
4. Validation errors return 400 Bad Request

**Prerequisites:** Stories 1.2, 5.1, 5.3

---

### Story 5.6: Policy Delete API

**As a** Compliance Officer,
**I want** to soft-delete policies (archive with audit trail),
**So that** policies are retained for audit purposes.

**Acceptance Criteria:**

1. DELETE /api/v1/policies/{policyId} endpoint:
   - Soft-deletes (sets status to LAPSED or marks as archived, based on business rules)
   - Returns 200 OK or 204 No Content
   - Audit entry created with delete reason
2. Hard delete NOT available via API (compliance requirement)
3. Idempotent: deleting already-deleted policy returns 200 OK

**Prerequisites:** Stories 1.2, 5.1

---

### Story 5.7: Policy Detail and Edit Page (React)

**As a** Customer Service Agent,
**I want** to view and edit policy details in the web UI,
**So that** I can manage policies via the new system.

**Acceptance Criteria:**

1. Page created at route /policies/:policyId
2. Display policy details in read-only mode:
   - Policy number
   - Linked customer (clickable link to customer detail)
   - Policy type (Motor/Endowment/House/Commercial)
   - Status badge
   - Start/end dates
   - Premium amount
   - Type-specific attributes (display format varies by type)
   - Created/updated timestamps
   - Notes
3. Edit mode:
   - Edit button → switches to form
   - Form fields for editable fields (premium, notes, type-specific attrs)
   - Real-time validation
   - Save/Cancel buttons
4. Status change dropdown:
   - Available transitions based on current status
   - Confirmation dialog for important transitions
5. Success/error messages on update
6. Loading spinner
7. Not found error if policy doesn't exist

**Prerequisites:** Stories 1.2, 3.1, 5.1, 5.4, 5.5

---

### Story 5.8: Policy Create Wizard (React) - Type-Specific Forms

**As a** Customer Service Agent,
**I want** to create a new policy using a guided wizard with type-specific forms,
**So that** I can create policies without making mistakes.

**Acceptance Criteria:**

1. Page created at route /policies/create
2. Multi-step wizard:
   - **Step 1:** Select customer (search customer, select from results)
   - **Step 2:** Select policy type (radio buttons: Motor / Endowment / House / Commercial)
   - **Step 3:** Basic policy info (startDate, endDate, premiumAmount, notes)
   - **Step 4:** Type-specific details (different form based on type selected)
   - **Step 5:** Review and confirm
3. Type-specific forms in Step 4:
   - **MotorPolicy:** registrationNumber, engineType (dropdown), vehicleValue, driverAge
   - **EndowmentPolicy:** sumAssured, term (years), maturityAmount
   - **HousePolicy:** propertyAddress, propertyValue, coveredRisks (checkboxes)
   - **CommercialPolicy:** businessType, annualRevenue, numberOfEmployees
4. Form validation:
   - Real-time field validation
   - Step-level validation (can't proceed to next step if current step has errors)
   - Error messages displayed inline
5. Navigation:
   - Next/Previous buttons
   - Progress indicator (step 1/5, 2/5, etc.)
6. Review step shows all entered data (read-only)
7. Confirm button creates policy via API
8. Success message with policy number
9. Error handling (display error, allow retry)
10. Option to create another policy or view newly created policy

**Prerequisites:** Stories 1.2, 3.1, 3.6 (build on create wizard pattern), 5.1, 5.2

---

### Story 5.9: Policy-to-Customer Linking UI Integration

**As a** Customer Service Agent,
**I want** to see linked policies on customer detail page and see linked customer on policy page,
**So that** I can navigate between related entities.

**Acceptance Criteria:**

1. Customer detail page includes "Linked Policies" section:
   - Table of policies for this customer
   - Columns: Policy Number, Type, Status, Start Date, Actions (View, Edit)
   - "Create New Policy" button links to /policies/create?customerId=...
2. Policy detail page includes "Linked Customer" section:
   - Customer card showing: Name, Email, Phone
   - Clickable link to customer detail page
   - Edit customer button (links to customer edit page)
3. When creating policy from customer page (Story 5.8):
   - Customer pre-selected in Step 1 of wizard
4. Reciprocal navigation:
   - Customer → Policies → Policy → Customer (all navigation working)

**Prerequisites:** Stories 1.2, 3.5, 5.7

---

### Story 5.10: Policy Management Audit Logging and Documentation

**As a** Compliance Officer,
**I want** comprehensive audit logging for all policy operations and complete API documentation,
**So that** compliance requirements are met and API is discoverable.

**Acceptance Criteria:**

1. Audit logging for all policy operations:
   - CREATE: log policy creation with all attributes
   - READ: log policy access (for compliance)
   - UPDATE: log before/after values for changed fields
   - DELETE: log policy deletion with reason
   - STATUS_CHANGE: log status transitions
2. Audit query API (admin only): GET /api/v1/audit?entity=POLICY&limit=100
3. OpenAPI documentation updated for all policy endpoints:
   - All 5 policy API endpoints documented
   - Request/response schemas for each policy type
   - Status codes and error responses
   - Example requests/responses
4. Swagger UI displays complete policy API (http://localhost:8080/api/docs)
5. Changelog documentation for policy-related changes

**Prerequisites:** Stories 1.2, 5.1-5.6, Story 2.8 (build on audit logging pattern)

---

## Story Guidelines Reference

**Story Format:**

```
**Story [EPIC.N]: [Story Title]**

As a [user type],
I want [goal/desire],
So that [benefit/value].

**Acceptance Criteria:**
1. [Specific testable criterion]
2. [Another specific criterion]
3. [etc.]

**Prerequisites:** [Dependencies on previous stories, if any]
```

**Story Requirements:**

- **Vertical slices** - Complete, testable functionality delivery
- **Sequential ordering** - Logical progression within epic
- **No forward dependencies** - Only depend on previous work
- **AI-agent sized** - Completable in 2-4 hour focused session
- **Value-focused** - Integrate technical enablers into value-delivering stories

---

**For implementation:** Use the `create-story` workflow to generate individual story implementation plans from this epic breakdown.
