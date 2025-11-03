# Story 2.1: Customer Domain Model and PostgreSQL Schema

Status: ready-for-dev

## Story

As a Backend Developer,
I want to define the Customer domain model and create the PostgreSQL schema,
So that I have a foundation for implementing customer CRUD operations.

## Acceptance Criteria

1. Customer JPA entity created with fields: customerId (UUID, primary key), firstName, lastName (String, required, 1-100 characters), dateOfBirth (LocalDate, optional), email (String, required, unique, email format), phone (String, optional, phone format), address, city, state, zipCode (String, optional), status (enum: ACTIVE, INACTIVE), createdAt, updatedAt (LocalDateTime, auto-managed via JPA @EntityListeners), createdBy, updatedBy (String, populated from JWT token)
2. PostgreSQL table `CUSTOMER` created with: Columns matching entity fields, Indexes: email, phone, status, createdAt, Constraints: email UNIQUE, NOT NULL on required fields
3. Flyway migration script created (V2__create_customer_table.sql) and tested
4. JPA repository interface created: `CustomerRepository extends JpaRepository<Customer, UUID>` with custom finder methods: findByEmail(String email), findByPhone(String phone), findByLastNameContainingIgnoreCase(String lastName), findByStatusAndCreatedAtAfter(Status status, LocalDateTime date) for reporting
5. Entity validation annotations applied: @NotNull on required fields, @Email on email field, @Pattern(regexp="...") for phone format (international format support), @Min(18) @Max(150) for age validation
6. Audit columns (createdAt, updatedAt, createdBy, updatedBy) auto-managed
7. Schema tested with sample data insert/select

## Tasks / Subtasks

