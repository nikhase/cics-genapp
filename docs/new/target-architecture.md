# CICS GenApp Cloud Modernization - Architecture Decision Document

**Author:** Niklas
**Date:** October 31, 2025
**Project Level:** Level 3 (Enterprise Cloud Modernization)
**Architecture Paradigm:** Strangler Pattern with Cloud-Native Foundation

---

## Executive Summary

CICS GenApp Cloud Modernization adopts a **platform-agnostic, cloud-native architecture** using Spring Boot 3.3+ (Java 17+) for the backend and React 18 with Vite for the frontend. The system implements a **strangler pattern** with Spring Cloud Gateway for gradual traffic migration from legacy COBOL/CICS to modern services. PostgreSQL serves as the primary database, with Change Data Capture (CDC) via Debezium HTTP sink ensuring eventual consistency with legacy Db2 during the 12-month transition. Authentication is delegated to Zitadel (OIDC), eliminating password management complexity. The entire stack is containerized with Docker and orchestrated via Kubernetes, ensuring portability across any cloud provider or on-premises environment.

**Key Architectural Principles:**

- ✅ **Platform-agnostic** (no vendor lock-in; AWS/Azure/GCP/on-prem ready)
- ✅ **Event-driven data consistency** (CDC without Kafka - HTTP-based)
- ✅ **External authentication** (OIDC/Zitadel, zero password management)
- ✅ **Observable from day one** (structured logging, Prometheus metrics, Jaeger tracing)
- ✅ **AI-agent consistency** (implementation patterns prevent code conflicts)
- ✅ **Strangler pattern** (feature toggles control gradual traffic cutover)

---

## Technology Stack Decisions

| Component                   | Decision                     | Version     | Rationale                                                        | Affects Epics |
| --------------------------- | ---------------------------- | ----------- | ---------------------------------------------------------------- | ------------- |
| **Backend Framework**       | Spring Boot                  | 3.3+ LTS    | Modern, OIDC support, team ready, fast time-to-market            | 1, 2, 4, 5    |
| **Language**                | Java                         | 17+         | Spring Boot 3.3+ requirement; team experienced                   | 1, 2, 4, 5    |
| **Frontend Framework**      | React                        | 18.x        | SPA, team experienced, large ecosystem                           | 1, 3, 4, 5    |
| **Frontend Build Tool**     | Vite                         | 5.x         | 5x faster builds than Create React App                           | 3             |
| **Primary Database**        | PostgreSQL                   | 15+         | Modern, open-source, excellent Spring Boot integration           | 1, 2, 5       |
| **Legacy Database**         | Db2 (via CDC)                | existing    | Async sync via Debezium, not primary                             | 1, 4, 5       |
| **API Gateway**             | Spring Cloud Gateway         | 4.x         | Embedded in Spring Boot, native feature toggle support           | 1, 4          |
| **Feature Toggles**         | Unleash                      | 5.x         | Self-hosted, open-source, platform-agnostic                      | 1, 4          |
| **Authentication**          | OIDC (Zitadel)               | Self-hosted | External provider, eliminates password management                | 1, 3          |
| **Data Consistency**        | CDC (Debezium)               | 2.4+        | PostgreSQL WAL → HTTP callbacks → Db2 (async)                    | 4, 5          |
| **Message Transport**       | HTTP (no Kafka)              | REST        | Simpler than message broker, sufficient for eventual consistency | 4             |
| **Containerization**        | Docker                       | 24.x        | Standard container runtime, multi-stage builds                   | 1             |
| **Orchestration**           | Kubernetes                   | 1.28+       | Platform-agnostic, industry standard, self-healing               | 1             |
| **Package Manager**         | Helm                         | 3.x         | Kubernetes templating, environment-specific values               | 1             |
| **Log Aggregation**         | ELK Stack                    | 8.x         | ElasticSearch + Logstash + Kibana, open-source                   | 1, 6          |
| **Metrics**                 | Prometheus + Grafana         | Latest      | Time-series metrics, self-hosted, industry standard              | 1, 6          |
| **Distributed Tracing**     | Jaeger                       | 1.x         | Request flow tracing across services                             | 1, 6          |
| **API Documentation**       | SpringDoc-OpenAPI            | 2.x         | Auto-generates OpenAPI 3.0 spec                                  | 2, 5          |
| **Contract Testing**        | Pact                         | 12.x        | Consumer-driven API contracts                                    | 1, 2, 5       |
| **Unit Testing (Backend)**  | JUnit 5 + Mockito            | Latest      | Spring Boot standard, 80%+ coverage                              | All           |
| **Unit Testing (Frontend)** | Jest + React Testing Library | Latest      | Industry standard for React                                      | 3             |
| **Integration Testing**     | TestContainers               | 1.x         | Real PostgreSQL via Docker                                       | 2, 5          |

