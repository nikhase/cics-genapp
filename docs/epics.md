# CICS GenApp Cloud Modernization - Epic Breakdown

**Author:** Niklas
**Date:** November 1, 2025
**Project Level:** Level 3 (Enterprise Cloud Modernization)
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

**Architecture Note:** All epics align with the target architecture documented in [target-architecture.md](./new/target-architecture.md), including Spring Boot 3.3+, React 18, PostgreSQL 15+, OIDC/Zitadel authentication, Debezium CDC, Unleash feature toggles, Docker/Kubernetes/Helm, and observability stack (ELK, Prometheus, Jaeger).

---

## EPIC 1: Cloud Foundation & Deployment Infrastructure

**Expanded Goal:**

Establish the complete cloud-native foundation and deployment infrastructure needed to run the modernized application. This epic creates the Spring Boot starter project with PostgreSQL connectivity, configures API Gateway for strangler pattern routing, implements OIDC/Zitadel authentication, sets up Debezium CDC for data sync, implements Unleash feature toggles, and establishes CI/CD pipeline with Docker/Kubernetes/Helm. Upon completion, the technical foundation is ready for business logic implementation in subsequent epics, and the team can deploy services to cloud environments (AWS/Azure/GCP/on-prem Kubernetes).

**Story Count:** 12 stories | **Estimated Duration:** Weeks 1-6

---

### Story 1.1: Project Repository Setup and Developer Environment

**As a** Development Team Lead,
**I want** to establish a standardized GitHub repository structure with build tooling configuration,
**So that** all developers have a consistent starting point and can quickly run local builds.

**Acceptance Criteria:**