- [ ] Task 1: Create Customer JPA entity class (AC: #1, #5, #6)
  - [ ] Create `src/main/java/com/example/cicsgenapp/entity/Customer.java` with all required fields
  - [ ] Annotate with `@Entity`, `@Table(name = "CUSTOMER")`
  - [ ] Add validation annotations: @NotNull, @Email, @Pattern for phone
  - [ ] Implement EntityListener for audit column management (createdAt, updatedAt)
  - [ ] Add @Column annotations with appropriate lengths (firstName/lastName: 1-100, email: 254, phone: 20)
  - [ ] Add getter/setter methods (or use Lombok @Getter/@Setter)
  - [ ] Add equals() and hashCode() for UUID-based comparison
  - [ ] Add toString() for debugging

- [ ] Task 2: Create PostgreSQL schema migration script (AC: #2, #3)
  - [ ] Create `src/main/resources/db/migration/V2__create_customer_table.sql`
  - [ ] Define CUSTOMER table with columns:
    - customerId UUID PRIMARY KEY (generate via uuid_generate_v4() or App)
    - firstName VARCHAR(100) NOT NULL
    - lastName VARCHAR(100) NOT NULL
    - dateOfBirth DATE
    - email VARCHAR(254) NOT NULL UNIQUE
    - phone VARCHAR(20)
    - address VARCHAR(255)
    - city VARCHAR(100)
    - state VARCHAR(2)
    - zipCode VARCHAR(10)
    - status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
    - createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    - updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    - createdBy VARCHAR(255)
    - updatedBy VARCHAR(255)
  - [ ] Create indexes: CREATE INDEX idx_customer_email ON CUSTOMER(email)
  - [ ] Create indexes: CREATE INDEX idx_customer_phone ON CUSTOMER(phone)
  - [ ] Create indexes: CREATE INDEX idx_customer_status ON CUSTOMER(status)
  - [ ] Create indexes: CREATE INDEX idx_customer_createdAt ON CUSTOMER(createdAt)
  - [ ] Test migration script locally against PostgreSQL test instance

- [ ] Task 3: Create CustomerRepository interface (AC: #4)
  - [ ] Create `src/main/java/com/example/cicsgenapp/repository/CustomerRepository.java`
  - [ ] Extend JpaRepository<Customer, UUID>
  - [ ] Add method: findByEmail(String email) with @Query annotation if needed
  - [ ] Add method: findByPhone(String phone)
  - [ ] Add method: findByLastNameContainingIgnoreCase(String lastName)
  - [ ] Add method: findByStatusAndCreatedAtAfter(Status status, LocalDateTime date) for reporting queries
  - [ ] Document each method with JavaDoc

- [ ] Task 4: Configure Flyway migration execution (AC: #3)
  - [ ] Update pom.xml: Add flyway-core dependency (latest stable)
  - [ ] Add flyway-maven-plugin for `mvn flyway:migrate` command
  - [ ] Update application.yml: Configure spring.flyway.locations = "classpath:db/migration"
  - [ ] Configure spring.flyway.baselineOnMigrate = true (for fresh databases)
  - [ ] Test locally: Run mvn flyway:migrate and verify V1__initial_schema.sql and V2__create_customer_table.sql execute in order

- [ ] Task 5: Implement JPA entity listener for audit column management (AC: #6)
  - [ ] Create `src/main/java/com/example/cicsgenapp/listener/AuditListener.java`
  - [ ] Implement @PrePersist: Set createdAt, updatedAt to current time; set createdBy/updatedBy from JWT (via SecurityContextHolder)
  - [ ] Implement @PreUpdate: Update updatedAt to current time; update updatedBy from JWT
  - [ ] Register listener in Customer entity: @EntityListeners(AuditListener.class)
  - [ ] Test audit columns are populated correctly

- [ ] Task 6: Test schema creation and sample data (AC: #7)
  - [ ] Unit test: Create Customer instance, verify all fields can be set
  - [ ] Unit test: Validate Customer entity with valid data passes validation
  - [ ] Unit test: Validate Customer entity with invalid email format fails validation
  - [ ] Unit test: Validate Customer entity with phone < 7 digits fails validation
  - [ ] Integration test: Insert customer record into PostgreSQL via Flyway migration
  - [ ] Integration test: Insert sample customer Jane Smith (email: jane@example.com, phone: +1-555-0123)
  - [ ] Integration test: Query CUSTOMER table and verify Jane Smith is present
  - [ ] Integration test: Query by email and verify result is correct
  - [ ] Integration test: Query by phone and verify result is correct
  - [ ] Integration test: Query by last name and verify partial match works
  - [ ] Integration test: Verify all indexes exist in PostgreSQL

- [ ] Task 7: Document entity and schema structure (AC: #1, #2)
  - [ ] Create/update `docs/stories/2-1-customer-entity-guide.md` with:
    - Customer entity fields documentation
    - Data type mappings (Java → PostgreSQL)
    - Validation rules (email format, phone format, age calculation)
    - Index strategy explanation
    - Audit column auto-management explanation
    - Example JPA query usage
    - Spring Data examples (CustomerRepository usage)
  - [ ] Update story file with implementation notes

## Dev Notes

### Architecture Context

This story establishes the Customer domain model, the first vertical slice of Epic 2 (Customer Service API & Spring Boot Backend). The Customer entity is fundamental to the modernization: it replaces the legacy COBOL SSC1 transaction with a modern Spring Boot REST API.

**Design Decisions:**
- **UUID as primary key**: Allows distributed generation and better scalability (vs. auto-incrementing integers)
- **PostgreSQL as primary store**: Single source of truth (VSAM is legacy, Db2 will be synced via Debezium CDC)
- **JPA entity listeners for auditing**: Automatic capture of createdBy/updatedBy from JWT (security context)
- **Email as unique constraint**: Business rule: no duplicate email addresses
- **ACTIVE/INACTIVE status**: Soft-delete pattern (customers can be marked inactive but not hard-deleted)

**Relationship to Previous Stories:**
- Story 1.3 established PostgreSQL connectivity and Flyway migrations (V1__initial_schema.sql created infrastructure)
- Story 1.5 establishes JWT authentication with createdBy/updatedBy captured from JWT token
- This story (2.1) creates the first business domain entity (V2__create_customer_table.sql)

### Technical Requirements for This Story

1. **JPA/Hibernate**: Entity mapping with annotations, relationship definitions
2. **PostgreSQL**: UUID data type (via pgcrypto extension), indexing strategy
3. **Flyway**: Database migration versioning (V1, V2, etc.)
4. **Spring Data JPA**: Repository pattern, query methods
5. **Bean Validation**: @NotNull, @Email, @Pattern annotations for input validation
6. **Audit Trail**: Automatic capture of createdAt, updatedAt, createdBy, updatedBy

### Constraints & Requirements

- **Java 17 LTS**: Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS**: Already established (Story 1.2)
- **Spring Data JPA**: Latest stable
- **PostgreSQL 15+**: Established in Story 1.3
- **Flyway**: For schema versioning
- **Email uniqueness**: Business requirement (no duplicate emails)
- **Age validation**: Implicit (dateOfBirth with @Min/@Max on calculated age)

### Testing Standards Summary

Story 2.1 requires validation of entity modeling and schema correctness:
- **Unit Tests**: Entity validation, bean validation annotations, audit listener behavior
- **Integration Tests**: Flyway migration execution, schema creation, INSERT/SELECT queries, index presence
- **Query Tests**: Custom repository methods (findByEmail, findByLastName, etc.)

Target: 85%+ test coverage for entity and repository classes

**Testing Subtasks in Task 6** include:
- Entity creation and field assignment
- Validation with valid/invalid data (email format, phone format)
- Flyway migration execution verification
- Sample data insertion
- Query verification (findByEmail, findByPhone, findByLastName)
- Index presence verification in PostgreSQL

### Project Structure Notes

**Alignment with unified-project-structure.md** (from Story 1.1):
```
src/main/java/com/example/cicsgenapp/
├── entity/
│   └── Customer.java (NEW - this story)
├── repository/
│   └── CustomerRepository.java (NEW - this story)
└── listener/
    └── AuditListener.java (NEW - this story)

src/main/resources/db/migration/
├── V1__initial_schema.sql (Story 1.3)
└── V2__create_customer_table.sql (NEW - this story)

src/test/java/com/example/cicsgenapp/
├── entity/
│   └── CustomerEntityTest.java (NEW - validation tests)
├── repository/
│   └── CustomerRepositoryIntegrationTest.java (NEW - query tests)
└── listener/
    └── AuditListenerTest.java (NEW - audit column tests)
```

**Maven module**: `genapp-backend` (established Story 1.1)
**Build target**: `target/classes/com/example/cicsgenapp/entity/Customer.class` after compile

### References

- [Spring Data JPA Reference Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate Entity Mapping](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#entity)
- [PostgreSQL UUID Type](https://www.postgresql.org/docs/current/datatype-uuid.html)
- [Flyway Database Migrations](https://flywaydb.org/documentation/)
- [Bean Validation (Jakarta Validation)](https://beanvalidation.org/)
- [Source: docs/epics.md#epic-2-customer-service-api--spring-boot-backend Story 2.1]
- [Source: docs/architecture.md - Architectural Layers section for context]

## Dev Agent Record

### Context Reference

- docs/stories/2-1-customer-domain-model-and-postgresql-schema.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

- 2025-11-03: Story 2.1 CREATED from Epic 2 (first story in customer service epic)
- 2025-11-03: Previous story learnings extracted from Story 1.4 (completed)

### Completion Notes List

*To be filled by dev agent during implementation*

### File List

*To be filled by dev agent during implementation*

## Change Log

- **2025-11-03 [14:00 UTC]:** Story 2.1 DRAFTED - Customer Domain Model and PostgreSQL Schema (first story in Epic 2)

---

## Prerequisites

- Story 1.3: PostgreSQL Database Connectivity and Schema Management (✓ COMPLETED)

## Story Type

Domain Modeling & Database Schema

## Story Points (Estimate)

8 points

## Epic

Epic 2: Customer Service API & Spring Boot Backend
