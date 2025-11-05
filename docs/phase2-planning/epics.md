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

**Architecture Note:** All epics align with the target architecture documented in [target-architecture.md](./new/target-architecture.md), including Spring Boot 3.4+ LTS, Java 21 LTS, Vaadin 24+ frontend, PostgreSQL 16 LTS, Spring Security authentication (with post-MVP OIDC/Zitadel), Debezium CDC, Unleash feature toggles, Docker/Kubernetes/Helm, and observability stack (ELK, Prometheus, Jaeger).

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
2. Maven pom.xml configured with Spring Boot 3.4+ LTS, all required dependencies (Spring Web, Data JPA, Security, Cloud Gateway, Actuator, etc.)
3. .gitignore configured for Maven/IntelliJ/VS Code/Docker artifacts
4. README.md with developer setup instructions (Java 21 LTS, Maven 3.8+, Docker, PostgreSQL 16 LTS, Git clone, mvn clean install)
5. Local development can run: `mvn clean install && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"`
6. Git workflow documentation (branch naming: feature/*, bugfix/*, main is protected, PR process)
7. Java code style configuration (Google Style Guide via Checkstyle, SpotBugs) integrated into build
8. Pre-commit hooks configured to prevent unformatted code commits

**Prerequisites:** None

---

### Story 1.2: Spring Boot Starter Project with Core Configuration

**As a** Backend Developer,
**I want** a working Spring Boot 3.4+ LTS application with core Spring configuration (profiles, properties, bean definitions),
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
   - **Setup Java:** Install Java 21 LTS and Maven
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

1. OpenAPI 3.0 specification generated via SpringDoc-OpenAPI (springdoc-openapi-starter-webmvc-ui v2.x)
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

## EPIC 3: Frontend Foundation & Customer Management UI (Vaadin)

**Expanded Goal:**

Build the responsive Vaadin web application that replaces the 3270 terminal interface, establishing the frontend infrastructure and implementing the first complete domain (customer management). This epic creates the Vaadin 24+ project with Spring Boot integration, implements Spring Security form-based login (with post-MVP OIDC), builds the dashboard and navigation foundation, and creates customer management screens (search, detail, create/edit). Upon completion, agents can perform customer operations via web UI and begin validating against the legacy COBOL system. The infrastructure and patterns established here (form components, API integration, validation framework) serve as the foundation for policy management and other domains in Epic 5.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 11-14

**Key Principle:** This epic is sequenced to establish **technology maturity** before policy management (Epic 5). By proving customer operations work reliably with parallel validation (Epic 4), we build confidence in the platform before expanding to policies.

---

### Story 3.1: Vaadin Project Setup with Spring Boot Integration

**As a** Frontend Developer,
**I want** to set up a Vaadin 24+ project integrated with Spring Boot 3.4+, with Material Design 3 theme and project structure,
**So that** I have a solid foundation for building server-side rendered web components.

**Acceptance Criteria:**

1. Vaadin 24+ project created with Spring Boot 3.4+ LTS integration (using Vaadin Spring Boot starter)
2. Java 21 LTS configured with Maven build
3. Vaadin routing configured (Router, @Route annotations, navigation layout)
4. Material Design 3 theme customization applied (color palette, typography, icons)
5. Project structure created:
   - src/main/java/com/example/ui/views/ (page-level views: LoginView, DashboardView, CustomerView, etc.)
   - src/main/java/com/example/ui/components/ (reusable Vaadin components)
   - src/main/java/com/example/service/ (API client service, authentication service)
   - src/main/java/com/example/security/ (Spring Security configuration)
   - src/main/resources/application.yml (environment-specific configuration)
   - frontend/ (optional Lit components or CSS styling)
6. Spring Security configured for form-based authentication (OIDC deferred to post-MVP)
7. Vaadin development mode tested: `mvn spring-boot:run` (live reload enabled)
8. Production build tested: `mvn clean install` (creates optimized JAR)
9. Dev server runs on http://localhost:8080
10. Responsive theme configured (Material Design 3 mobile breakpoints)

**Prerequisites:** Story 1.2

---

### Story 3.2: Login Page with Spring Security Form Authentication

**As a** User,
**I want** to log in with my username and password via Spring Security,
**So that** I can securely access the system.

**Acceptance Criteria:**

1. LoginView created at route `/login` (Vaadin @Route("/login"))
2. Page displays login form with:
   - Username field (text input)
   - Password field (password input)
   - "Sign In" button
   - "Forgot Password?" link (deferred functionality)
3. Form submission via POST to Spring Security `/login` endpoint
4. Spring Security validates credentials against user database
5. On successful authentication:
   - User context populated with: userId, email, name, roles (from Spring Security Principal)
   - User redirected to `/dashboard` route
   - Session created (server-side via Spring Security)
6. On failed authentication:
   - Error message displayed: "Invalid username or password"
   - Username field cleared, password field cleared
   - User remains on login page
7. Protected routes check for authenticated session; redirect to `/login` if not authenticated
8. Logout endpoint: POST `/logout` (Spring Security standard) redirects to `/login`
9. Remember-me functionality (optional): checkbox to extend session timeout
10. CSRF protection enabled (Spring Security default)
11. Responsive design (works on mobile/tablet)
12. Accessibility: form labels, error announcements

**Note:** OIDC/Zitadel integration deferred to post-MVP (Story 1.5 in backlog). Current implementation uses Spring Security form-based authentication with local user database.

**Prerequisites:** Stories 1.2, 3.1

---

### Story 3.3: Dashboard Page with Vaadin Navigation Layout

**As a** Customer Service Agent,
**I want** to see a dashboard with overview of workload and quick actions,
**So that** I can quickly navigate to common tasks.

**Acceptance Criteria:**

1. DashboardView created at route `/` or `/dashboard` (Vaadin @Route("/dashboard"))
2. Uses AppLayout (Vaadin component with header, sidebar, and content area)
3. Header section displays:
   - App logo/title ("CICS GenApp Modernization")
   - User greeting ("Welcome, Jane Smith")
   - User profile button (dropdown menu with: Profile, Settings, Logout)
   - Time-of-day greeting ("Good Morning" / "Good Afternoon" / "Good Evening" based on local time)
4. Sidebar navigation (NavBar or Drawer) with menu items:
   - 🏠 Dashboard (leads to /dashboard)
   - 👥 Customers (leads to /customers)
   - 📄 Policies (leads to /policies)
   - 📋 Audit Log (leads to /audit, visible to ROLE_COMPLIANCE_OFFICER and ROLE_ADMIN only)
   - 📊 Reports (leads to /reports, visible to ROLE_ADMIN only)
   - ⚙️ Admin (leads to /admin, visible to ROLE_ADMIN only)
5. Sidebar responsive: drawer mode on mobile, expand/collapse toggle
6. Active menu item highlighted based on current route (routing-aware)
7. Main content area displays quick actions:
   - "New Customer" button → navigates to /customers/create
   - "New Policy" button → navigates to /policies/create
   - "Search Customer" TextField with search button → navigates to /customers?query=...
   - "Search Policy" TextField with search button → navigates to /policies?query=...
8. Optional metrics dashboard (fetched from backend API):
   - Total customers count (GET /api/v1/admin/metrics/customers/count)
   - Total policies count (GET /api/v1/admin/metrics/policies/count)
   - Recent activity feed (last 5 operations from audit log)
9. Responsive design: Vaadin responsive utilities (HorizontalLayout, VerticalLayout with flex)
10. Logout functionality: logout button calls Spring Security /logout endpoint, redirects to /login
11. Loading state while fetching metrics (Vaadin Spinner component)
12. Accessibility: ARIA labels on navigation items, semantic HTML

**Prerequisites:** Stories 1.2, 3.1, 3.2

---

### Story 3.4: Customer Search and List Page with Vaadin Grid

**As a** Customer Service Agent,
**I want** to search for customers and see results in a paginated grid,
**So that** I can quickly find customers.

**Acceptance Criteria:**

1. CustomerListView created at route `/customers` (Vaadin @Route("/customers"))
2. Search form with fields (using Vaadin components):
   - TextField for query (placeholder: "Search by name, email, or phone")
   - ComboBox for status filter (All / Active / Inactive)
   - Button for search
   - Button to clear filters
3. Search performed on button click or Enter key in search field
4. Results displayed in Vaadin Grid with columns:
   - ID (UUID, truncated to first 8 chars)
   - Name (first + last name)
   - Email
   - Phone
   - Status (Badge component: green for ACTIVE, gray for INACTIVE)
   - Actions (buttons)
5. Grid features:
   - Sortable columns (click header; arrow indicator shows direction)
   - Pagination built-in (Vaadin Grid's built-in pagination)
   - Rows per page selector (25/50/100 rows)
   - Page navigation (Previous/Next, "Page X of Y")
6. Actions column with buttons:
   - "View" button → navigates to `/customers/{customerId}` (detail page)
   - "Edit" button → navigates to `/customers/{customerId}/edit`
   - Vertical menu (ContextMenu or right-click) with delete option
7. Delete confirmation dialog:
   - Title: "Delete Customer?"
   - Message: "This customer will be marked inactive. This action cannot be undone."
   - Buttons: "Cancel", "Delete"
   - On confirm: calls DELETE /api/v1/customers/{customerId}, removes row from grid
8. Empty state message if no results: "No customers found. Try different search criteria."
9. Loading state while fetching (Vaadin Spinner overlay on grid)
10. Error message if search fails (Vaadin Notification, "Error loading customers")
11. Query state preserved in URL: `/customers?query=smith&status=ACTIVE` (for bookmarking)
12. Performance: < 2s load time for typical searches (debounce search input at 300ms)
13. Accessibility: Grid has ARIA roles, keyboard navigation (arrow keys to navigate rows)

**Prerequisites:** Stories 1.2, 3.1, 3.3, Story 2.4 (API)

---

### Story 3.5: Customer Detail Page with Vaadin Components

**As a** Customer Service Agent,
**I want** to view and edit customer details,
**So that** I can manage customer information.

**Acceptance Criteria:**

1. CustomerDetailView created at route `/customers/{customerId}` (Vaadin @Route("/customers/:customerId"))
2. Detail view (read-only mode) displays customer information using Vaadin FormLayout:
   - Name (first/last, displayed as read-only TextField or Label)
   - Date of birth (formatted as MM/DD/YYYY)
   - Email
   - Phone
   - Address (street, city, state, zip)
   - Status (Badge component: green for ACTIVE, gray for INACTIVE)
   - Created/updated timestamps (formatted)
   - Created/updated by (user information)
3. Edit button switches to edit mode
4. Edit mode displays form fields with current values pre-populated (using FormLayout)
5. Form validation (real-time feedback):
   - Email valid format (Vaadin EmailField validator)
   - Phone valid format if provided (regex pattern)
   - Name required, 1-100 chars
   - Error messages displayed below each field (red text, icon)
6. Save button submits changes via PUT /api/v1/customers/{customerId} (disabled until form is valid)
7. Cancel button discards changes and returns to read-only mode
8. Success message on save: "Customer updated successfully" (Vaadin Notification, 3s)
9. Error message on failure (Notification with error details)
10. 404 error if customer doesn't exist (error view, "Customer not found")
11. Optimistic locking: if customer was updated by another user, show conflict notification with refresh option
12. Loading state while fetching/saving (Vaadin Spinner overlay)
13. Related Policies section (read-only):
    - Vaadin Grid showing policies linked to this customer
    - Columns: Policy Number, Type, Status, Start Date, Actions
    - "View" button on each row → navigates to `/policies/{policyId}`
    - "Create New Policy" button → navigates to `/policies/create?customerId={customerId}`
14. Audit History section (visible to users with ROLE_COMPLIANCE_OFFICER or ROLE_ADMIN):
    - Expandable section using Vaadin Details component
    - Grid showing: Date, User, Operation (CREATE/UPDATE/DELETE), Changes (JSON)
    - Limit to last 10 entries
    - Immutable data (display only)

**Prerequisites:** Stories 1.2, 3.1, 3.3, Stories 2.3, 2.5 (APIs)

---

### Story 3.6: Customer Create/Edit Page with Vaadin Form

**As a** Customer Service Agent,
**I want** to create and edit customer records using a guided multi-step form,
**So that** I can add and modify customers with minimal errors.

**Acceptance Criteria:**

1. CustomerCreateEditView created at route `/customers/create` (new) and `/customers/{customerId}/edit` (edit mode)
2. Multi-step form using Vaadin Stepper or custom step navigation:
   - **Step 1:** Basic Info (first name, last name, date of birth)
   - **Step 2:** Contact (email, phone)
   - **Step 3:** Address (street, city, state, zip code)
   - **Step 4:** Review and Confirm
3. Form fields with validation (using Vaadin FormLayout and field components):
   - firstName: TextField, required, 1-100 chars, error: "First name is required"
   - lastName: TextField, required, 1-100 chars
   - dateOfBirth: DatePicker, optional, must be age 18+, error: "Must be at least 18 years old"
   - email: EmailField, required, unique (async validation), valid email format
   - phone: TextField with pattern validator, optional, valid phone format (e.g., +1-555-0123)
   - address: TextField, optional, max 200 chars
   - city: TextField, optional, 1-100 chars
   - state: ComboBox, optional, pre-populated with US states
   - zipCode: TextField, optional, pattern \d{5}-?\d{4} (5-6 digits)
4. Real-time validation feedback (field-level error messages displayed below field, red styling)
5. Next button disabled if current step has validation errors
6. Previous button always enabled (can go back and modify earlier steps)
7. Progress indicator shows current step (e.g., "Step 1 of 4", visual progress bar)
8. Review step displays all entered data in read-only format:
   - Sections: Basic Info, Contact, Address
   - "Edit" button next to each section allows re-entering that step
   - All values displayed clearly for confirmation before submit
9. Confirm button:
   - For create: POST /api/v1/customers with form data
   - For edit: PUT /api/v1/customers/{customerId} with changes
10. Loading state while creating/updating (Vaadin Spinner overlay, button disabled)
11. Success message with options:
    - "View Customer" → navigates to /customers/{customerId}
    - "Create Another" → reset form to step 1 (create mode only)
    - "Go to Dashboard" → navigates to /dashboard
    - "Go Back" → navigates to previous page or /customers (edit mode)
12. Error handling:
    - Duplicate email error: "A customer with this email already exists"
    - Server error: "Failed to create customer. Please try again." (with retry)
    - Validation error: highlight field, show which field needs correction
    - Async email validation: show "checking..." spinner while validating uniqueness
13. Keyboard support: Tab navigation through form, Enter submits (on review step), Escape cancels
14. Edit mode pre-populates all fields with current customer data on initial load

**Prerequisites:** Stories 1.2, 3.1, 3.3, Stories 2.2, 2.5 (APIs)

---

### Story 3.7: Reusable Vaadin Form Components and Validation Library

**As a** Frontend Developer,
**I want** reusable Vaadin form components and validation utilities,
**So that** forms provide consistent validation and user experience across all pages.

**Acceptance Criteria:**

1. Custom Vaadin form component classes created (Java/Vaadin):
   - **ValidatedTextField** (extends TextField)
     - Properties: label, placeholder, value, required, pattern, maxLength
     - Real-time validation feedback with error message below field (red text)
     - Visual error indicator (red border on validation error)
     - Required asterisk (*) displayed in label
     - Helper text (optional, displayed below field)
   - **ValidatedEmailField** (extends EmailField)
     - Built-in email format validation
     - Async validation for email uniqueness (calls API)
     - Shows "checking..." spinner during async validation
     - Error message if email already exists
   - **ValidatedPhoneField** (extends TextField)
     - Phone format validation (regex pattern: +1-555-0123)
     - Auto-formatting as user types (DP: (123) 456-7890)
     - Optional field support
   - **ValidatedDatePicker** (extends DatePicker)
     - Date format validation
     - Age calculation validation (e.g., must be 18+)
     - Min/max date constraints
     - Shows age when date selected (e.g., "Age: 35")
   - **ValidatedComboBox** (extends ComboBox)
     - Dropdown list with validation
     - Required field support
     - Search/filter within list
   - **FormErrorNotification** component
     - Displays form-level errors above form
     - Red background with error icon, dismissible
   - **SuccessNotification** component
     - Displays success messages
     - Green background with checkmark icon
     - Auto-dismiss after 3 seconds
   - **ConfirmDialog** component (extends Vaadin Dialog)
     - Modal dialog for destructive actions
     - Warning icon, title, message
     - "Cancel" and "Delete/Confirm" buttons
     - Requires explicit confirmation before action
   - **LoadingOverlay** component
     - Centered spinner with optional backdrop overlay
     - Shows during API calls or long operations
2. All components follow Material Design 3 design system (Vaadin Material theme: colors, spacing, typography)
3. Components support responsive layout (mobile, tablet, desktop breakpoints)
4. Accessibility: ARIA labels on all components, semantic HTML, keyboard navigation support
5. Validation error messages are clear and actionable (e.g., "Email is required", not "email null")
6. Components tested with JUnit 5 + Vaadin TestBench (or manual testing)

**Prerequisites:** Stories 1.2, 3.1

---

### Story 3.8: Backend API Service Layer (Spring Boot)

**As a** Frontend Developer,
**I want** a well-structured backend service layer that handles API calls with proper error handling,
**So that** Vaadin UI components can easily call APIs without repetitive boilerplate.

**Acceptance Criteria:**

1. HTTP client service created in Spring Boot:
   - **RestTemplate** or **WebClient** configured for HTTP calls (prefer WebClient for async)
   - Methods for standard CRUD operations:
     - `get(url, responseType): T`
     - `post(url, body, responseType): T`
     - `put(url, body, responseType): T`
     - `delete(url): void`
2. Authentication handling:
   - Automatically attaches Spring Security principal (authenticated user) to requests
   - If using JWT tokens (post-MVP), attaches `Authorization: Bearer {token}` header
   - Session-based authentication used currently (Spring Security session)
3. Response and error handling:
   - 2xx: Return parsed JSON response object
   - 4xx: Parse error details and throw custom exception with field-level details
   - 5xx: Throw exception with error message
4. Retry logic for transient failures (with Resilience4j):
   - Max 3 retries with exponential backoff (100ms, 200ms, 400ms)
   - Retry on: 408 Request Timeout, 429 Too Many Requests, 5xx errors
   - Don't retry on: 4xx validation errors
5. Correlation ID handling:
   - Generate or propagate X-Trace-Id header in all requests (UUID)
   - Used for distributed tracing across services
6. Timeout handling:
   - Default timeout: 15 seconds (configurable)
   - Timeout exceptions caught and handled with user-friendly message
7. Domain service classes (wrappers around HTTP client):
   - **CustomerService**:
     - `createCustomer(dto: CreateCustomerRequest): Customer`
     - `getCustomer(id: UUID): Customer`
     - `searchCustomers(query, status, limit, offset): Page<Customer>`
     - `updateCustomer(id: UUID, dto: UpdateCustomerRequest): Customer`
     - `deleteCustomer(id: UUID): void`
   - **PolicyService** (placeholder for future use):
     - Similar methods for policy operations
8. Error mapping to user-friendly messages:
   - 409 Conflict (duplicate email) → "A customer with this email already exists"
   - 400 Bad Request (validation) → Show field-level errors
   - 404 Not Found → "Customer not found"
   - Network errors → "No internet connection, please check your network"
   - 5xx errors → "Server error, please try again later"
9. Logging:
   - Log all API calls in development mode (method, URL, status, timing)
   - Log errors with full context (request, response, stack trace)
10. Vaadin component integration example:
    ```java
    Customer customer = customerService.getCustomer(customerId);
    // OR for async:
    customerService.getCustomerAsync(customerId).thenAccept(customer -> {
      // update UI
    });
    ```

**Prerequisites:** Stories 1.2, 3.1, 3.2, Stories 2.1-2.8 (APIs)

---

### Story 3.9: Test Data Setup and Seed Database

**As a** QA Engineer,
**I want** to populate the database with realistic test customer and policy data,
**So that** I can develop and test customer/policy features without manual data entry.

**Acceptance Criteria:**

1. Repeatable Flyway migrations (R__) created for seeding test data
2. Test data includes 17+ realistic customers with diverse demographics:
   - Mixed names (Jane Smith, John Smith, Sarah Johnson, etc.)
   - International names for Unicode testing (Anna Müller, Pierre Dupont, Maria Rossi, Carlos Hernandez)
   - Email addresses use @example.com domain (never production)
   - Phone numbers in E.164 format (+1-555-0XXX)
   - Date of birth values ensure all customers are 18+ years old
   - All customers marked ACTIVE status by default
   - Addresses in various Oregon cities for geographic diversity
3. UPSERT pattern (ON CONFLICT ... DO UPDATE) ensures fresh data on each application startup
4. Seeds preserve created_at timestamp from original insert, update updated_at on each run
5. Seed data accessible via GET /api/v1/customers - search returns expected customers
6. Seed includes at least 2 customers with last name "Smith" for search testing
7. Optional: Seed policy data if policy APIs exist (defer if not)
8. Migration is idempotent - running multiple times produces consistent data state
9. Test data clearly separated from production data (email domain, user IDs)
10. Seed data documented in README with verification queries

**Prerequisites:** Stories 1.2, 3.1, Stories 2.1-2.4 (APIs)

---

### Story 3.10: Customer List Overview Page with Vaadin Grid

**As a** Customer Service Agent,
**I want** a consolidated dashboard showing all active customers with key metrics,
**So that** I can quickly understand workload and customer distribution.

**Acceptance Criteria:**

1. CustomerListView created at route `/customers/overview` (alternative to detailed search page)
2. Displays Vaadin Grid with all active customers:
   - Columns: ID (truncated), Name, Email, City, Status Badge, Created Date
   - Sortable columns (click header to toggle sort direction)
   - Built-in pagination (25/50/100 rows per page)
   - Page navigation (Previous/Next, "Page X of Y")
3. Summary metrics bar at top:
   - Total active customers count
   - Total customers by status breakdown (pie chart or horizontal bar chart)
   - Last updated timestamp
4. Quick filters above grid:
   - Filter by status (All / Active / Inactive)
   - Filter by city (ComboBox with state list)
5. Row actions (action column):
   - View → navigates to `/customers/{id}`
   - Edit → navigates to `/customers/{id}/edit`
   - Delete (context menu or vertical actions button)
6. Bulk actions (optional, checkbox selection):
   - Delete multiple selected customers
   - Mark as inactive for multiple customers
7. Empty state message if no customers found
8. Loading overlay while fetching data
9. Responsive design (works on mobile/tablet/desktop)
10. Keyboard navigation (arrow keys to navigate rows, Enter to view detail)

**Prerequisites:** Stories 1.2, 3.1, 3.3, Stories 2.3-2.4 (APIs)

---

### Story 3.11: Unified Customer Management Interface with SSC1-Style UX

**As a** Customer Service Agent used to the legacy CICS SSC1 terminal interface,
**I want** a single unified Vaadin view for all customer operations (search, create, edit, view) with terminal-style keyboard shortcuts,
**So that** I can perform customer management tasks without navigating between multiple pages and maintain muscle memory from the 3270 terminal interface.

**Acceptance Criteria:**

1. UnifiedCustomerView created at route `/customers/dashboard` - single consolidated interface combining:
   - Customer search/lookup with quick find field
   - View customer details (in context)
   - Create new customer (toggle to form mode)
   - Edit existing customer (inline or side panel)
   - Delete customer (with confirmation)
   - View customer history/audit trail

2. Layout design (single-page, context-aware):
   - Left panel: Search/lookup field + customer list grid (responsive, ~30% width)
   - Right panel: Detail view or form (toggle based on context, ~70% width)
   - Top action bar with quick commands
   - Status bar at bottom showing current mode and available shortcuts

3. Customer search/quick find:
   - Search field with focus-steal behavior (Ctrl+F to focus)
   - Real-time search as user types (debounce 300ms)
   - Results shown in left panel grid (ID, Name, Email, Status)
   - Click row to load customer details in right panel
   - Search history (last 5 searches, accessible via dropdown)

4. Keyboard shortcuts (SSC1-style terminal shortcuts):
   - **Ctrl+A** → Add new customer (focus to create form)
   - **Ctrl+E** → Edit selected customer
   - **Ctrl+S** → Search/focus search field
   - **Ctrl+D** → Delete selected customer (with confirmation)
   - **Ctrl+L** → Clear search / reset to customer list view
   - **Ctrl+?** → Show keyboard shortcut help (modal dialog)
   - **Escape** → Cancel current operation / return to list view
   - **Arrow Up/Down** → Navigate customer list (when focus in list)
   - **Enter** → Select/open customer from list

5. Right panel modes (context-aware):
   - **View Mode** (default after selecting customer):
     - Display customer details (read-only)
     - Buttons: Edit, Delete, Audit Trail
     - Show related policies (grid)
   - **Edit Mode**:
     - Form with validated fields (phone, email, DOB, address)
     - In-place editing (no modal dialog, integrated in right panel)
     - Save/Cancel buttons
     - Validation errors displayed inline
   - **Create Mode** (triggered by Ctrl+A):
     - Empty form with all customer fields
     - Save/Cancel buttons
     - Clear focus on successful save, ready for next customer

6. Customer list grid features:
   - Display: ID (first 8 chars), Name, Email, Status badge
   - Highlight selected row (background color change)
   - Show count: "X customers found"
   - Support sorting by Name, Email, Status
   - Sticky header (scrolls with content)

7. Form validation and error handling:
   - Real-time validation (as user types)
   - Field-level error messages (red text below field)
   - Required field indicators (red asterisk *)
   - Async validation for email uniqueness (show spinner)
   - Form-level error notification (red banner at top)
   - Success notification on save (green banner)

8. Status indicators and UX feedback:
   - "Mode: View / Edit / Create" label in status bar
   - "X matches found" in search results
   - Loading spinner while fetching
   - "Saved" confirmation (3s toast notification)
   - Unsaved changes warning if user tries to navigate away

9. Mobile responsive design:
   - Stack layout: search above, details below (single column on mobile)
   - Touch-friendly buttons and grid rows
   - Simplified keyboard shortcuts (Ctrl+X may not work on mobile, fall back to buttons)

10. Accessibility:
    - ARIA labels on all interactive elements
    - Keyboard navigation throughout (Tab, Shift+Tab, arrow keys)
    - Screen reader friendly (semantic HTML, proper heading hierarchy)
    - High contrast mode support

11. Performance:
    - Customer list loads within 2 seconds
    - Search results update in < 500ms (with debouncing)
    - Pagination prevents loading entire customer table

12. Testing:
    - Unit tests for search logic and filtering
    - Manual testing of all keyboard shortcuts
    - Test with screen reader (NVDA or JAWS)
    - Test on mobile devices (iOS Safari, Chrome Android)

**Dev Notes:**

This story represents a significant UX pivot from the current multi-page approach (separate search, detail, create, edit pages) to a unified SSC1-inspired interface. The goal is to minimize page navigation and provide power-user experience through keyboard shortcuts, similar to the legacy CICS 3270 terminal interface that customers are already familiar with.

Key architectural decisions:
- Single Vaadin View with mode switching (not multiple routes)
- Left panel uses Grid, right panel uses FormLayout or read-only display
- Keyboard event handlers registered globally to Vaadin UI
- Context maintained in View state (selectedCustomer, currentMode)
- API calls async (no blocking)

Implementation approach:
- Combine existing CustomerListView (grid) and CustomerDetailView (form) into single UnifiedCustomerView
- Add keyboard shortcut handler using Vaadin's keyboard events
- Refactor to use side-panel layout instead of full-page navigation
- Preserve all validation and error handling from existing components

Related Stories:
- Reuse components from Stories 3.4, 3.5, 3.6 (avoid duplication)
- Reuse API services from Story 3.8 (CustomerService)
- Apply validation patterns from Story 3.7 (ValidatedTextField, etc.)

Potential Extensions (post-MVP):
- Customer bulk operations (multi-select, bulk delete, status change)
- Advanced search filters (date range, address search, etc.)
- Customer history panel (audit trail in side panel)
- Policy linking UI (assign policies to customer in same view)
- Favorites/recent customers (quick access)

**Prerequisites:** Stories 3.4-3.8 (existing search/detail/edit/form/services components)

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

## EPIC 5: Policy Management API & Vaadin UI

**Expanded Goal:**

Complete the modernized application by implementing the full policy management lifecycle in Spring Boot + Vaadin, supporting all 4 policy types (Motor, Endowment, House, Commercial). This epic creates the policy domain models with type-specific attributes, implements policy CRUD APIs with comprehensive validation, builds Vaadin UI for policy management including a type-specific create wizard, and integrates policies with customer management. Upon completion, the application achieves full feature parity with legacy system, agents can perform all customer and policy operations via web UI, and the system is ready for production traffic cutover. This epic builds on the proven patterns and infrastructure established in Epic 3.

**Story Count:** 10 stories | **Estimated Duration:** Weeks 19-24

**Key Principle:** Policy management scales the patterns proven in Epic 3 (customer management). By this point, the Vaadin framework, Spring Boot backend, form validation components, and API service layer are all stable and reusable. This epic focuses on domain-specific logic (policy types, status transitions, validations) rather than framework/infrastructure concerns.

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

### Story 5.7: Policy Detail Page with Vaadin Display Components

**As a** Customer Service Agent,
**I want** to view and edit policy details in the web UI,
**So that** I can manage policies via the new system.

**Acceptance Criteria:**

1. PolicyDetailView created at route `/policies/{policyId}` (Vaadin @Route("/policies/:policyId"))
2. Detail view (read-only) displays using FormLayout:
   - Policy number, type, status (Badge component)
   - Linked customer (clickable link to `/customers/{customerId}`)
   - Start/end dates, premium amount
   - Type-specific attributes displayed appropriately:
     - **MotorPolicy:** registration number, engine type, vehicle value, driver age
     - **EndowmentPolicy:** sum assured, term, maturity amount
     - **HousePolicy:** property address, property value, covered risks (as comma-separated list)
     - **CommercialPolicy:** business type, annual revenue, number of employees
   - Notes (multi-line text)
   - Created/updated timestamps and user information
   - Linked customer section (card showing customer name, email, phone)
3. Edit mode (toggled by Edit button):
   - Form fields populate with current values
   - Editable fields: premium amount, notes, type-specific attributes
   - Immutable fields: policy number, customer, policy type, dates
   - Real-time validation (inherited from Story 3.7 components)
   - Save/Cancel buttons
4. Status change section:
   - Dropdown showing available transitions based on current status
   - Confirmation dialog before status change
   - API call to PATCH /api/v1/policies/{policyId}/status
5. Messages:
   - Success toast on update: "Policy updated successfully"
   - Error notification on failure with retry option
6. Loading state: Vaadin Spinner overlay during fetch/save
7. 404 error handling: "Policy not found" message if policy doesn't exist
8. Edit mode pre-populates all fields with current policy data on view load

**Prerequisites:** Stories 1.2, 3.1, 5.1, 5.4, 5.5

---

### Story 5.8: Policy Create Wizard with Vaadin - Type-Specific Forms

**As a** Customer Service Agent,
**I want** to create a new policy using a guided wizard with type-specific forms,
**So that** I can create policies without making mistakes.

**Acceptance Criteria:**

1. PolicyCreateView created at route `/policies/create` (Vaadin @Route("/policies/create"))
2. Multi-step form using Vaadin Stepper or custom step navigation:
   - **Step 1:** Select customer (search field + Grid of results, click to select)
   - **Step 2:** Select policy type (RadioButtonGroup: Motor / Endowment / House / Commercial)
   - **Step 3:** Basic policy info (startDate, endDate, premiumAmount, notes)
   - **Step 4:** Type-specific details (form changes based on selected type)
   - **Step 5:** Review and confirm
3. Type-specific forms in Step 4 (using ValidatedTextField, ComboBox, DatePicker from Story 3.7):
   - **MotorPolicy:**
     - registrationNumber (TextField, required, pattern: ^[A-Z]{2}\d{2}[A-Z]{3}$)
     - engineType (ComboBox, required, options: PETROL, DIESEL, ELECTRIC, HYBRID)
     - vehicleValue (NumberField, required, > 0)
     - driverAge (NumberField, required, 18-100)
   - **EndowmentPolicy:**
     - sumAssured (NumberField, required, > 0)
     - term (NumberField, required, 1-40 years)
     - maturityAmount (NumberField, required, >= sumAssured)
   - **HousePolicy:**
     - propertyAddress (TextField, required)
     - propertyValue (NumberField, required, > 0)
     - coveredRisks (CheckboxGroup, required, at least 1 selected: fire, theft, flood, earthquake)
   - **CommercialPolicy:**
     - businessType (TextField, required)
     - annualRevenue (NumberField, required, > 0)
     - numberOfEmployees (NumberField, required, >= 1)
4. Form validation:
   - Real-time field validation feedback (red borders, error messages)
   - Step-level validation: Next button disabled if current step has errors
   - Type-specific validation rules enforced (see Story 5.3)
5. Navigation: Next/Previous buttons, progress indicator (e.g., "Step 3 of 5", progress bar)
6. Review step displays all entered data in read-only format:
   - Sections: Customer, Policy Type, Basic Info, Type-Specific Details
   - "Edit" button next to each section allows returning to that step
7. Confirm button:
   - Creates policy via API: POST /api/v1/policies
   - Sends full payload with all customer, policy, and type-specific data
   - Button disabled during submission
8. Success notification with policy number and options:
   - "View Policy" → navigates to `/policies/{policyId}`
   - "Create Another" → resets form to Step 1 (create mode)
   - "Go to Dashboard" → navigates to `/dashboard`
9. Error handling:
   - Validation errors: highlight field, show message
   - Customer not found: show error in Step 1, allow re-search
   - Server error: show notification with retry option
10. Edit mode pre-populates form if editing existing policy (future enhancement)

**Prerequisites:** Stories 1.2, 3.1, 3.6, 3.7, 5.1, 5.2, 5.3

---

### Story 5.9: Policy-to-Customer Linking UI Integration

**As a** Customer Service Agent,
**I want** to see linked policies on customer detail page and linked customer on policy page,
**So that** I can navigate between related entities seamlessly.

**Acceptance Criteria:**

1. Customer detail page (Story 3.5) includes "Linked Policies" section:
   - Vaadin Grid showing policies for this customer
   - Columns: Policy Number, Type, Status, Start Date, Actions
   - "View" action button → navigates to `/policies/{policyId}`
   - "Edit" action button → navigates to `/policies/{policyId}/edit`
   - "Create New Policy" button → navigates to `/policies/create?customerId={customerId}`
   - Pre-selects this customer in Step 1 of the create wizard
2. Policy detail page (Story 5.7) includes "Linked Customer" section:
   - Customer card component showing: Name, Email, Phone
   - Clickable link to customer → navigates to `/customers/{customerId}`
   - "Edit Customer" button (if user has permission) → navigates to `/customers/{customerId}/edit`
3. Policy create wizard (Story 5.8) customer selection:
   - When creating policy from customer detail page (Step 1):
     - Customer pre-selected and displayed
     - Allow changing customer via search if needed
   - Search functionality filters customers by name, email, phone
4. Navigation consistency:
   - Full round-trip navigation works: Customer → View Policy → View Customer
   - No dead links or missing references
   - Both directions (customer→policies and policy→customer) functional

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