---

## Project Initialization

### Spring Boot Backend

```bash
mvn archetype:generate \
  -DgroupId=com.cicsgenapp \
  -DartifactId=cicsgenapp-backend \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd cicsgenapp-backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### React Frontend

```bash
npm create vite@latest cicsgenapp-frontend -- --template react-ts
cd cicsgenapp-frontend
npm install
npm run dev
```

---

## Backend Project Structure

```
cicsgenapp-backend/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── helm/
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
├── src/main/java/com/cicsgenapp/
│   ├── CicsGenAppApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java (OIDC/Zitadel)
│   │   ├── GatewayConfig.java (Spring Cloud Gateway)
│   │   ├── DatabaseConfig.java (PostgreSQL + Db2)
│   │   ├── AsyncConfig.java (CDC HTTP callbacks)
│   │   └── ObservabilityConfig.java (Logging, metrics)
│   ├── customer/
│   │   ├── controller/CustomerController.java
│   │   ├── service/CustomerService.java
│   │   ├── repository/CustomerRepository.java
│   │   └── entity/Customer.java
│   ├── policy/
│   │   ├── controller/PolicyController.java
│   │   ├── service/PolicyService.java
│   │   ├── repository/PolicyRepository.java (+ type-specific repos)
│   │   ├── validator/ (MotorPolicy, Endowment, House, Commercial)
│   │   └── entity/ (Policy + 4 type-specific entities)
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── service/OidcTokenService.java
│   │   └── filter/JwtAuthenticationFilter.java
│   ├── audit/
│   │   ├── controller/AuditController.java
│   │   ├── service/AuditService.java
│   │   ├── aspect/AuditAspect.java
│   │   └── entity/AuditLog.java
│   ├── integration/
│   │   ├── service/ (DualWriteService, DataValidationService, CircuitBreaker)
│   │   ├── controller/ChangeDataCaptureController.java (Debezium HTTP POST)
│   │   └── dto/ChangeDataCaptureEvent.java
│   ├── feature/
│   │   ├── controller/FeatureToggleController.java
│   │   ├── service/FeatureToggleService.java
│   │   ├── client/UnleashClient.java
│   │   └── entity/FeatureToggle.java
│   ├── common/
│   │   ├── error/ (Exceptions, GlobalExceptionHandler)
│   │   ├── logging/ (StructuredLogger, RequestLoggingFilter)
│   │   ├── validation/ (Email, Phone, Date validators)
│   │   └── security/SecurityUtils.java
│   └── reports/
│       ├── controller/ReportController.java
│       ├── service/ReportService.java
│       └── dto/ (CustomerSummary, PolicySummary)
├── src/main/resources/
│   ├── application.yml
│   ├── application-{dev,test,prod}.yml
│   ├── db/migration/ (Flyway/Liquibase scripts)
│   └── logback-spring.xml (JSON logging config)
├── src/test/java/ (mirror structure with Test/IntegrationTest/PactTest classes)
└── README.md
```

---

## Frontend Project Structure

```
cicsgenapp-frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── Dockerfile
├── docker-compose.yml
├── helm/
├── public/
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── pages/ (Login, Dashboard, CustomerSearch, CustomerDetail, CustomerCreate, PolicySearch, PolicyDetail, PolicyCreate, AuditLog, Reports, AdminConsole, ParallelRunValidation)
│   ├── components/
│   │   ├── common/ (Header, Sidebar, ErrorBoundary, LoadingSpinner)
│   │   ├── forms/ (TextInput, SelectInput, DatePicker, EmailInput, PhoneInput, FormError)
│   │   ├── feedback/ (SuccessAlert, ErrorAlert, ConfirmDialog)
│   │   ├── customer/ (SearchForm, List, DetailCard, EditForm, CreateWizard)
│   │   ├── policy/ (SearchForm, List, DetailCard, EditForm, CreateWizard)
│   │   └── admin/ (FeatureToggleManager, ParallelRunDashboard, ValidationMetrics)
│   ├── services/
│   │   ├── api/ (ApiClient, CustomerService, PolicyService, AuditService, AuthService, FeatureToggleService)
│   │   ├── state/ (authStore, customerStore, policyStore - Zustand)
│   │   └── utils/ (dateUtils, validationUtils, formatUtils)
│   ├── hooks/ (useAuth, useCustomerForm, usePolicyForm, useApi)
│   ├── types/ (Customer, Policy, Auth, Common)
│   ├── styles/ (theme - MUI Clarity, global.css)
│   ├── context/AuthContext.tsx
│   ├── __tests__/ (mirror component tests)
│   └── pact/ (customer.pact.ts, policy.pact.ts)
└── README.md
```

---

## Naming Conventions

### REST API Endpoints

```
POST   /api/v1/customers
GET    /api/v1/customers?query=smith&status=ACTIVE&limit=50
GET    /api/v1/customers/{id}
PUT    /api/v1/customers/{id}
DELETE /api/v1/customers/{id}