1. GitHub repository created with clear folder structure (src/, tests/, docs/, config/, helm/)
2. Maven pom.xml configured with Spring Boot 3.3+ LTS, all required dependencies (Spring Web, Data JPA, Security, Cloud Gateway, Actuator, etc.)
3. .gitignore configured for Maven/IntelliJ/VS Code/Docker artifacts
4. README.md with developer setup instructions (Java 17+, Maven 3.8+, Docker, PostgreSQL, Git clone, mvn clean install)
5. Local development can run: `mvn clean install && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"`
6. Git workflow documentation (branch naming: feature/*, bugfix/*, main is protected, PR process)
7. Java code style configuration (Google Style Guide via Checkstyle, SpotBugs) integrated into build
8. Pre-commit hooks configured to prevent unformatted code commits

**Prerequisites:** None

---

### Story 1.2: Spring Boot Starter Project with Core Configuration

**As a** Backend Developer,
**I want** a working Spring Boot 3.3+ application with core Spring configuration (profiles, properties, bean definitions),
**So that** I can add business logic without worrying about framework setup.

**Acceptance Criteria:**

1. Spring Boot application boots successfully with embedded Tomcat on port 8080
2. Application properties file configured for dev/test/prod profiles (application-dev.yml, application-test.yml, application-prod.yml)
3. Spring Security configured with OIDC readiness (placeholder for Zitadel integration in Story 1.5)
4. Spring Data JPA configured with Hibernate dialect (PostgreSQL dialect)
5. Actuator endpoints exposed at /actuator (health, metrics, info) for monitoring
6. Error handling middleware (@ControllerAdvice, custom exception handlers) in place with standardized error response format
7. Structured JSON logging configured using Logback/SLF4J (all log lines output as JSON with traceId)
8. Application can be packaged as Docker image (Dockerfile, Docker Compose for local dev with PostgreSQL)
9. Health check endpoint working: GET /actuator/health returns {"status":"UP"}

**Prerequisites:** Story 1.1

---

### Story 1.3: PostgreSQL Database Connectivity and Schema Management

**As a** Database Administrator,
**I want** the Spring Boot application to connect to PostgreSQL with connection pooling and schema versioning,
**So that** the application can reliably store and retrieve data with proper migrations.

**Acceptance Criteria:**

1. PostgreSQL JDBC driver configured in Spring Boot (latest version, 15+ compatibility)
2. Connection pool configured (HikariCP: 10 min, 20 max connections, 30s timeout)
3. Spring Data JPA configured with PostgreSQL dialect
4. Flyway migration tool integrated for schema versioning (V1__initial_schema.sql, etc.)
5. Initial schema migration script created (CUSTOMER, POLICY, AUDIT_LOG, FEATURE_TOGGLE tables with indexes)
6. Environment-specific PostgreSQL connection strings configured (dev uses local, test uses testcontainer, prod uses managed DB)
7. Connection health check working via Spring Actuator: GET /actuator/health/db returns {"status":"UP","details":{"database":"PostgreSQL"}}
8. Database connection tested with successful query execution (SELECT 1)
9. Docker Compose includes PostgreSQL service for local development

**Prerequisites:** Story 1.2

---

### Story 1.4: Spring Cloud Gateway and Strangler Pattern Routing

**As a** Architect,
**I want** to establish a Spring Cloud Gateway embedded in the Spring Boot application that routes requests to either new Spring Boot services or legacy COBOL system,
**So that** we can gradually migrate traffic without clients knowing about the backend change.

**Acceptance Criteria:**

1. Spring Cloud Gateway (4.x) configured as embedded gateway in Spring Boot
2. Routes configured (configurable via application.yml or database):
   - `/api/v1/customers/*` → routes to Spring Boot CustomerController OR legacy COBOL (via feature toggle)
   - `/api/v1/policies/*` → routes to Spring Boot PolicyController OR legacy COBOL (via feature toggle)
   - `/api/v1/audit/*` → routes to Spring Boot AuditController (always new system)
   - `/api/v1/auth/*` → routes to OIDC/Zitadel integration
3. Request/response logging implemented at gateway level (JSON structured logs with correlation ID)
4. Circuit breaker configured (Resilience4j) to fail over to legacy if new service is down (50% failure rate threshold, 30s wait)
5. Timeout handling (5s timeout for downstream services, graceful error response)
6. CORS headers configured for React frontend (http://localhost:3000 in dev, production domain in prod)
7. Request tracing ID (X-Trace-Id or correlation ID) added to all requests for distributed tracing
8. Gateway can be deployed independently and scales horizontally via Kubernetes

**Prerequisites:** Stories 1.2, 1.4

---

### Story 1.5: OIDC Authentication with Zitadel Integration

**As a** Security Engineer,
**I want** to integrate OpenID Connect (OIDC) authentication with Zitadel for user login,
**So that** users can securely authenticate without the application managing passwords.

**Acceptance Criteria:**

1. Zitadel configured as external OIDC provider (self-hosted or cloud instance)
2. Spring Security configured with OAuth2 resource server (validates JWT tokens from Zitadel)
3. Login flow implemented:
   - React frontend redirects to Zitadel login (GET /authorize?client_id=...&redirect_uri=http://localhost:3000/auth/callback&...)
   - Zitadel handles authentication (username/password or SSO)
   - Zitadel redirects back to React frontend with authorization code
   - React frontend exchanges code for JWT token (via backend endpoint or directly to Zitadel)
   - React frontend stores JWT in secure session storage
4. JWT token contains:
   - User ID in subject claim
   - Email claim
   - Role(s) in custom claim (e.g., "customer_service_agent", "admin", "compliance_officer")
   - Expiration (configurable, typical 1 hour for access token)
5. All API requests require valid JWT token in Authorization header: `Authorization: Bearer {token}`
6. Invalid/expired tokens return 401 Unauthorized with error code and message
7. Role-based access control configured (@PreAuthorize("hasRole('ADMIN')") on protected endpoints)
8. Logout endpoint implemented: POST /api/v1/auth/logout (clears session, invalidates token)
9. Token refresh mechanism if needed (via refresh token)

**Prerequisites:** Story 1.2

---

### Story 1.6: Unleash Feature Toggles for Traffic Management

**As a** DevOps Engineer,
**I want** a feature toggle system (Unleash) that controls traffic routing and feature visibility without code deployment,
**So that** we can safely canary traffic to new services and quickly rollback if issues occur.

**Acceptance Criteria:**

1. Unleash (5.x) configured and deployed (self-hosted via Docker/Kubernetes or Unleash SaaS)
2. Unleash Java client integrated into Spring Boot application
3. Toggles defined for:
   - `customer-api-enabled` (true = route to Spring Boot, false = route to legacy COBOL)
   - `policy-api-enabled` (true = route to Spring Boot, false = route to legacy COBOL)
   - `cdc-sync-enabled` (true = enable Debezium CDC sync to legacy Db2)
   - `parallel-run-validation-enabled` (true = run validation checks and populate dashboard)
4. Toggle can be updated at runtime via Unleash UI without redeployment
5. Toggle state queryable via API: GET /api/v1/admin/toggles returns list of toggles with current state
6. Toggle state persisted in Unleash backend (survives service restart)
7. Audit log created when toggle state changes (who, when, from/to state, reason)
8. Default toggle values configured in each environment (dev, test, prod)
9. Performance: toggle check < 1ms (cached locally with periodic refresh from Unleash)
10. Feature toggle logic in Spring Cloud Gateway routes based on toggle state

**Prerequisites:** Stories 1.2, 1.4

---

### Story 1.7: Structured Logging and Observability Setup

**As a** Operations Engineer,
**I want** centralized, structured logging with correlation IDs for request tracing across services,
**So that** I can quickly diagnose issues and understand request flow.

**Acceptance Criteria:**

1. Structured JSON logging configured (all log lines output as JSON with consistent schema)
2. Log schema includes: timestamp, level, logger, message, traceId (correlation ID), userId, customerId (if applicable), errorDetails
3. Log levels properly used (ERROR for failures, WARN for degradation, INFO for key events, DEBUG for details)
4. Correlation ID (X-Trace-Id header or MDC) added to all logs within a request context
5. Sensitive data masked in logs (no passwords, PII like full SSNs, API keys, credit card numbers)
6. Log aggregation configured (logs sent to ELK Stack: Elasticsearch + Logstash + Kibana)
7. Sample log lines documented in README (show example ERROR, WARN, INFO, DEBUG with field explanations)
8. Metrics exposed (request count, latency, error rate) via Micrometer to Prometheus endpoint (/actuator/prometheus)
9. Logging doesn't degrade performance (async appenders, ring buffer)

**Prerequisites:** Story 1.2

---

### Story 1.8: Containerization with Docker and Multi-Stage Builds

**As a** DevOps Engineer,
**I want** the Spring Boot application containerized in Docker with optimized multi-stage builds,
**So that** we can deploy consistently across dev, test, and production environments.

**Acceptance Criteria:**

1. Dockerfile created with multi-stage build:
   - Build stage: Maven image, runs mvn clean install
   - Runtime stage: openjdk:17-slim image, includes only JAR
2. Base image chosen (openjdk:17-slim or distroless/java17, minimal and secure)
3. Application JAR included in container
4. Docker image size < 500MB (optimized with multi-stage and minimal dependencies)
5. Container starts Spring Boot application on port 8080
6. Health check configured in Dockerfile: HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health
7. Non-root user configured (security best practice, e.g., appuser with UID 1000)
8. Environment variables can be passed to container for configuration (SPRING_PROFILES_ACTIVE, DB_URL, etc.)
9. docker build and docker run tested locally
10. Docker Compose file includes PostgreSQL service for local development

**Prerequisites:** Story 1.2

---

### Story 1.9: CI/CD Pipeline with GitHub Actions

**As a** Development Team,
**I want** an automated CI/CD pipeline that builds, tests, and deploys on every commit,
**So that** we can ship changes quickly with confidence.

**Acceptance Criteria:**

1. GitHub Actions workflow (.github/workflows/build-and-deploy.yml) configured
2. Pipeline triggers on:
   - Push to main branch
   - Pull requests to main
   - Manual trigger via workflow_dispatch
3. Pipeline stages:
   - **Checkout:** Clone repository
   - **Setup Java:** Install Java 17 and Maven
   - **Build:** mvn clean install (compiles, runs unit tests)
   - **Test:** Unit tests (with coverage report), integration tests (using TestContainers for PostgreSQL)
   - **Code Quality:** SonarQube scan or static analysis
   - **Security Scan:** Trivy scan on Docker image for vulnerabilities
   - **Package:** Create Docker image, tag with git commit hash and 'latest'
   - **Push:** Push Docker image to container registry (GitHub Container Registry, ECR, DockerHub, or private registry)
   - **Deploy (Auto):** Deploy to test environment (Kubernetes, pull new image, restart deployment)
   - **Deploy (Manual):** Deploy to production environment on approval (Kubernetes)
4. Build artifacts (JAR, Docker image) versioned with git commit hash (e.g., my-app:abc123def456)
5. Pipeline notifications sent to Slack/Teams on success/failure
6. Pipeline execution time < 10 minutes (fast feedback)
7. Failed pipelines block merges to main branch (via branch protection rule)
8. Deployment can be rolled back via pipeline (redeploy previous image version)

**Prerequisites:** Stories 1.2, 1.8

---

### Story 1.10: Debezium CDC Setup for PostgreSQL-to-Db2 Synchronization

**As a** Integration Architect,
**I want** to set up Debezium Change Data Capture with HTTP sink to sync PostgreSQL changes to legacy Db2,
**So that** data remains synchronized between new and legacy systems during the transition period.

**Acceptance Criteria:**

1. Debezium (2.4+) configured with PostgreSQL connector
2. Debezium deployed via Docker Compose or Kubernetes
3. PostgreSQL WAL (Write-Ahead Log) configured for CDC (max_wal_level = logical)
4. Debezium monitors PostgreSQL tables:
   - CUSTOMER (changes: INSERT, UPDATE, DELETE)
   - POLICY (changes: INSERT, UPDATE, DELETE)
   - AUDIT_LOG (changes: INSERT only, immutable)
5. HTTP sink configured to POST change events to Spring Boot application endpoint: POST /api/internal/cdc-sync
6. Change event payload includes:
   - table name
   - operation (I, U, D)
   - before/after values (for UPDATE operations)
   - timestamp
7. Spring Boot application receives CDC events and asynchronously syncs to legacy Db2:
   - Connect to Db2 via JDBC
   - Translate PostgreSQL data types to Db2 data types
   - Execute INSERT/UPDATE/DELETE on legacy Db2 tables
   - Handle conflicts gracefully (log and continue, don't fail the original Spring Boot transaction)
8. CDC is gated by feature toggle `cdc-sync-enabled` (can disable without redeployment)
9. CDC latency monitored: target < 5 seconds from PostgreSQL change to Db2 update
10. Failure handling: if Db2 sync fails, log error with details, alert ops team, don't rollback Spring Boot transaction

**Prerequisites:** Stories 1.2, 1.3

---

### Story 1.11: Kubernetes and Helm Charts for Cloud Deployment

**As a** DevOps Engineer,
**I want** Kubernetes manifests (via Helm charts) for deploying Spring Boot application to any Kubernetes cluster,
**So that** we can deploy consistently across AWS EKS, Azure AKS, GCP GKE, or on-premises Kubernetes.

**Acceptance Criteria:**

1. Helm Chart (3.x) created with structure:
   - Chart.yaml (metadata)
   - values.yaml (default configuration)
   - templates/ (Kubernetes resource templates)
2. Kubernetes resources defined:
   - Deployment (Spring Boot application, 2+ replicas for HA)
   - Service (ClusterIP for internal, LoadBalancer or Ingress for external access)
   - ConfigMap (application.yml configuration)
   - Secret (database credentials, Zitadel client secret, Unleash API token)
   - HorizontalPodAutoscaler (scale to 2-5 replicas based on CPU/memory)
   - PodDisruptionBudget (ensure 1+ pod always available during updates)
3. Helm values for different environments:
   - values-dev.yaml (development configuration)
   - values-test.yaml (test configuration)
   - values-prod.yaml (production configuration with HA, monitoring)
4. Deployment strategy: rolling updates (maxSurge=1, maxUnavailable=0)
5. Resource requests/limits configured (CPU: 250m request / 500m limit, Memory: 512Mi request / 1Gi limit)
6. Liveness and readiness probes configured (using /actuator/health endpoint)
7. Environment-specific database URLs, feature toggle endpoints, etc. configured via values
8. Helm install/upgrade tested locally (minikube or Docker Desktop Kubernetes)
9. Helm chart compatible with any Kubernetes distribution (EKS, AKS, GKE, on-prem)

**Prerequisites:** Stories 1.2, 1.8

---

### Story 1.12: Observability Stack (ELK, Prometheus, Jaeger, Grafana)

**As a** Operations Engineer,
**I want** comprehensive observability infrastructure for logs, metrics, and distributed tracing,
**So that** I can understand system behavior, diagnose issues, and monitor health.

**Acceptance Criteria:**

1. ELK Stack deployed:
   - Elasticsearch (8.x) for log aggregation
   - Logstash for log processing/parsing
   - Kibana for log exploration and dashboards
2. Spring Boot application sends structured JSON logs to ELK (via Logstash or direct Elasticsearch)
3. Prometheus (latest) deployed for metrics collection
4. Spring Boot exposes Prometheus metrics at /actuator/prometheus
5. Grafana (latest) deployed for visualization
6. Pre-built dashboards created in Grafana:
   - Application Overview (request count, response time, error rate)
   - Database Performance (query latency, connection pool status)
   - JVM Metrics (heap usage, GC time, thread count)
7. Jaeger (1.x) deployed for distributed tracing
8. Spring Boot configured with Jaeger client (instrumentation)
9. Requests traced across services with correlation IDs visible in Jaeger UI
10. Alerts configured in Prometheus AlertManager:
    - High error rate (> 1%)
    - High response latency (P99 > 500ms)
    - Pod restart frequency
    - Low disk space
11. All observability components deployed via Kubernetes (StatefulSets for databases, Deployments for services)

**Prerequisites:** Stories 1.2, 1.7, 1.11

---

## EPIC 2: Customer Service API & Spring Boot Backend

**Expanded Goal:**

Implement the complete customer management functionality in Spring Boot, exposing REST APIs that replace the legacy COBOL SSC1 transaction. This epic creates the customer domain model, implements all CRUD operations with comprehensive validation, adds audit logging, provides API documentation, and implements proper error handling. Upon completion, agents can begin using customer APIs and testing against the legacy COBOL system in parallel.

**Story Count:** 8 stories | **Estimated Duration:** Weeks 7-10

---

### Story 2.1: Customer Domain Model and PostgreSQL Schema

**As a** Backend Developer,
**I want** to define the Customer domain model and create the PostgreSQL schema,
**So that** I have a foundation for implementing customer CRUD operations.

**Acceptance Criteria:**

1. Customer JPA entity created with fields:
   - customerId (UUID, primary key)
   - firstName, lastName (String, required, 1-100 characters)
   - dateOfBirth (LocalDate, optional)
   - email (String, required, unique, email format)
   - phone (String, optional, phone format)
   - address, city, state, zipCode (String, optional)
   - status (enum: ACTIVE, INACTIVE)
   - createdAt, updatedAt (LocalDateTime, auto-managed via JPA @EntityListeners)
   - createdBy, updatedBy (String, populated from JWT token)
2. PostgreSQL table `CUSTOMER` created with:
   - Columns matching entity fields
   - Indexes: email, phone, status, createdAt
   - Constraints: email UNIQUE, NOT NULL on required fields
3. Flyway migration script created (V2__create_customer_table.sql) and tested
4. JPA repository interface created: `CustomerRepository extends JpaRepository<Customer, UUID>`
5. Custom finder methods implemented:
   - `findByEmail(String email)`
   - `findByPhone(String phone)`
   - `findByLastNameContainingIgnoreCase(String lastName)`
   - `findByStatusAndCreatedAtAfter(Status status, LocalDateTime date)` for reporting
6. Entity validation annotations applied:
   - `@NotNull` on required fields
   - `@Email` on email field
   - `@Pattern(regexp="...")` for phone format (international format support)
   - `@Min(18) @Max(150)` for age validation
7. Audit columns (createdAt, updatedAt, createdBy, updatedBy) auto-managed
8. Schema tested with sample data insert/select

**Prerequisites:** Story 1.3

---

### Story 2.2: Customer Create API (POST /api/v1/customers)

**As a** Customer Service Agent,
**I want** to create a new customer via REST API,
**So that** I can add new customers to the system.

**Acceptance Criteria:**

1. POST /api/v1/customers endpoint implemented with @PostMapping("/customers")
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
     "data": {
       "customerId": "550e8400-e29b-41d4-a716-446655440000",
       "firstName": "Jane",
       "lastName": "Smith",
       "email": "jane.smith@example.com",
       "phone": "+1-555-0123",
       "status": "ACTIVE",
       "createdAt": "2025-11-01T10:00:00Z"
     },
     "metadata": { "timestamp": "2025-11-01T10:00:00Z", "version": "1.0" }
   }
   ```
4. Validation errors return 400 Bad Request with field-level errors
5. Duplicate email returns 409 Conflict with message: "Customer with this email already exists"
6. Request/response logged in structured JSON format (info level)
7. Audit entry created automatically (operation: CREATE, user from JWT token, all field values)
8. Default status set to ACTIVE
9. Requires ROLE_CUSTOMER_SERVICE_AGENT or higher
10. Response time < 200ms typical

**Prerequisites:** Stories 1.2, 1.5, 2.1

---

### Story 2.3: Customer Read API (GET /api/v1/customers/{id})

**As a** Customer Service Agent,
**I want** to retrieve customer details by ID,
**So that** I can view customer information.

**Acceptance Criteria:**

1. GET /api/v1/customers/{customerId} endpoint implemented
2. Returns 200 OK with customer object:
   ```json
   {
     "data": {
       "customerId": "550e8400-e29b-41d4-a716-446655440000",
       "firstName": "Jane",
       "lastName": "Smith",
       "dateOfBirth": "1985-03-15",
       "email": "jane.smith@example.com",
       "phone": "+1-555-0123",
       "address": "123 Main St",
       "city": "Portland",
       "state": "OR",
       "zipCode": "97201",
       "status": "ACTIVE",
       "createdAt": "2025-11-01T10:00:00Z",
       "updatedAt": "2025-11-01T10:05:00Z"
     },
     "metadata": { "timestamp": "2025-11-01T10:15:00Z", "version": "1.0" }
   }
   ```
3. Customer not found returns 404 Not Found with message: "Customer {customerId} not found"
4. Response includes all customer fields including timestamps and audit info
5. Audit entry created (READ operations logged for compliance)
6. Performance: response < 100ms for typical query
7. Invalid UUID format returns 400 Bad Request
8. Can include related resources link: `"_links": {"policies": "/api/v1/policies?customerId=..."}`

**Prerequisites:** Stories 1.2, 1.5, 2.1

---

### Story 2.4: Customer Search/List API (GET /api/v1/customers)

**As a** Customer Service Agent,
**I want** to search for customers by name, email, or phone with pagination,
**So that** I can quickly find customers in the system.

**Acceptance Criteria:**

1. GET /api/v1/customers endpoint with query parameters:
   - `query` (optional): searches firstName, lastName, email, phone (case-insensitive substring match)
   - `status` (optional): filters by status (ACTIVE, INACTIVE)
   - `limit` (optional, default 50, max 100): page size
   - `offset` (optional, default 0): pagination offset
   - `sortBy` (optional, default "lastName"): field to sort by (firstName, lastName, email, createdAt)
   - `sortOrder` (optional, default "ASC"): ASC or DESC
2. Returns 200 OK with response:
   ```json
   {
     "data": [
       {"customerId": "...", "firstName": "Jane", "lastName": "Smith", "email": "...", "phone": "...", "status": "ACTIVE"},
       {"customerId": "...", "firstName": "John", "lastName": "Doe", "email": "...", "phone": "...", "status": "ACTIVE"}
     ],
     "pagination": {
       "limit": 50,
       "offset": 0,
       "total": 237,
       "hasMore": true
     },
     "metadata": { "timestamp": "2025-11-01T10:15:00Z", "version": "1.0" }
   }
   ```
3. Empty results return 200 OK with empty data array
4. Performance: response < 500ms for large datasets (1M+ customers)
5. Database query uses indexed columns (email, phone, lastName)
6. Results sorted by specified field
7. Audit entry created for search operations (for compliance)
8. Limit enforced (max 100) to prevent resource exhaustion
9. Offset must be non-negative

**Prerequisites:** Stories 1.2, 1.5, 2.1

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
     "address": "456 Oak Ave",
     "city": "Seattle"
   }
   ```
4. Returns 200 OK with updated customer object
5. Customer not found returns 404 Not Found
6. Validation errors return 400 Bad Request with field-level details
7. Duplicate email (if updating email) returns 409 Conflict
8. Tracks which fields changed (for audit trail):
   - Before: {"phone": "+1-555-0123", "address": "123 Main St"}
   - After: {"phone": "+1-555-0199", "address": "456 Oak Ave"}
9. updatedAt timestamp auto-updated to current time
10. Audit entry created with before/after values of changed fields
11. Optimistic locking: etag header prevents concurrent update conflicts (version field in entity)
12. Requires ROLE_CUSTOMER_SERVICE_AGENT or ROLE_ADMIN

**Prerequisites:** Stories 1.2, 1.5, 2.1

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
6. Audit entry created with deletion reason (optional request parameter)
7. Soft-deleted customer still queryable via GET (visible to compliance roles, hidden from normal search)
8. Associated policies linked to deleted customer handled per business rule:
   - Soft-deleted customer's policies remain ACTIVE (customer status is separate from policy status)
   - Policies can only be linked to ACTIVE customers for new creations
9. Hard delete not possible via API (compliance requirement)
10. Requires ROLE_COMPLIANCE_OFFICER or ROLE_ADMIN

**Prerequisites:** Stories 1.2, 1.5, 2.1

---

### Story 2.7: Input Validation and Error Handling Framework

**As a** Backend Developer,
**I want** consistent error handling and validation across all customer APIs,
**So that** clients receive clear, actionable error messages.

**Acceptance Criteria:**

1. Global exception handler (@ControllerAdvice) created for all endpoints
2. Validation errors return structured response:
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
         },
         {
           "field": "phone",
           "message": "Phone must be 10+ digits",
           "value": "555",
           "code": "INVALID_FORMAT"
         }
       ]
     },
     "metadata": { "timestamp": "2025-11-01T10:15:00Z", "traceId": "abc123..." }
   }
   ```
3. Field-level validation:
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

**Prerequisites:** Story 2.1

---

### Story 2.8: API Documentation (OpenAPI/Swagger) and Audit Logging

**As a** Frontend Developer,
**I want** comprehensive OpenAPI documentation and audit logging for all customer APIs,
**So that** I know how to call the APIs and compliance has a record of all operations.

**Acceptance Criteria:**

1. OpenAPI 3.0 specification generated via SpringDoc-OpenAPI (springdoc-openapi-starter-webmvc-ui)
2. Swagger UI available at /api/docs (interactive API explorer)
3. Documentation includes for each endpoint:
   - All endpoints (POST, GET, PUT, DELETE)
   - Request/response schemas with field descriptions
   - Status codes and error responses
   - Required headers (Authorization: Bearer {token})
   - Example requests/responses
   - Rate limiting info (if applicable)
   - Required roles
4. Audit logging implemented for all customer operations:
   - CREATE: log customer creation with all attributes, user ID, timestamp
   - READ: log customer access (for compliance), user ID, timestamp
   - UPDATE: log before/after values for changed fields, user ID, timestamp
   - DELETE: log customer deletion with reason, user ID, timestamp
5. Audit entries stored in PostgreSQL AUDIT_LOG table:
   - auditId (UUID, primary key)
   - timestamp (LocalDateTime)
   - userId (String from JWT)
   - operation (enum: CREATE, READ, UPDATE, DELETE)
   - entityType (CUSTOMER)
   - entityId (customerId)
   - changes (JSON: {fieldName: {before, after}, ...})
   - ipAddress (from request)
   - userAgent (from request)
6. Audit entries immutable (no update/delete, only insert)
7. Audit entries not returned to clients in normal API responses
8. Audit query API (admin/compliance only): GET /api/v1/audit?entity=CUSTOMER&limit=100

**Prerequisites:** Stories 2.2-2.6

---

## EPIC 3: React Frontend - Authentication & Core UI

**Expanded Goal:**

Build the responsive React single-page application that replaces the 3270 terminal interface. This epic creates the React 18 + Vite project, implements Zitadel OIDC login, builds the dashboard, and creates customer management screens (search, detail, create). Upon completion, agents can perform customer operations via web UI and begin validating against the legacy COBOL system.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 11-14

---

### Story 3.1: React Project Setup with Vite and Material Design

**As a** Frontend Developer,
**I want** to set up a React 18 SPA with Vite, TypeScript, routing, and Material Design 3,
**So that** I have a solid foundation for building UI components.

**Acceptance Criteria:**

1. React 18 project created with Vite (npm create vite@latest cicsgenapp-frontend -- --template react-ts)
2. TypeScript configured for type safety (tsconfig.json with strict mode enabled)
3. React Router v6 configured for client-side routing (BrowserRouter, Routes, lazy loading)
4. Material-UI (MUI) v5 installed with TypeScript support
5. Clarity Enterprise Design System theme customization applied (color palette, typography)
6. Project structure created:
   - src/components/ (reusable UI components)
   - src/pages/ (page-level components: Login, Dashboard, CustomerSearch, etc.)
   - src/services/ (API client, authentication service)
   - src/hooks/ (custom React hooks: useAuth, useApi, etc.)
   - src/context/ (React Context: AuthContext, ThemeContext)
   - src/types/ (TypeScript interfaces)
   - src/styles/ (theme configuration, global CSS)
7. ESLint and Prettier configured for code style
8. Public folder with favicon, index.html, robots.txt
9. Environment variables configured (.env.dev, .env.prod) with API_BASE_URL
10. Build process tested: npm run build (optimized build, size < 500KB gzipped for JavaScript bundle)
11. Dev server runs on http://localhost:3000

**Prerequisites:** Story 1.2

---

### Story 3.2: Login Page and Zitadel OIDC Authentication

**As a** User,
**I want** to log in with my corporate credentials via Zitadel OIDC,
**So that** I can securely access the system without managing passwords.

**Acceptance Criteria:**

1. Login page created at route /login
2. Page displays Zitadel logo and "Sign in with corporate SSO" button
3. Clicking button redirects to Zitadel authorization endpoint with correct parameters:
   - `client_id` (configured in .env)
   - `redirect_uri` (http://localhost:3000/auth/callback for dev, production URL for prod)
   - `response_type=code`
   - `scope=openid profile email`
4. Zitadel handles authentication (username/password or SSO)
5. After authentication, Zitadel redirects to /auth/callback route with `code` parameter
6. Frontend exchanges code for JWT token (via backend: POST /api/v1/auth/callback?code=...)
7. Backend calls Zitadel's token endpoint to exchange code for JWT
8. Frontend receives JWT and stores in secure session storage (NOT localStorage)
9. User context updated with: userId, email, name, roles (from JWT claims)
10. User redirected to /dashboard on successful login
11. Logout button clears session storage, redirects to /login
12. Protected routes check for valid token; redirect to /login if missing or expired
13. Token refresh: if access token near expiration, automatically refresh via refresh token
14. Error messages displayed on login failure: "Authentication failed", "Invalid credentials", or "Server error"
15. Loading state while exchanging code for token (spinner displayed)

**Prerequisites:** Stories 1.2, 1.5, 3.1

---

### Story 3.3: Dashboard Page with Navigation and Quick Actions

**As a** Customer Service Agent,
**I want** to see a dashboard with overview of workload and quick actions,
**So that** I can quickly navigate to common tasks.

**Acceptance Criteria:**

1. Dashboard page created at route /dashboard (protected route, requires auth)
2. Header section displays:
   - App logo/title ("CICS GenApp Modernization")
   - User greeting ("Welcome, Jane Smith")
   - User profile dropdown (with options: Profile, Settings, Logout)
   - Time-of-day greeting ("Good Morning" / "Good Afternoon" / "Good Evening" based on local time)
3. Sidebar navigation with menu items:
   - 🏠 Dashboard (home icon)
   - 👥 Customers (people icon)
   - 📄 Policies (document icon)
   - 📋 Audit Log (clock icon, visible to compliance_officer and admin roles only)
   - 📊 Reports (chart icon, visible to admin roles)
   - ⚙️ Admin (settings icon, visible to admin roles)
4. Sidebar collapses on mobile devices (hamburger menu)
5. Active menu item highlighted based on current route
6. Main content area displays quick actions:
   - "New Customer" button → /customers/create
   - "New Policy" button → /policies/create
   - "Search Customer" text input → /customers/search?query=...
   - "Search Policy" text input → /policies/search?query=...
7. Optional metrics dashboard (if business requires):
   - Total customers count (retrieved from backend)
   - Total policies count (retrieved from backend)
   - Recent activity feed (last 5 operations)
8. Responsive design: sidebar collapses on mobile, main content full width
9. Keyboard shortcut: Ctrl+K opens search modal (search customers globally)
10. Loading state while fetching metrics (skeleton loaders)

**Prerequisites:** Stories 1.2, 3.1, 3.2

---

### Story 3.4: Customer Search and List Page

**As a** Customer Service Agent,
**I want** to search for customers and see results in a paginated list,
**So that** I can quickly find customers.

**Acceptance Criteria:**

1. Page created at route /customers/search
2. Search form with fields:
   - Query input (placeholder: "Search by name, email, or phone")
   - Status filter dropdown (All / Active / Inactive)
   - Search button
   - Clear button (resets form to defaults)
3. Pressing Enter in search input submits search
4. Results displayed in data table:
   - Columns: ID (uuid, truncated), Name, Email, Phone, Status, Actions
   - Status shown with badge: green for ACTIVE, gray for INACTIVE
   - Sortable columns (click header to sort; arrow indicator shows direction)
   - Pagination controls (Previous/Next buttons, "Page X of Y", Rows per page: 25/50/100)
5. Actions column with buttons:
   - "View" button → navigates to /customers/{customerId} (detail page)
   - "Edit" button → navigates to /customers/{customerId}?mode=edit
   - "More" menu (three dots) with delete option (soft-delete with confirmation)
6. Empty state message if no results: "No customers found. Try different search criteria."
7. Loading state while fetching (spinner centered on page)
8. Error message if search fails (with retry button)
9. URL reflects search state (shareable): /customers/search?query=smith&status=ACTIVE&page=1&limit=50
10. Performance: < 2s load time for typical searches (use debouncing on search input, 300ms delay)
11. Accessibility: keyboard navigation, ARIA labels on buttons, screen reader support

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.4 (API)

---

### Story 3.5: Customer Detail and Edit Page

**As a** Customer Service Agent,
**I want** to view and edit customer details,
**So that** I can manage customer information.

**Acceptance Criteria:**

1. Page created at route /customers/{customerId}
2. Detail view (read-only mode) shows customer information:
   - Name (first/last)
   - Date of birth
   - Email
   - Phone
   - Address (street, city, state, zip)
   - Status (badge: green/gray)
   - Created/updated timestamps
   - Created/updated by (user information)
3. Edit button switches to edit mode
4. Edit mode displays form fields with current values pre-populated
5. Form validation (real-time feedback):
   - Email valid format
   - Phone valid format (if provided)
   - Name required, 1-100 chars
   - Show error message below field if validation fails
6. Save button submits changes (disabled until form is valid)
7. Cancel button discards changes and returns to read-only mode
8. Success message on save: "Customer updated successfully" (toast notification, 3s duration)
9. Error message on failure with details
10. 404 Not Found error if customer doesn't exist
11. Optimistic locking: if customer was updated by another user, show conflict error with option to refresh
12. Loading state while fetching/saving (spinner)
13. Related Policies section (read-only):
    - Table of policies linked to this customer
    - Columns: Policy Number, Type, Status, Start Date, Actions (View)
    - "Create New Policy" button → /policies/create?customerId={customerId}
14. Audit History section (if user has audit_viewer role):
    - Expandable section showing immutable audit log for this customer
    - Columns: Date, User, Operation, Changes
    - Limit to last 10 entries

**Prerequisites:** Stories 1.2, 3.1, 3.3, Stories 2.3, 2.5 (APIs)

---

### Story 3.6: Customer Create Page with Form Wizard

**As a** Customer Service Agent,
**I want** to create a new customer using a guided multi-step wizard,
**So that** I can add customers with minimal errors.

**Acceptance Criteria:**

1. Page created at route /customers/create
2. Multi-step wizard with progress indicator:
   - **Step 1:** Basic Info (first name, last name, date of birth)
   - **Step 2:** Contact (email, phone)
   - **Step 3:** Address (street, city, state, zip code)
   - **Step 4:** Review and Confirm
3. Form fields with validation:
   - firstName: text, required, 1-100 chars, error: "First name is required"
   - lastName: text, required, 1-100 chars
   - dateOfBirth: date picker, optional, must be age 18+, error: "Must be at least 18 years old"
   - email: email input, required, unique, valid email format
   - phone: tel input, optional, valid phone format (regex validation)
   - address: text, optional
   - city: text, optional
   - state: dropdown, optional (populated from state list)
   - zipCode: text, optional, 5-6 digits
4. Real-time validation feedback (field-level error messages displayed below field)
5. Next button disabled if current step has errors
6. Previous button always enabled (can go back)
7. Progress indicator shows current step (e.g., "Step 1 of 4")
8. Review step shows all entered data in read-only format:
   - Basic Info, Contact, Address all displayed
   - "Edit" button next to each section allows editing that section
9. Confirm button creates customer via API (POST /api/v1/customers)
10. Loading state while creating (spinner, button disabled)
11. Success message with customer ID and option to:
    - "View Customer" → /customers/{customerId}
    - "Create Another" → reset wizard
    - "Go to Dashboard" → /dashboard
12. Error handling:
    - Duplicate email error: "A customer with this email already exists"
    - Server error: "Failed to create customer. Please try again."
    - Validation error: show which field needs correction
13. Keyboard support: Tab navigation through form, Enter submits (on review step)

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.2 (API)

---

### Story 3.7: Form Components and Validation UI Library

**As a** Frontend Developer,
**I want** reusable form validation components and error handling UI,
**So that** forms provide consistent user experience across all pages.

**Acceptance Criteria:**

1. Reusable form components created:
   - **TextInput** component with real-time validation feedback
     - Props: label, placeholder, value, onChange, error, required, disabled
     - Shows error message below field in red
     - Visual error indicator (red border)
     - Required asterisk (*)
     - Optional helper text below field
   - **Select/Dropdown** component (same validation features as TextInput)
   - **DatePicker** component with calendar UI and validation
     - Validates date format, age calculation
     - Disabled dates handling
     - Max/min date constraints
   - **EmailInput** component with:
     - Real-time email validation
     - Async validation (check uniqueness via API)
     - Show "checking..." state while validating
   - **PhoneInput** component with:
     - Phone format validation
     - Format mask (user types, auto-formatted: (123) 456-7890)
   - **FormError** component for form-level errors
     - Displays above form with error icon
     - Red background, dismissible
   - **SuccessAlert** component for success messages
     - Green background with checkmark
     - Auto-dismiss after 3 seconds
   - **LoadingSpinner** component
     - Centered spinner with optional backdrop
     - Shows while API calls in progress
   - **ConfirmDialog** component for destructive actions
     - Modal with warning icon
     - "Cancel" and "Delete" buttons
     - Requires user to confirm before deletion
2. All components follow Material Design 3 design system (colors, spacing, typography)
3. All components support dark mode (theme provider)
4. Accessibility: ARIA labels, proper heading structure, keyboard navigation
5. Components tested with Jest + React Testing Library

**Prerequisites:** Stories 1.2, 3.1

---

### Story 3.8: API Integration and Client Service Layer

**As a** Frontend Developer,
**I want** a centralized API client service that handles authentication, error handling, and request/response,
**So that** components can easily call APIs without repetitive boilerplate.

**Acceptance Criteria:**

1. ApiClient service created with methods:
   - `get<T>(url: string, options?: AxiosRequestConfig): Promise<T>`
   - `post<T>(url: string, body: unknown, options?: AxiosRequestConfig): Promise<T>`
   - `put<T>(url: string, body: unknown, options?: AxiosRequestConfig): Promise<T>`
   - `delete<T>(url: string, options?: AxiosRequestConfig): Promise<T>`
2. Automatically attaches JWT token to Authorization header: `Authorization: Bearer {token}`
3. Handles response status codes:
   - 2xx: resolve promise with parsed JSON
   - 4xx: throw error with field-level details (validation errors)
   - 5xx: throw error with message
4. Retry logic for transient failures:
   - Max 3 retries with exponential backoff (100ms, 200ms, 400ms)
   - Retry on: 408 Request Timeout, 429 Too Many Requests, 5xx errors
   - Don't retry on: 4xx validation errors
5. Handles 401 Unauthorized (token expired):
   - Attempt to refresh token via refresh endpoint
   - If refresh fails, redirect to /login
6. Logging in development mode (console.log all API calls with timing)
7. Correlation ID (X-Trace-Id header) included in all requests (UUID)
8. Timeout handling (15s default timeout, configurable)
9. CustomerService wrapper with methods:
   - `createCustomer(data: CreateCustomerRequest): Promise<Customer>`
   - `getCustomer(id: string): Promise<Customer>`
   - `searchCustomers(query: SearchQuery): Promise<SearchResult<Customer>>`
   - `updateCustomer(id: string, data: UpdateCustomerRequest): Promise<Customer>`
   - `deleteCustomer(id: string): Promise<void>`
10. PolicyService wrapper (for future use):
    - Similar methods for policy operations
11. Usage example in components:
    ```typescript
    const customers = await CustomerService.searchCustomers({query: "smith", status: "ACTIVE"});
    ```
12. Error handling wrapper for common error scenarios:
    - Duplicate email: extract and display user-friendly message
    - Validation errors: display field-level errors
    - Network errors: show "No internet connection" message
    - Server errors: show "Server error, please try again later"

**Prerequisites:** Stories 1.2, 3.1, 3.2, Stories 2.1-2.8 (APIs)

---

## EPIC 4: Parallel Run Validation Framework

**Expanded Goal:**

Implement the critical infrastructure for safe, gradual traffic cutover from legacy COBOL to Spring Boot. This epic creates the Debezium CDC-based data synchronization (PostgreSQL → Db2), implements data comparison/validation logic, builds the validation dashboard for monitoring match rates, implements circuit breaker patterns for failover, and creates feature toggle management UI. Upon completion, agents can run 50-100+ parallel operations validating that new system data matches legacy system, providing the objective metrics needed to confidently route production traffic to new services.

**Story Count:** 5 stories | **Estimated Duration:** Weeks 15-18

---

### Story 4.1: Debezium CDC Implementation for PostgreSQL-to-Db2 Sync

**As a** Integration Architect,
**I want** to implement Debezium Change Data Capture that syncs PostgreSQL changes to legacy Db2 asynchronously,
**So that** data remains synchronized between new and legacy systems during the transition period.

**Acceptance Criteria:**

1. Debezium PostgreSQL connector configured to capture changes from CUSTOMER, POLICY, AUDIT_LOG tables
2. CDC events published via HTTP sink to Spring Boot endpoint: POST /api/internal/cdc-sync
3. Change event payload includes:
   - table name (CUSTOMER, POLICY, AUDIT_LOG)
   - operation (INSERT, UPDATE, DELETE)
   - before values (for UPDATE and DELETE)
   - after values (for INSERT and UPDATE)
   - timestamp of change
4. Spring Boot endpoint receives and processes CDC events:
   - Connects to legacy Db2 system
   - Maps PostgreSQL data types to Db2 data types (UUID → VARCHAR, LocalDateTime → TIMESTAMP, etc.)
   - Executes INSERT/UPDATE/DELETE on legacy Db2 tables (CUSTOMER, POLICY, AUDIT_LOG)
5. CDC is gated by feature toggle `cdc-sync-enabled` (can disable without redeployment)
6. Failure handling:
   - If Db2 sync fails, log error with full context (table, record ID, error message)
   - Alert operations team (via monitoring alert)
   - Do NOT rollback Spring Boot transaction (data is safe in PostgreSQL)
   - Continue processing next event (don't block pipeline)
7. Idempotency: if same change is processed twice, second attempt is idempotent (no duplicate inserts)
8. Latency monitoring:
   - Target < 5 seconds from PostgreSQL change to Db2 update
   - Track latency metric for each operation type
   - Alert if latency > 10 seconds (potential issue)
9. Dead letter queue (DLQ) for failed events:
   - Store failed CDC events in PostgreSQL table with retry count
   - Ops team can inspect and manually fix failed syncs
10. Comprehensive logging of all CDC operations (JSON structured logs)

**Prerequisites:** Stories 1.3, 1.10, 1.6, 2.1

---

### Story 4.2: Data Comparison and Validation Service

**As a** QA Engineer,
**I want** an automated service that compares data between Spring Boot and COBOL to verify they match,
**So that** I can objectively measure readiness for traffic cutover.

**Acceptance Criteria:**

1. ValidationService created with methods:
   - `validateCustomer(customerId: UUID): ValidationResult`
   - `validatePolicy(policyId: UUID): ValidationResult`
   - `compareDataStructures(newData: Map, legacyData: Map): FieldComparison[]`
2. Comparison logic:
   - Retrieves customer/policy from Spring Boot API
   - Retrieves same data from legacy COBOL system (via HTTP bridge or Db2 query)
   - Maps field names (Spring Boot field → COBOL field, e.g., firstName → FIRST_NAME)
   - Compares values (handles data type conversions, date formats, etc.)
   - Reports match/mismatch on each field
3. Handles special cases:
   - ID format differences (UUID vs alphanumeric) - marked as "expected mapping"
   - Date format differences (ISO 8601 vs MMDDYYYY) - normalized before compare
   - Trailing spaces in COBOL strings - trimmed
   - Null/empty handling (NULL = blank string in COBOL)
   - Numeric precision (round to same decimal places)
4. Returns validation report:
   ```json
   {
     "customerId": "550e8400-e29b-41d4-a716-446655440000",
     "status": "MATCH",
     "timestamp": "2025-11-01T10:15:00Z",
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
     "overallMatch": true,
     "matchPercentage": 100
   }
   ```
5. Validation report persisted in PostgreSQL (VALIDATION_RESULT table):
   - validationId (UUID)
   - operationId (links to original CREATE/UPDATE operation)
   - entityType (CUSTOMER, POLICY)
   - entityId
   - status (MATCH, MISMATCH, ERROR)
   - details (JSON with field comparisons)
   - timestamp
6. Performance: validation < 2s for typical record
7. Supports batch validation: `validateMultiple(ids: List<UUID>): List<ValidationResult>`
8. Error handling: if legacy system unreachable, store error in validation report (status: ERROR)

**Prerequisites:** Stories 1.3, 1.6, 2.1, 2.2-2.6

---

### Story 4.3: Parallel Run Validation Dashboard UI

**As a** QA Manager,
**I want** a dashboard showing parallel run validation results and match rates,
**So that** I can monitor readiness for traffic cutover.

**Acceptance Criteria:**

1. Validation dashboard page created at route /admin/parallel-run-validation (admin-only)
2. Real-time metrics displayed:
   - Overall match rate: XX.X% (all validations)
   - Operations validated: NNN (count)
   - Operations with mismatches: N (count)
   - Operations with errors: N (count)
   - Most recent operation status indicator (✅ MATCH / ⚠️ MISMATCH / ❌ ERROR)
3. Metrics by entity:
   - Customer operations: match rate %, count, trend (sparkline)
   - Policy operations: match rate %, count, trend (sparkline)
   - Policy by type breakdown: Motor, Endowment, House, Commercial (separate match rates)
4. Validation details table with columns:
   - Operation ID (link to full validation report)
   - Entity type (Customer/Policy)
   - Entity ID (truncated UUID)
   - Operation (CREATE/UPDATE/DELETE)
   - Timestamp
   - Status (✅ MATCH / ⚠️ MISMATCH / ❌ ERROR)
   - Fields matched (e.g., "10/10" for full match, "3/10" for partial match with mismatch details)
5. Filtering/sorting:
   - Filter by date range (date picker)
   - Filter by entity type (Customer/Policy)
   - Filter by status (MATCH/MISMATCH/ERROR)
   - Sort by timestamp (newest first), status, entity type
6. Mismatch details view (click on mismatch row):
   - Side-by-side comparison table:
     - Column: Field Name
     - Column: New System Value
     - Column: Legacy System Value
     - Column: Match? (✅ or ❌)
   - Highlight differences (e.g., red text for mismatched values)
   - Explanation (e.g., "ID format expected, mapped correctly")
7. Export functionality:
   - Export button: export validation report to CSV
   - Include all validations for selected filters
8. Auto-refresh: every 30 seconds (real-time monitoring)
9. Alert threshold:
   - If mismatch rate > 2%, show red warning banner: "⚠️ High mismatch rate (X%). Investigate before cutover."
   - If error rate > 1%, show red banner: "❌ Multiple validation errors. Check system logs."
10. Performance: dashboard loads < 2s, metrics refresh < 5s

**Prerequisites:** Stories 1.2, 3.1, 4.2

---

### Story 4.4: Circuit Breaker and Failover Pattern for Legacy System

**As a** Architect,
**I want** to implement circuit breaker pattern for API → legacy COBOL routing,
**So that** if legacy COBOL system becomes unavailable, requests fail gracefully without overwhelming it.

**Acceptance Criteria:**

1. Circuit breaker implemented for legacy COBOL service calls (Resilience4j)
2. States: CLOSED (normal), OPEN (failing, don't call), HALF_OPEN (test if recovered)
3. Thresholds configurable via Unleash feature toggles or application.yml:
   - Failure rate threshold: 50% (if 50%+ of requests fail, open circuit)
   - Request volume threshold: 5 requests (need at least 5 requests to measure)
   - Slow call rate threshold: 50% (if 50%+ of requests are slow, consider opening)
   - Slow call duration: 5s (requests taking > 5s are considered "slow")
   - Wait duration in open state: 30s (wait before testing recovery)
4. Circuit transitions:
   - **CLOSED → OPEN:** When failure/slow-call threshold crossed, open circuit (stop calling legacy)
   - **OPEN → HALF_OPEN:** After 30s open, enter half-open (test with 1 request)
   - **HALF_OPEN → CLOSED:** If test request succeeds, close circuit (resume normal calls)
   - **HALF_OPEN → OPEN:** If test request fails, reopen circuit
5. When circuit OPEN:
   - Requests don't call legacy system
   - Return fallback response (e.g., {"status": "fallback", "message": "Legacy system unavailable, using local data"})
   - Or throw exception with clear message: "Legacy system temporarily unavailable"
   - If using dual-write: Spring Boot write succeeds, legacy write skipped (acceptable for transition)
6. Monitoring circuit breaker:
   - Emit metrics on state changes (gauge: circuit_breaker_state)
   - Alert ops team when circuit opens (via Prometheus AlertManager)
   - Expose /actuator/circuitbreakers endpoint for health checks
7. Configuration example:
   ```yaml
   resilience4j:
     circuitbreaker:
       instances:
         cobolService:
           failureRateThreshold: 50
           slowCallRateThreshold: 50
           slowCallDurationThreshold: 5s
           waitDurationInOpenState: 30s
           minimumNumberOfCalls: 5
   ```
8. Logging: log all circuit state transitions (info level) with timestamp, previous state, new state
9. Dashboard integration: expose circuit breaker state in /api/v1/admin/health endpoint

**Prerequisites:** Stories 1.2, 1.4

---

### Story 4.5: Feature Toggle Management UI and Advanced Routing

**As a** DevOps Engineer,
**I want** an admin UI to manage feature toggles without code deployment,
**So that** I can safely control traffic routing during parallel run.

**Acceptance Criteria:**

1. Feature toggle management page created at route /admin/feature-toggles (admin-only)
2. Page displays list of all toggles with columns:
   - Toggle name (e.g., `customer-api-enabled`)
   - Current state (ON/OFF, shown as switch)
   - Description (e.g., "Route customer API requests to Spring Boot")
   - Last changed (who, when, reason)
   - Percentage traffic (for canary: 0%, 10%, 50%, 100%)
3. Toggle controls:
   - Toggle on/off switch (changes state immediately)
   - Percentage slider (for canary deployments: 0-100%, in 10% increments)
4. Update workflow:
   - Admin clicks toggle switch
   - Confirmation dialog appears:
     - "Enable customer-api-enabled for 50% of traffic?"
     - Reason field (required or optional, captures in audit trail)
     - "Cancel" and "Confirm" buttons
   - On confirm: update toggle state, show success message
   - State persists to Unleash backend
   - Notification sent to Slack/Teams channel (#devops-alerts)
5. Toggle state propagates to:
   - Spring Cloud Gateway (routes requests based on toggle state)
   - Feature toggle checks in Spring Boot (controls CDC, validation, etc.)
   - Optional: React frontend can query toggle state (e.g., show admin console only if toggle enabled)
6. Advanced routing rules (optional, if using sophisticated toggle system):
   - Route by user group (e.g., "qa_testers" to Spring Boot, others to legacy)
   - Route by time of day (e.g., run validation tests at 2am)
   - Route by request parameter (e.g., ?use-new-api=true for testing)
   - Route by geography (e.g., East Coast to Spring Boot, West Coast to legacy)
7. Audit trail for all toggle changes:
   - Toggle name, old state, new state, changed by (user), timestamp, reason
   - Immutable log in PostgreSQL AUDIT_LOG table
   - Queryable via /api/v1/audit?entity=FEATURE_TOGGLE
8. Rollback toggle to previous state:
   - "Undo" button reverts toggle to previous state with same audit trail entry
   - Available for 30 minutes after change

**Prerequisites:** Stories 1.2, 1.6, 3.1

---

## EPIC 5: Policy Management API & React UI

**Expanded Goal:**

Complete the modernized application by implementing the full policy management lifecycle in Spring Boot + React, supporting all 4 policy types (Motor, Endowment, House, Commercial). This epic creates the policy domain models with type-specific attributes, implements policy CRUD APIs with comprehensive validation, builds React UI for policy management including a type-specific create wizard, and integrates policies with customer management. Upon completion, the application achieves full feature parity with legacy system, agents can perform all customer and policy operations via web UI, and the system is ready for production traffic cutover.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 19-24

---

### Story 5.1: Policy Domain Model and PostgreSQL Schema (4 Types)

**As a** Backend Developer,
**I want** to define the Policy domain model with support for 4 policy types using JPA inheritance,
**So that** I can implement policy CRUD operations with type-specific attributes.

**Acceptance Criteria:**

1. Policy base JPA entity created with fields:
   - policyId (UUID, primary key)
   - customerId (FK to Customer)
   - policyNumber (String, unique, auto-generated format: P-YYYYMM-NNNNN)
   - policyType (enum: MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)
   - status (enum: ACTIVE, RENEWED, LAPSED)
   - startDate (LocalDate)
   - endDate (LocalDate)
   - premiumAmount (BigDecimal)
   - notes (String, optional)
   - createdAt, updatedAt (LocalDateTime, auto-managed)
   - createdBy, updatedBy (String, from JWT token)
2. Type-specific subclasses (JPA @Inheritance strategy: SINGLE_TABLE or JOINED_TABLE):
   - **MotorPolicy:** registrationNumber, engineType (enum: PETROL, DIESEL, ELECTRIC, HYBRID), vehicleValue (BigDecimal), driverAge (Integer)
   - **EndowmentPolicy:** sumAssured (BigDecimal), term (Integer years), maturityAmount (BigDecimal)
   - **HousePolicy:** propertyAddress (String), propertyValue (BigDecimal), coveredRisks (JSON array: fire, theft, flood, etc.)
   - **CommercialPolicy:** businessType (String), annualRevenue (BigDecimal), numberOfEmployees (Integer)
3. PostgreSQL tables created:
   - POLICY (base table with all common fields + policy_type discriminator column for SINGLE_TABLE)
   - OR POLICY + MOTOR_POLICY, ENDOWMENT_POLICY, HOUSE_POLICY, COMMERCIAL_POLICY (JOINED_TABLE strategy)
   - Recommend SINGLE_TABLE for simplicity
4. Indexes created: customerId, policyNumber, status, startDate, policyType
5. Constraints: policyNumber UNIQUE, FK customerId → CUSTOMER, NOT NULL on required fields
6. Flyway migration script created and tested
7. JPA repository interfaces created:
   - `PolicyRepository extends JpaRepository<Policy, UUID>`
   - `MotorPolicyRepository extends PolicyRepository` (if needed for type-specific queries)
   - Custom finder methods: `findByCustomerId`, `findByStatus`, `findByPolicyType`
8. Schema tested with sample data (1 customer, 1 policy of each type)

**Prerequisites:** Stories 1.3, 2.1

---

### Story 5.2: Policy Create API (POST /api/v1/policies)

**As a** Customer Service Agent,
**I want** to create a new policy of any type via REST API,
**So that** I can add policies to the system.

**Acceptance Criteria:**

1. POST /api/v1/policies endpoint implemented
2. Request body supports all 4 policy types:
   ```json
   {
     "customerId": "550e8400-e29b-41d4-a716-446655440000",
     "policyType": "MOTOR",
     "startDate": "2025-11-01",
     "endDate": "2026-10-31",
     "premiumAmount": 850.00,
     "notes": "New motor insurance",
     "typeSpecificData": {
       "registrationNumber": "AB21XYZ",
       "engineType": "PETROL",
       "vehicleValue": 25000.00,
       "driverAge": 35
     }
   }
   ```
3. Response 201 Created with policy object:
   ```json
   {
     "data": {
       "policyId": "550e8400-e29b-41d4-a716-446655440001",
       "policyNumber": "P-202511-00001",
       "customerId": "550e8400-e29b-41d4-a716-446655440000",
       "policyType": "MOTOR",
       "status": "ACTIVE",
       "startDate": "2025-11-01",
       "endDate": "2026-10-31",
       "premiumAmount": 850.00,
       "notes": "New motor insurance",
       "typeSpecificData": { ... },
       "createdAt": "2025-11-01T10:00:00Z"
     },
     "metadata": { "timestamp": "2025-11-01T10:00:00Z", "version": "1.0" }
   }
   ```
4. Policy validation:
   - customerId must exist (FK validation)
   - policyType valid (MOTOR/ENDOWMENT/HOUSE/COMMERCIAL)
   - startDate <= endDate
   - premiumAmount > 0
   - Type-specific validation (see Story 5.3)
5. Duplicate policy prevention (if applicable per business rule)
6. Audit entry created automatically
7. Policy linked to customer automatically
8. policyNumber auto-generated and unique
9. Default status: ACTIVE
10. Requires ROLE_CUSTOMER_SERVICE_AGENT or ROLE_ADMIN

**Prerequisites:** Stories 1.2, 1.5, 5.1, Story 2.1 (customer must exist)

---

### Story 5.3: Policy Type-Specific Validation

**As a** Backend Developer,
**I want** to implement validation rules specific to each policy type,
**So that** policies are created with valid data.

**Acceptance Criteria:**

1. **MotorPolicy validation:**
   - registrationNumber: required, valid format (e.g., regex: ^[A-Z]{2}\d{2}[A-Z]{3}$)
   - engineType: required, enum (PETROL, DIESEL, ELECTRIC, HYBRID)
   - vehicleValue: required, > 0
   - driverAge: required, >= 18 and <= 100
2. **EndowmentPolicy validation:**
   - sumAssured: required, > 0
   - term: required, 1-40 years
   - maturityAmount: required, >= sumAssured
3. **HousePolicy validation:**
   - propertyAddress: required, non-empty
   - propertyValue: required, > 0
   - coveredRisks: required, at least 1 selected (fire, theft, flood, earthquake, etc.)
4. **CommercialPolicy validation:**
   - businessType: required, non-empty
   - annualRevenue: required, > 0
   - numberOfEmployees: required, >= 1
5. Custom validators created:
   - `@ValidMotorPolicy` validator annotation
   - `@ValidEndowmentPolicy` validator annotation
   - `@ValidHousePolicy` validator annotation
   - `@ValidCommercialPolicy` validator annotation
6. Validation error response:
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
7. Error messages specific to each type (e.g., "Invalid registration number format for Motor policy")

**Prerequisites:** Stories 1.2, 5.1

---

### Story 5.4: Policy Read/Search APIs

**As a** Customer Service Agent,
**I want** to retrieve policy details and search policies,
**So that** I can find and view policies.

**Acceptance Criteria:**

1. GET /api/v1/policies/{policyId} endpoint:
   - Returns 200 OK with full policy object (including type-specific data)
   - Returns 404 if not found
   - Includes all fields plus createdBy/updatedBy
   - Performance: < 100ms
2. GET /api/v1/policies endpoint (search/list) with query parameters:
   - `customerId` (optional): filter by customer
   - `policyType` (optional): filter by type (MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)
   - `status` (optional): filter by status (ACTIVE, RENEWED, LAPSED)
   - `startDateFrom`, `startDateTo` (optional): date range filter
   - `query` (optional): search by policyNumber
   - `limit` (optional, default 50, max 100): page size
   - `offset` (optional, default 0): pagination offset
   - `sortBy`, `sortOrder`: sorting options
3. Returns paginated response:
   ```json
   {
     "data": [ ... ],
     "pagination": { "limit": 50, "offset": 0, "total": 237, "hasMore": true },
     "metadata": { "timestamp": "...", "version": "1.0" }
   }
   ```
4. Sorting: by startDate DESC (newest first) by default
5. Performance: < 500ms for large datasets
6. Audit entry created for read operations

**Prerequisites:** Stories 1.2, 5.1

---

### Story 5.5: Policy Update and Status Change APIs

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
   - Optimistic locking via version field
2. PATCH /api/v1/policies/{policyId}/status endpoint:
   - Changes policy status
   - Request body: `{"status": "RENEWED"}`
   - Validates status transition (business rules):
     - ACTIVE → RENEWED or LAPSED
     - RENEWED → LAPSED
     - LAPSED → (no transitions allowed)
   - Returns 200 OK
   - Audit entry created
3. Validation errors return 400 Bad Request

**Prerequisites:** Stories 1.2, 5.1, 5.3

---

### Story 5.6: Policy Soft-Delete API

**As a** Compliance Officer,
**I want** to soft-delete policies (archive with audit trail),
**So that** policies are retained for audit purposes.

**Acceptance Criteria:**

1. DELETE /api/v1/policies/{policyId} endpoint:
   - Soft-deletes (sets status to LAPSED or marks archived)
   - Returns 200 OK or 204 No Content
   - Audit entry created with delete reason
2. Hard delete NOT available via API
3. Idempotent: deleting already-lapsed policy returns 200 OK

**Prerequisites:** Stories 1.2, 5.1

---

### Story 5.7: Policy Detail and Edit Page (React)

**As a** Customer Service Agent,
**I want** to view and edit policy details in the web UI,
**So that** I can manage policies via the new system.

**Acceptance Criteria:**

1. Page created at route /policies/{policyId}
2. Detail view (read-only) displays:
   - Policy number, type, status (badge)
   - Linked customer (clickable link to customer detail)
   - Start/end dates, premium amount
   - Type-specific attributes displayed appropriately:
     - MotorPolicy: registration, engine type, vehicle value, driver age
     - EndowmentPolicy: sum assured, term, maturity amount
     - HousePolicy: property address, property value, covered risks (list)
     - CommercialPolicy: business type, annual revenue, employees
   - Notes
   - Created/updated timestamps and by whom
3. Edit mode:
   - Edit button switches to form
   - Form fields for editable fields (premium, notes, type-specific attrs)
   - Real-time validation
   - Save/Cancel buttons
4. Status change dropdown:
   - Available transitions based on current status
   - Confirmation dialog for transitions
5. Success/error messages on update
6. Loading spinner
7. 404 if policy doesn't exist

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
   - **MotorPolicy:** registrationNumber (text), engineType (dropdown), vehicleValue (number), driverAge (number)
   - **EndowmentPolicy:** sumAssured (number), term (number, years), maturityAmount (number)
   - **HousePolicy:** propertyAddress (text), propertyValue (number), coveredRisks (checkboxes: fire, theft, flood, etc.)
   - **CommercialPolicy:** businessType (text), annualRevenue (number), numberOfEmployees (number)
4. Form validation:
   - Real-time field validation
   - Step-level validation (can't proceed if current step has errors)
   - Error messages displayed inline
5. Navigation: Next/Previous buttons, progress indicator
6. Review step shows all entered data (read-only)
7. Confirm button creates policy via API
8. Success message with policy number
9. Error handling and retry
10. Option to create another or view newly created policy

**Prerequisites:** Stories 1.2, 3.1, 3.6, 5.1, 5.2

---

### Story 5.9: Policy-to-Customer Linking UI Integration

**As a** Customer Service Agent,
**I want** to see linked policies on customer detail page and linked customer on policy page,
**So that** I can navigate between related entities.

**Acceptance Criteria:**

1. Customer detail page includes "Linked Policies" section:
   - Table of policies for this customer
   - Columns: Policy Number, Type, Status, Start Date, Actions (View, Edit)
   - "Create New Policy" button links to /policies/create?customerId={customerId}
2. Policy detail page includes "Linked Customer" section:
   - Customer card: Name, Email, Phone
   - Clickable link to customer detail page
   - Edit customer button
3. When creating policy from customer page:
   - Customer pre-selected in Step 1 of wizard
4. Reciprocal navigation: Customer → Policies → Policy → Customer (all working)

**Prerequisites:** Stories 1.2, 3.5, 5.7

---

### Story 5.10: Policy Management Audit Logging and OpenAPI Documentation

**As a** Compliance Officer,
**I want** comprehensive audit logging for all policy operations and complete API documentation,
**So that** compliance requirements are met and APIs are discoverable.

**Acceptance Criteria:**

1. Audit logging for all policy operations:
   - CREATE: log policy creation with all attributes
   - READ: log policy access (for compliance)
   - UPDATE: log before/after values for changed fields
   - DELETE: log policy deletion with reason
   - STATUS_CHANGE: log status transitions
2. Audit query API (admin/compliance only): GET /api/v1/audit?entity=POLICY&limit=100
3. OpenAPI 3.0 documentation:
   - All 5 policy API endpoints documented
   - Request/response schemas for each policy type
   - Status codes and error responses
   - Example requests/responses
4. Swagger UI displays complete policy API (/api/docs)
5. Changelog documentation for policy-related changes

**Prerequisites:** Stories 1.2, 5.1-5.6, Story 2.8 (build on audit pattern)

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
- **AI-agent sized** - Completable in single focused session (2-4 hours)
- **Value-focused** - Integrate technical enablers into value-delivering stories

---

**For implementation:** Use the `create-story` workflow to generate individual story implementation plans from this epic breakdown.
