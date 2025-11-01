# Story 1.3: PostgreSQL Database Connectivity and Schema Management

Status: ready-for-dev

## Story

As a Database Administrator,
I want the Spring Boot application to connect to PostgreSQL with connection pooling and schema versioning,
So that the application can reliably store and retrieve data with proper migrations.

## Acceptance Criteria

1. PostgreSQL JDBC driver configured in Spring Boot (latest version, 15+ compatibility)
2. Connection pool configured (HikariCP: 10 min, 20 max connections, 30s timeout)
3. Spring Data JPA configured with PostgreSQL dialect
4. Flyway migration tool integrated for schema versioning (V1__initial_schema.sql, etc.)
5. Initial schema migration script created (CUSTOMER, POLICY, AUDIT_LOG, FEATURE_TOGGLE tables with indexes)
6. Environment-specific PostgreSQL connection strings configured (dev uses local, test uses testcontainer, prod uses managed DB)
7. Connection health check working via Spring Actuator: GET /actuator/health/db returns {"status":"UP","details":{"database":"PostgreSQL"}}
8. Database connection tested with successful query execution (SELECT 1)
9. Docker Compose includes PostgreSQL service for local development

## Tasks / Subtasks

- [ ] Task 1: Add PostgreSQL JDBC driver and Flyway to pom.xml (AC: #1, #4)
  - [ ] Add org.postgresql:postgresql dependency (latest 42.x)
  - [ ] Add org.flywaydb:flyway-core dependency
  - [ ] Add org.springframework.boot:spring-boot-starter-data-jpa dependency if not present
  - [ ] Verify all dependencies resolve without conflicts
  - [ ] Update parent Spring Boot version if needed for compatibility

- [ ] Task 2: Configure PostgreSQL connection pool in application.yml (AC: #2, #6)
  - [ ] Update application-dev.yml with PostgreSQL dev connection string (localhost:5432, genapp_dev)
  - [ ] Update application-test.yml with TestContainers PostgreSQL configuration
  - [ ] Update application-prod.yml with managed PostgreSQL endpoint placeholder
  - [ ] Configure HikariCP pool: minimum-idle: 10, maximum-pool-size: 20, connection-timeout: 30000ms (dev)
  - [ ] Configure prod pool with production-safe settings: minimum-idle: 5, maximum-pool-size: 20
  - [ ] Set connection-test-query: "SELECT 1" for validation
  - [ ] Document connection string format and environment variables for Docker

- [ ] Task 3: Configure Flyway migrations directory structure (AC: #4)
  - [ ] Create src/main/resources/db/migration/ directory
  - [ ] Create src/test/resources/db/migration/ directory (separate for test fixtures)
  - [ ] Configure Flyway in application.yml files:
    - locations: classpath:db/migration
    - out-of-order: false (enforce migration order)
    - validate-on-migrate: true
  - [ ] Document migration naming convention: V{number}__{description}.sql

- [ ] Task 4: Create initial database schema migration script (AC: #5)
  - [ ] Create src/main/resources/db/migration/V1__initial_schema.sql with:
    - CUSTOMER table (customerId UUID PK, firstName, lastName, email UNIQUE, phone, address, city, state, zipCode, status, createdAt, updatedAt, createdBy, updatedBy)
    - Indexes on email, phone, status, createdAt
    - POLICY table (policyId UUID PK, customerId FK, policyNumber UNIQUE, policyType, status, startDate, endDate, premiumAmount, notes, createdAt, updatedAt, createdBy, updatedBy)
    - Indexes on customerId, policyNumber, status, startDate, policyType
    - AUDIT_LOG table (auditId UUID PK, timestamp, userId, operation ENUM, entityType, entityId, changes JSONB, ipAddress, userAgent)
    - Indexes on entityType, entityId, timestamp
    - FEATURE_TOGGLE table (toggleId UUID PK, toggleName UNIQUE, toggleState BOOLEAN, lastChanged, changedBy, reason TEXT)
  - [ ] Add foreign key constraints: POLICY.customerId → CUSTOMER.customerId
  - [ ] Add NOT NULL constraints on required fields
  - [ ] Add column constraints: email length, phone format, premiumAmount > 0
  - [ ] Add CREATE INDEX statements for performance
  - [ ] Document schema in comments within SQL file

- [ ] Task 5: Configure JPA entity mapping for PostgreSQL (AC: #3)
  - [ ] Verify application.yml contains: spring.jpa.database-platform: org.hibernate.dialect.PostgreSQLDialect
  - [ ] Verify spring.jpa.hibernate.ddl-auto is set appropriately per profile:
    - dev: create-drop (for development testing)
    - test: create-drop (TestContainers databases are ephemeral)
    - prod: validate (migrations only, no DDL auto)
  - [ ] Set spring.jpa.show-sql: false for production
  - [ ] Set spring.jpa.properties.hibernate.format_sql: true for dev
  - [ ] Verify open-in-view: false (from Story 1.2)
  - [ ] Create sample JPA entities mapping if needed (or defer to Story 2.1)

- [ ] Task 6: Test PostgreSQL connection in dev and test environments (AC: #6, #7, #8)
  - [ ] Start local PostgreSQL via docker-compose (from Story 1.1 setup)
  - [ ] Run: mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
  - [ ] Verify logs show: "HikariPool-1 - Starting" and "Tomcat started on port 8080"
  - [ ] Test health endpoint: curl http://localhost:8080/actuator/health
  - [ ] Verify response includes: {"status":"UP","components":{"db":{"status":"UP","details":{"database":"PostgreSQL"}}}}
  - [ ] Test direct connection: curl http://localhost:8080/actuator/health/db
  - [ ] Verify Flyway migration runs: logs should show "Successfully validated 1 migration"
  - [ ] Run test suite: mvn test (verifies test profile with TestContainers works)

- [ ] Task 7: Update Docker Compose for PostgreSQL service (AC: #9)
  - [ ] Verify docker-compose.yml from Story 1.1 includes PostgreSQL service
  - [ ] Verify PostgreSQL image: postgres:16-alpine
  - [ ] Verify environment variables: POSTGRES_DB=genapp_dev, POSTGRES_USER=genapp, POSTGRES_PASSWORD=genapp_dev
  - [ ] Verify volume mount for persistence: ./postgres_data:/var/lib/postgresql/data
  - [ ] Verify port mapping: 5432:5432
  - [ ] Test: docker-compose up, wait for "database system is ready to accept connections", then curl health endpoint
  - [ ] Test: docker-compose down -v (cleanup)

- [ ] Task 8: Document database setup and migration process (AC: #6, #9)
  - [ ] Update README.md with PostgreSQL connection setup:
    - How to start PostgreSQL (docker-compose up)
    - How to run migrations (automatic on boot)
    - How to view migration status (Flyway output in logs)
  - [ ] Document environment variables for different profiles (dev/test/prod)
  - [ ] Add troubleshooting section: connection refused, authentication errors, migration failures
  - [ ] Document backup/restore procedures for dev database

- [ ] Task 9: Comprehensive testing of database connectivity (AC: 1-9)
  - [ ] Unit tests: Test PostgreSQL dialect configuration via ApplicationContextTests
  - [ ] Integration tests: Test Flyway migration execution with @SpringBootTest @ActiveProfiles("test")
  - [ ] Integration tests: Test HikariCP pool initialization via ConnectionPoolTests
  - [ ] Integration tests: Test /actuator/health endpoint includes db component
  - [ ] Run mvn clean install to verify full build succeeds
  - [ ] Test against real PostgreSQL instance (dev environment)
  - [ ] Test TestContainers PostgreSQL for test profile (verify T-SQL commands work)

## Dev Notes

### Architecture Context

This story extends Story 1.2 by establishing persistent data storage for the application. Where Story 1.2 configured Spring Framework features, Story 1.3 establishes the database layer and schema versioning pipeline.

**Learnings from Story 1.2:**
- Story 1.2 created the application framework with JPA configured for PostgreSQL dialect
- HikariCP connection pool was partially configured in application.yml (profiles defined)
- Flyway was mentioned in Task 3 but not yet fully implemented
- JDBC driver needs to be verified/added if missing
- Database schema (CUSTOMER, POLICY, AUDIT_LOG) needs to be created via migration scripts

**Key Pattern from Story 1.2:**
- Do NOT recreate existing files. The JPA dialect is already set to PostgreSQL.
- Enhance the configuration with Flyway, add the JDBC driver, and create migration scripts.
- Connection pool settings from Story 1.2 should align with this story's requirements.

### Technical Requirements for This Story

1. **PostgreSQL 16 LTS Driver** - JDBC driver for PostgreSQL 15+ compatibility
2. **Connection Pool (HikariCP)** - Already partially configured in Story 1.2, needs sizing per environment
3. **Flyway Schema Versioning** - Deterministic, repeatable schema management
4. **Database Schema** - Tables for Customer, Policy, Audit Log, Feature Toggles with indexes
5. **TestContainers** - Ephemeral PostgreSQL for test suite (integration testing)

### Constraints & Requirements

- **Java 21 LTS:** Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS:** Already established (Story 1.2)
- **PostgreSQL 16 LTS:** Primary database (matches Docker Compose service)
- **HikariCP:** Connection pooling (included in Spring Data JPA)
- **Flyway:** Schema versioning and migration
- **Port 5432:** Standard PostgreSQL port in Docker Compose

### Testing Standards Summary

Story 1.3 requires validation of database connectivity and schema setup:
- **Integration Tests**: ApplicationContext loading with PostgreSQL config, Flyway migration execution
- **Connection Pool Tests**: HikariCP initialization and pooling behavior
- **Health Check Tests**: /actuator/health/db endpoint validation
- **TestContainers Tests**: Verify test profile uses ephemeral PostgreSQL instances
- **Docker Compose Tests**: Verify docker-compose up provides working PostgreSQL

Target: 80%+ test coverage for database-specific code

### References

- [Spring Boot Database Initialization](https://docs.spring.io/spring-boot/docs/3.3.x/reference/html/howto.html#howto-database-initialization)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [TestContainers PostgreSQL Module](https://www.testcontainers.org/modules/databases/postgres/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP/wiki/Configuration)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)

## Dev Agent Record

### Context Reference

- docs/stories/1-3-postgresql-database-connectivity-and-schema-management.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

### Completion Notes List

### File List

## Change Log

- **2025-11-01 [16:00 UTC]:** Story 1.3 DRAFTED - PostgreSQL Database Connectivity and Schema Management

## Status

drafted

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.1: Project Repository Setup and Developer Environment (✓ COMPLETED)

## Story Type

Infrastructure & Data Layer Setup

## Story Points (Estimate)

13 points