POST   /api/v1/policies
GET    /api/v1/policies?type=MOTOR&status=ACTIVE
GET    /api/v1/policies/{id}
PUT    /api/v1/policies/{id}
PATCH  /api/v1/policies/{id}/status
DELETE /api/v1/policies/{id}

POST   /api/v1/auth/callback
POST   /api/v1/auth/logout

GET    /api/v1/audit-logs
GET    /api/v1/reports/customer-summary
GET    /api/v1/reports/policy-summary

GET    /api/v1/admin/toggles
PATCH  /api/v1/admin/toggles/{name}

POST   /api/internal/sync-to-legacy (Debezium HTTP callbacks)
```

**Rules:** Plural nouns, kebab-case, no verbs in URLs

### Database Naming

Convention: snake_case for tables/columns

```sql
CUSTOMER, customer_id, first_name, created_at
POLICY, policy_id, policy_status
AUDIT_LOG, audit_timestamp, user_id
FEATURE_TOGGLE, toggle_name
```

### Java Classes

PascalCase packages and classes:

```
com.cicsgenapp.customer.controller.CustomerController
com.cicsgenapp.policy.validator.MotorPolicyValidator
com.cicsgenapp.common.error.ValidationException
```

### React Components

PascalCase components, kebab-case files:

```
File: src/components/customer/CustomerSearchForm.tsx
Export: function CustomerSearchForm() { }
```

---

## API Response Format

### Success (2xx)

```json
{
  "data": { "customerId": "...", "firstName": "Jane", ... },
  "metadata": { "timestamp": "2025-10-31T10:15:00Z", "version": "1.0" }
}
```

### Paginated

```json
{
  "data": [ { ... }, { ... } ],
  "pagination": { "limit": 50, "offset": 0, "total": 237, "hasMore": true },
  "metadata": { "timestamp": "...", "version": "1.0" }
}
```

### Error (4xx/5xx)

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "One or more validation errors occurred",
    "details": [{ "field": "email", "message": "Invalid email format" }],
    "traceId": "550e8400-..."
  },
  "metadata": { "timestamp": "..." }
}
```

---

## Structured Logging

All logs output as JSON:

```json
{
  "timestamp": "2025-10-31T10:15:00.123Z",
  "level": "INFO",
  "logger": "com.cicsgenapp.customer.CustomerService",
  "message": "Customer created successfully",
  "customerId": "550e8400-...",
  "userId": "user-123",
  "traceId": "550e8400-...",
  "spanId": "span-456",
  "duration_ms": 45
}
```

**Log Levels:** ERROR (failures), WARN (degradation), INFO (events), DEBUG (diagnostics)

---

## Authentication & Authorization (OIDC/Zitadel)

### JWT Token

```json
{
  "sub": "user-123",
  "email": "jane@example.com",
  "roles": ["AGENT", "COMPLIANCE"],
  "exp": 1730364900,
  "iat": 1730361300
}
```

### Roles

- `AGENT`: Customer/policy CRUD
- `ADMIN`: User management, feature toggles
- `COMPLIANCE`: Audit logs, reports

```java
@PreAuthorize("hasRole('AGENT')")
public ResponseEntity<CustomerDto> createCustomer(...) { }
```

---

## Data Consistency Pattern (CDC)

### Change Data Capture Flow

```
PostgreSQL INSERT/UPDATE/DELETE
    ↓
PostgreSQL WAL (Write-Ahead Log)
    ↓
Debezium Server (CDC listener)
    ↓
HTTP POST /api/internal/sync-to-legacy
{ "op": "c|u|d", "before": {...}, "after": {...} }
    ↓
Spring Boot ChangeDataCaptureController
    ↓
LegacyCobolBridge.syncToDb2()
    ↓
Db2 INSERT/UPDATE/DELETE (async)
    ↓
Log result (audit trail)
```

