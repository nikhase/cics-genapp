# Story 1.4: Spring Cloud Gateway and Strangler Pattern Routing

Status: drafted

## Story

As an Architect,
I want to establish a Spring Cloud Gateway embedded in the Spring Boot application that routes requests to either new Spring Boot services or legacy COBOL system,
So that we can gradually migrate traffic without clients knowing about the backend change.

## Acceptance Criteria

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

## Tasks / Subtasks

- [ ] Task 1: Add Spring Cloud Gateway and Resilience4j dependencies to pom.xml (AC: #1, #4)
  - [ ] Add org.springframework.cloud:spring-cloud-starter-gateway dependency (4.x, compatible with Spring Boot 3.3)
  - [ ] Add org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j dependency
  - [ ] Add io.github.resilience4j:resilience4j-core dependency
  - [ ] Add io.github.resilience4j:resilience4j-circuitbreaker dependency
  - [ ] Verify Spring Cloud version matches Spring Boot 3.3 (typically Spring Cloud 2023.0.x series)
  - [ ] Run mvn dependency:tree to ensure no conflicts

- [ ] Task 2: Configure Spring Cloud Gateway in application.yml (AC: #2)
  - [ ] Create GatewayConfig.java with RouteLocator bean (Java-based route configuration preferred over YAML for flexibility)
  - [ ] Define four main routes with proper predicates and filters:
    - POST /api/v1/customers → http://localhost:8080/api/v1/customers (local service)
    - GET /api/v1/customers/** → http://localhost:8080/api/v1/customers/**
    - POST /api/v1/policies → http://localhost:8080/api/v1/policies
    - GET /api/v1/policies/** → http://localhost:8080/api/v1/policies/**
    - GET /api/v1/audit/** → http://localhost:8080/api/v1/audit/**
    - POST /api/v1/auth/** → http://localhost:8080/api/v1/auth/**
  - [ ] Add feature toggle filter: check Unleash toggle (customer-api-enabled, policy-api-enabled) before routing
  - [ ] If toggle OFF: route to legacy COBOL system endpoint (configurable, e.g., http://legacy-cobol-system:8081)
  - [ ] If toggle ON: route to Spring Boot service (self-routing, 127.0.0.1:8080)
  - [ ] Add route order to ensure specific routes take precedence over wildcards
  - [ ] Configure gateway port (default 8080, or override to separate port if desired)

- [ ] Task 3: Implement circuit breaker for legacy COBOL fallback (AC: #4)
  - [ ] Create CircuitBreakerConfig.java with Resilience4j configuration
  - [ ] Configure circuit breaker instance for legacy COBOL calls:
    - failureRateThreshold: 50 (open circuit if 50%+ failures)
    - slowCallRateThreshold: 50 (open if 50%+ slow calls)
    - slowCallDurationThreshold: 5000ms (calls > 5s are "slow")
    - waitDurationInOpenState: 30000ms (wait 30s before testing recovery)
    - minimumNumberOfCalls: 5 (need at least 5 calls to measure)
    - permittedNumberOfCallsInHalfOpenState: 1 (test with 1 request in half-open)
  - [ ] Create CircuitBreakerFilter.java that wraps legacy calls:
    - Intercept requests routing to legacy system
    - Apply circuit breaker policy
    - If circuit OPEN: return fallback response (e.g., 503 Service Unavailable with message)
    - If circuit CLOSED/HALF_OPEN: proceed normally
  - [ ] Add fallback response format:
    ```json
    {
      "error": {
        "code": "LEGACY_SYSTEM_UNAVAILABLE",
        "message": "Legacy system temporarily unavailable. Please try again in a few moments.",
        "details": "The legacy COBOL service is experiencing issues. Your request could not be routed."
      },
      "metadata": {"timestamp": "...", "traceId": "..."}
    }
    ```
  - [ ] Emit metrics on circuit state changes (for Prometheus)

- [ ] Task 4: Implement request/response logging at gateway level (AC: #3)
  - [ ] Create GatewayLoggingFilter.java implementing GatewayFilter
  - [ ] Log structured JSON for all requests:
    - Request ID / correlation ID (from X-Trace-Id header or generate new UUID)
    - HTTP method (GET, POST, PUT, DELETE)
    - Original path (before routing)
    - Route destination (Spring Boot or legacy)
    - Timestamp (ISO 8601)
    - Client IP address
    - User agent
  - [ ] Log structured JSON for all responses:
    - Request ID (same as request)
    - HTTP status code
    - Response time (latency in ms)
    - Route destination
    - Timestamp
  - [ ] Ensure all logs are JSON-formatted with traceId field (for ELK Stack aggregation)
  - [ ] Use Spring Cloud Gateway built-in logging or custom filter
  - [ ] Configure log level: INFO for normal requests, WARN for errors, DEBUG for detailed (configurable per profile)

- [ ] Task 5: Implement request tracing with X-Trace-Id correlation ID propagation (AC: #7)
  - [ ] Create TraceIdFilter.java implementing GatewayFilter with GlobalFilter
  - [ ] For each request:
    - Check for X-Trace-Id header
    - If present, extract and use as correlation ID
    - If missing, generate new UUID
  - [ ] Add correlation ID to request context (for use in downstream services)
  - [ ] Propagate X-Trace-Id header to downstream service (add to forwarded request)
  - [ ] Add correlation ID to response headers (X-Trace-Id response header)
  - [ ] Ensure correlation ID is available in MDC for logging (from Story 1.2 LoggingFilter)
  - [ ] Test correlation ID flows through request → gateway → service → response

- [ ] Task 6: Configure CORS headers for React frontend (AC: #6)
  - [ ] Create CorsConfig.java with globalCorsConfig (replaces SecurityConfig CORS, gateway-level takes precedence)
  - [ ] Configure allowed origins:
    - Dev: http://localhost:3000, http://localhost:3001 (dev server and alternate)
    - Prod: https://cicsgenapp.example.com (production domain)
  - [ ] Configure allowed methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
  - [ ] Configure allowed headers: Content-Type, Authorization, X-Trace-Id, X-Requested-With
  - [ ] Configure exposed headers: X-Trace-Id (for client to capture correlation ID)
  - [ ] Configure credentials: allow (for cookies if needed)
  - [ ] Configure max age: 3600 (1 hour cache for preflight)
  - [ ] Test CORS with curl preflight request (OPTIONS /api/v1/customers)

- [ ] Task 7: Configure gateway for multiple environments (dev, test, prod) (AC: #1, #2)
  - [ ] Create application-dev.yml with:
    - Gateway port: 8080
    - Resilience4j thresholds: lenient (40% failure threshold for faster testing)
    - Legacy COBOL endpoint: http://localhost:8081 (mock for testing)
    - CORS allowed origins: http://localhost:3000
  - [ ] Create application-test.yml with:
    - Gateway port: 8080
    - Circuit breaker: disabled or very lenient for testing
    - Legacy COBOL endpoint: http://test-legacy-service:8081 (TestContainers mock)
    - CORS allowed origins: * (allow all for test)
  - [ ] Create application-prod.yml with:
    - Gateway port: 8080 (or reverse proxy on :443)
    - Resilience4j thresholds: strict (50% as specified)
    - Legacy COBOL endpoint: https://legacy-system.corporate.com (secure, TLS)
    - CORS allowed origins: https://cicsgenapp.example.com (production domain only)
    - All timeouts: 5s as specified
  - [ ] Document how to override gateway config via environment variables (Spring Boot externalizes config)

- [ ] Task 8: Implement feature toggle integration with Unleash (AC: #2)
  - [ ] Create FeatureToggleFilter.java that checks Unleash toggles before routing
  - [ ] For each request to /api/v1/customers/* or /api/v1/policies/*:
    - Query Unleash client (from Story 1.6 integration)
    - Check toggle state: customer-api-enabled, policy-api-enabled
    - If toggle enabled (true): route to Spring Boot service
    - If toggle disabled (false): route to legacy COBOL system
    - If toggle missing: default to Spring Boot (fail-safe to new system)
  - [ ] Add metrics: count requests by route destination (Spring Boot vs legacy) via Micrometer
  - [ ] Add support for canary deployments:
    - Unleash supports percentage-based toggles (0%, 10%, 50%, 100%)
    - Route X% to new system, (100-X)% to legacy for gradual traffic cutover
  - [ ] Log toggle state at request time (for debugging routing decisions)

- [ ] Task 9: Test gateway routing with multiple scenarios (AC: 1-8)
  - [ ] Integration test: Route request to /api/v1/customers → verify reaches Spring Boot service
  - [ ] Integration test: Toggle feature OFF → route to legacy endpoint
  - [ ] Integration test: Request includes X-Trace-Id header → verify header propagated to downstream
  - [ ] Integration test: Generate X-Trace-Id if missing → verify UUID created
  - [ ] Integration test: CORS preflight (OPTIONS) → verify CORS headers present
  - [ ] Integration test: Circuit breaker opens when legacy service returns 50%+ errors
  - [ ] Integration test: Circuit breaker half-open state → test recovery after 30s wait
  - [ ] Integration test: Request timeout (downstream takes > 5s) → verify timeout response
  - [ ] Integration test: Structured logging includes traceId for all requests
  - [ ] Load test: Gateway can handle 1000+ req/s with proper timeout/pooling

- [ ] Task 10: Document gateway configuration and routing rules (AC: #1, #2, #8)
  - [ ] Update README.md with gateway overview:
    - What is Spring Cloud Gateway
    - How to enable/disable toggles
    - How to switch between Spring Boot and legacy routing
    - How to monitor gateway health
  - [ ] Create GATEWAY.md technical documentation:
    - Route definitions and predicates
    - Circuit breaker configuration and thresholds
    - Feature toggle mappings (which toggle controls which route)
    - How to add new routes (if needed)
    - Troubleshooting: requests hitting wrong endpoint, circuit breaker stuck open, etc.
  - [ ] Document environment variables for different deployments
  - [ ] Document API contract changes (if any) between Spring Boot and legacy interfaces

- [ ] Task 11: Prepare for Kubernetes deployment (AC: #8)
  - [ ] Verify gateway is stateless (no session affinity needed)
  - [ ] Verify horizontally scalable (multiple pod replicas can run in parallel)
  - [ ] Prepare for Helm chart (in Story 1.11):
    - Document resource requirements (CPU, memory)
    - Document liveness/readiness probes (use /actuator/health)
    - Document environment variables (legacy endpoint, toggle URLs, etc.)
  - [ ] Test with local Kubernetes (minikube) with 2+ gateway replicas
  - [ ] Verify load balancing works across replicas (requests distributed evenly)

## Dev Notes

### Architecture Context

This story extends Story 1.2 and 1.3 by adding the API Gateway layer that sits between clients and backend services. The gateway is the **critical component** that enables the strangler pattern, allowing simultaneous operation of new (Spring Boot) and legacy (COBOL) services without client awareness.

**Key Pattern:** The gateway is NOT a separate service but embedded in the Spring Boot application (GatewayConfig bean). This keeps deployment simple and aligns with cloud-native patterns. In Story 1.11 (Kubernetes), multiple gateway instances will run, with load balancing at ingress level.

**Learnings from Story 1.2 and 1.3:**
- Story 1.2 established Spring Security with CORS configuration (which now moves to gateway-level)
- Story 1.3 established PostgreSQL for storing feature toggle state (Unleash integration)
- Neither story has the full Spring Cloud Gateway routing rules yet

### Technical Requirements for This Story

1. **Spring Cloud Gateway 4.x** - Latest stable for Spring Boot 3.3 compatibility
2. **Resilience4j Circuit Breaker** - Patterns for legacy service failover
3. **Unleash Feature Toggles** - Control routing without redeployment (integrated in Story 1.6)
4. **Structured Logging** - JSON logs with traceId at gateway level
5. **CORS Handling** - Gateway-level CORS (takes precedence over service-level)

### Constraints & Requirements

- **Java 21 LTS:** Required (from Story 1.1)
- **Spring Boot 3.3.4 LTS:** Already established
- **Spring Cloud 2023.0.x:** Matched to Spring Boot 3.3 LTS
- **Resilience4j 2.1+:** Latest stable
- **Downstream timeout:** 5 seconds maximum (from AC #5)
- **Circuit breaker threshold:** 50% failure rate (from AC #4)
- **Port:** Gateway runs on 8080 (same as embedded Spring Boot service)

### Testing Standards Summary

Story 1.4 requires validation of gateway routing, circuit breaking, and feature toggle integration:
- **Unit Tests**: Route definition parsing, circuit breaker state transitions
- **Integration Tests**: Full request flow through gateway with feature toggle ON/OFF, CORS headers, tracing
- **Circuit Breaker Tests**: Verify state transitions (CLOSED → OPEN → HALF_OPEN → CLOSED)
- **Load Tests**: Gateway performance with 1000+ req/s, timeout handling
- **Resilience Tests**: Verify graceful degradation when downstream service is down

Target: 80%+ test coverage for gateway routing logic

### References

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Resilience4j Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
- [Spring Cloud Gateway with Resilience4j](https://spring.io/guides/gs/cloud-resilience4j/)
- [Strangler Pattern](https://martinfowler.com/bliki/StranglerFigApplication.html)
- [CORS Configuration in Spring](https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)

## Dev Agent Record

### Context Reference

- docs/stories/1-4-spring-cloud-gateway-and-strangler-pattern-routing.context.xml

### Agent Model Used

Claude Haiku 4.5

### Debug Log References

### Completion Notes List

### File List

## Change Log

- **2025-11-01 [16:45 UTC]:** Story 1.4 DRAFTED - Spring Cloud Gateway and Strangler Pattern Routing

## Status

drafted

---

## Prerequisites

- Story 1.2: Spring Boot Starter Project with Core Configuration (✓ COMPLETED)
- Story 1.3: PostgreSQL Database Connectivity and Schema Management (ready-for-dev)

## Story Type

Infrastructure & API Layer Setup

## Story Points (Estimate)

16 points