**Properties:**

- ✅ Asynchronous (non-blocking Pg writes)
- ✅ At-least-once delivery (Debezium retries)
- ✅ Eventual consistency (Db2 lags behind Pg by seconds)
- ✅ No Kafka (HTTP-based simplicity)

### Debezium Configuration

```yaml
debezium:
  server:
    channel:
      type: http
      http:
        url: http://spring-boot:8080/api/internal/sync-to-legacy
        method: POST
  source:
    connector:
      class: io.debezium.connector.postgresql.PostgresConnector
    database:
      hostname: postgres
      port: 5432
      dbname: cicsgenapp
    table:
      include:
        list: public.customer,public.policy,public.audit_log,public.feature_toggle
```

---

## Strangler Pattern & Feature Toggles

### API Gateway Routing

```java
// Spring Cloud Gateway config
if (featureToggleService.isEnabled("customer-api-enabled")) {
  route → Spring Boot service (POST /customer-service/v1/customers)
} else {
  route → Legacy COBOL service (via HTTP bridge)
}
```

### Parallel Run Validation (EPIC 4)

1. **Dual-Write Phase** (weeks 15-24 during transition)

   - Feature toggle: `parallel-run-enabled = true`
   - All writes → PostgreSQL (primary) + Db2 (async via CDC)
   - Errors logged but non-fatal (eventual consistency acceptable)

2. **Validation Dashboard** (agents test operations)

   - Manual: Create customer in new system, verify in legacy
   - Automated: Background job compares Pg vs Db2 every 5 minutes
   - Track match rate per entity type (Customer, Motor, Endowment, House, Commercial)

3. **Traffic Cutover** (once validated)
   - Feature toggle: `customer-api-enabled = 50%` (canary deployment)
   - Monitor error rates, latency, exceptions
   - Increment to 100% when stable
   - Legacy system becomes read-only

---

## Testing Strategy

### Unit Tests (Target 80%+ Coverage)

**Spring Boot:**

```java
@Test
void testValidateCustomer_InvalidEmail_ThrowsException() {
  assertThrows(ValidationException.class,
    () -> validationService.validate(invalidCustomer)
  );
}
```

**React:**

```typescript
test("CustomerSearchForm submits query", () => {
  render(<CustomerSearchForm onSearch={mockFn} />);
  const input = screen.getByRole("textbox");
  fireEvent.change(input, { target: { value: "Smith" } });
  expect(mockFn).toHaveBeenCalledWith("Smith");
});
```

### Integration Tests

**Spring Boot with TestContainers:**

```java
@SpringBootTest
@Testcontainers
class CustomerServiceIntegrationTest {
  @Container
  static PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"));

  @Test
  void testCreateCustomer_SavesToPg() {
    Customer saved = customerService.createCustomer(customer);
    assertTrue(customerRepository.existsById(saved.getId()));
  }
}
```

### API Contract Tests (Pact)

Validates API contracts between React and Spring Boot:

```java
@SpringBootTest
@PactTestFor(providerName = "CustomerAPI", port = "9000")
class CustomerApiPactTest {
  @Pact(provider = "CustomerAPI", consumer = "ReactFrontend")
  public RequestResponsePact createCustomerPact(PactBuilder builder) {
    return builder
      .uponReceiving("POST /api/v1/customers")
        .path("/api/v1/customers")
        .method("POST")
        .body(matcher.customerRequest())
      .willRespondWith()
        .status(201)
        .body(matcher.customerResponse())
      .toPact();
  }
}
```

---

## Deployment Architecture

### Docker Images

**Spring Boot:**

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]
```

**React:**

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY . .
RUN npm ci && npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
HEALTHCHECK --interval=30s CMD curl -f http://localhost/ || exit 1
```

### Kubernetes (Helm)

**values.yaml:**

```yaml
replicaCount: 3
image:
  repository: cicsgenapp/backend
  tag: "1.0.0"
service:
  type: LoadBalancer
  port: 80
env:
  DATABASE_URL: jdbc:postgresql://postgres:5432/cicsgenapp
  OIDC_PROVIDER_URL: https://zitadel.example.com
autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
```

---

## Development Environment Setup

### Prerequisites

```bash
Java 17+
Node.js 18+
Docker 24.x
PostgreSQL 15 (via Docker)
Git
```

### Local Development

**Backend:**

```bash
cd cicsgenapp-backend
docker-compose up -d postgres
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
# Available: http://localhost:8080
```

**Frontend:**

```bash
cd cicsgenapp-frontend
npm install
npm run dev
# Available: http://localhost:5173
```

---

## Architecture Decision Records (ADRs)

### ADR-001: Spring Boot 3.3+ over Quarkus

**Decision:** Spring Boot 3.3+ LTS for backend

**Rationale:** Team ready on day 1, 12-month timeline, OIDC support excellent in Spring Security 6

**Consequences:** (+) Team productivity, (-) Slightly higher memory than Quarkus

---

### ADR-002: PostgreSQL Primary (Db2 via CDC)

**Decision:** PostgreSQL primary; Db2 synced async via CDC

**Rationale:** Db2 vendor lock-in, CDC decouples systems, eventual consistency acceptable

**Consequences:** (+) Clean architecture, (-) Eventual consistency with Db2

---

### ADR-003: CDC without Kafka (HTTP Sink)

**Decision:** Debezium Server + HTTP Sink (PostgreSQL WAL → HTTP callbacks)

**Rationale:** No Kafka operational overhead, HTTP callbacks simpler, sufficient for eventual consistency

**Consequences:** (+) No message broker, (+) Simple ops, (-) HTTP less reliable than broker (mitigated by retries)

---

### ADR-004: Zitadel for OIDC

**Decision:** Zitadel self-hosted OIDC authentication

**Rationale:** Eliminates password management, platform-agnostic, Spring Security 6 native support

**Consequences:** (+) Externalized auth, (-) Zitadel infrastructure dependency

---

### ADR-005: Spring Cloud Gateway for Strangler

**Decision:** Spring Cloud Gateway for request routing

**Rationale:** Embedded in Spring Boot, native feature toggle support, simpler than Kong

**Consequences:** (+) Integrated deployment, (-) Less advanced than Kong

---

### ADR-006: Unleash (Self-Hosted Feature Toggles)

**Decision:** Unleash open-source, self-hosted

**Rationale:** Platform-agnostic, open-source (no vendor lock-in), sufficient for strangler pattern

**Consequences:** (+) Self-hosted control, (-) Operational responsibility

---

## Consistency Rules for AI Agents

**MANDATORY** for all story implementations:

1. ✅ Use provided API response format (data/metadata)
2. ✅ Use structured JSON logging (consistent schema)
3. ✅ Validate all input (frontend + backend)
4. ✅ Handle errors with GlobalExceptionHandler
5. ✅ Test before merging (unit + integration)
6. ✅ Use kebab-case for REST endpoints
7. ✅ Use snake_case for database columns
8. ✅ Check feature toggles before calling legacy
9. ✅ Audit all data mutations (log to AUDIT_LOG)
10. ✅ Use OIDC/JWT for authentication (no sessions)

---

## Cross-Epic Architecture Mapping

| Component       | EPIC 1    | EPIC 2    | EPIC 3        | EPIC 4        | EPIC 5    |
| --------------- | --------- | --------- | ------------- | ------------- | --------- |
| Spring Boot     | ✅ Setup  | ✅ API    | ⚠️ API        | ✅ Routing    | ✅ API    |
| React           | ⚠️ Setup  | ⚠️ UI     | ✅ Build      | ⚠️ Dashboard  | ✅ UI     |
| PostgreSQL      | ✅ Schema | ✅ Tables | ⚠️ Read       | ⚠️ Validation | ✅ Tables |
| Db2 (CDC)       | -         | ✅ Sink   | ⚠️ Validation | ✅ Setup      | ✅ Sync   |
| API Gateway     | ✅ Config | ⚠️ Routes | ⚠️ Routes     | ✅ Toggles    | ⚠️ Routes |
| Feature Toggles | ✅ Setup  | ⚠️ Use    | ⚠️ Use        | ✅ Mgmt       | ⚠️ Use    |
| Testing         | ✅ Setup  | ✅ Tests  | ✅ Tests      | ✅ Contract   | ✅ Tests  |
| Observability   | ✅ Setup  | ⚠️ Logs   | ⚠️ Logs       | ⚠️ Tracing    | ⚠️ Logs   |

---

**Architecture Document Complete!** ✅

This architecture is the **consistency contract** for all AI agents implementing Epics 1-5 (43 stories). All decisions are documented; system is ready for Epic 1 implementation.
