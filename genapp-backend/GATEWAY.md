# Spring Cloud Gateway Configuration & Routing Guide

## Overview

The CICS GenApp backend uses **Spring Cloud Gateway** as an embedded API gateway to implement the **strangler pattern**. This allows gradual migration of traffic from the legacy COBOL system to new Spring Boot services without clients being aware of the backend change.

**Key Architecture:**
- Embedded in Spring Boot 3.3.4 application (not a separate microservice)
- Routes requests to either new Spring Boot services or legacy COBOL endpoints
- Implements circuit breaker pattern (Resilience4j) for fault tolerance
- Provides structured JSON logging with correlation IDs for distributed tracing
- Configurable per environment (dev/test/prod) via application profiles

---

## Route Definitions

### Customer API Routes

| Route | Method | Destination | Purpose |
|-------|--------|-------------|---------|
| `/api/v1/customers` | GET | Spring Boot (8080) | List customers |
| `/api/v1/customers/{id}` | GET | Spring Boot (8080) | Get customer details |
| `/api/v1/customers` | POST | Spring Boot (8080) | Create customer |
| `/api/v1/customers/{id}` | PUT | Spring Boot (8080) | Update customer |
| `/api/v1/customers/{id}` | DELETE | Spring Boot (8080) | Soft delete customer |

### Policy API Routes

| Route | Method | Destination | Purpose |
|-------|--------|-------------|---------|
| `/api/v1/policies` | GET | Spring Boot (8080) | List policies |
| `/api/v1/policies/{id}` | GET | Spring Boot (8080) | Get policy details |
| `/api/v1/policies` | POST | Spring Boot (8080) | Create policy |
| `/api/v1/policies/{id}` | PUT | Spring Boot (8080) | Update policy |
| `/api/v1/policies/{id}` | DELETE | Spring Boot (8080) | Soft delete policy |

### Audit & Auth Routes

| Route | Method | Destination | Purpose |
|-------|--------|-------------|---------|
| `/api/v1/audit/**` | Any | Spring Boot (8080) | Audit logging (always new system) |
| `/api/v1/auth/**` | Any | Spring Boot (8080) | OIDC/Zitadel authentication |

---

## Circuit Breaker Configuration

### Overview

The circuit breaker protects against cascading failures when the legacy COBOL system becomes unavailable.

### State Transitions

```
CLOSED (Normal operation)
   ↓ (50%+ failures detected)
OPEN (Legacy unavailable, return fallback)
   ↓ (wait 30s)
HALF_OPEN (Test recovery with 1 request)
   ↓ (Success or failure)
CLOSED or OPEN
```

### Configuration Parameters

| Parameter | Value | Description |
|-----------|-------|-------------|
| Failure Rate Threshold | 50% | Opens circuit if 50%+ calls fail |
| Slow Call Rate Threshold | 50% | Opens circuit if 50%+ calls are slow |
| Slow Call Duration | 5000ms (5s) | Calls longer than 5s are "slow" |
| Wait Duration in Open | 30000ms (30s) | Waits 30s before testing recovery |
| Minimum Calls | 5 | Needs at least 5 calls to measure |
| Half-Open Permitted Calls | 1 | Tests recovery with 1 request |

### Example Fallback Response

When circuit breaker opens (legacy system unavailable):

```json
{
  "error": {
    "code": "LEGACY_SYSTEM_UNAVAILABLE",
    "message": "Legacy system temporarily unavailable. Please try again in a few moments.",
    "details": "The legacy COBOL service is experiencing issues. Your request could not be routed."
  },
  "metadata": {
    "timestamp": "2025-11-03T10:30:45.123Z",
    "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

HTTP Status: **503 Service Unavailable**

---

## Feature Toggle Integration (Story 1.6)

### Upcoming Enhancement

Feature toggles (Unleash) will control which system handles requests:

```java
// Pseudocode (implemented in Story 1.6):
if (unleash.isEnabled("customer-api-enabled")) {
  route to Spring Boot (8080)
} else {
  route to Legacy COBOL (8081)
}
```

### Supported Toggle Names

- `customer-api-enabled` - Controls /api/v1/customers/* routing
- `policy-api-enabled` - Controls /api/v1/policies/* routing

### Canary Deployments

Toggles support percentage-based rollout:

```
0% → All traffic to legacy
10% → 10% to Spring Boot, 90% to legacy
50% → 50/50 split
100% → All traffic to Spring Boot
```

---

## CORS Configuration

### By Environment

#### Development
```
Allowed Origins: http://localhost:3000, http://localhost:3001, http://localhost:8080
Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Max Age: 3600s (1 hour)
```

#### Test
```
Allowed Origins: * (allow all for integration testing)
Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Max Age: 3600s
```

#### Production
```
Allowed Origins: https://cicsgenapp.example.com (ONLY)
Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Max Age: 3600s
Credentials: true (allows cookies for authentication)
```

### Example Preflight Request

```bash
curl -X OPTIONS http://localhost:8080/api/v1/customers \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

Expected response headers:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization, X-Trace-Id, X-Requested-With
Access-Control-Max-Age: 3600
```

---

## Request/Response Logging

### Log Format

All gateway logs are **JSON-formatted** for ELK Stack integration:

#### Request Log
```json
{
  "level": "INFO",
  "timestamp": "2025-11-03T10:30:45.123Z",
  "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "type": "GATEWAY_REQUEST",
  "method": "GET",
  "path": "/api/v1/customers/123",
  "clientIp": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

#### Response Log
```json
{
  "level": "INFO",
  "timestamp": "2025-11-03T10:30:45.456Z",
  "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "type": "GATEWAY_RESPONSE",
  "method": "GET",
  "path": "/api/v1/customers/123",
  "statusCode": 200,
  "latencyMs": 145
}
```

### Log Levels

- **INFO**: Successful requests (2xx, 3xx)
- **WARN**: Client errors (4xx)
- **ERROR**: Server errors (5xx)

---

## Trace ID & Correlation

### X-Trace-Id Header

Every request must have a trace ID for distributed tracing:

```
Request Headers:
  X-Trace-Id: a1b2c3d4-e5f6-7890-abcd-ef1234567890

Response Headers:
  X-Trace-Id: a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

### Trace ID Flow

1. **Request received**: Extract X-Trace-Id or generate UUID
2. **Gateway logging**: Add to MDC for all logs
3. **Downstream service**: Propagate in request header
4. **Response**: Include in response header
5. **Client**: Can capture from response for debugging

### Example: Full Request Lifecycle

```bash
# Client sends request (with or without trace ID)
curl http://localhost:8080/api/v1/customers/123 \
  -H "X-Trace-Id: client-assigned-id-123"

# Gateway generates or uses provided trace ID
# All gateway logs include: {"traceId": "client-assigned-id-123"}

# Response includes trace ID
HTTP/1.1 200 OK
X-Trace-Id: client-assigned-id-123
```

If client doesn't provide trace ID, gateway generates UUID:
```
X-Trace-Id: 550e8400-e29b-41d4-a716-446655440000
```

---

## Timeout Handling

### Configuration

- **Connect Timeout**: 5 seconds
- **Read Timeout**: 5 seconds
- **Total Request Timeout**: 5 seconds

### Timeout Response

When downstream service doesn't respond within 5 seconds:

```json
{
  "error": {
    "code": "REQUEST_TIMEOUT",
    "message": "Request to downstream service timed out",
    "details": "The service did not respond within 5 seconds"
  },
  "metadata": {
    "timestamp": "2025-11-03T10:30:50.456Z",
    "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

HTTP Status: **504 Gateway Timeout**

---

## Environment-Specific Configuration

### Development Profile (`-Dspring.profiles.active=dev`)

```yaml
Gateway:
  Legacy Endpoint: http://localhost:8081 (local mock)
  Service Endpoint: http://localhost:8080

Circuit Breaker (lenient for testing):
  Failure Rate: 40%
  Wait Duration: 10s
  Min Calls: 2

Logging:
  Level: DEBUG (verbose)
```

### Test Profile (`-Dspring.profiles.active=test`)

```yaml
Gateway:
  Legacy Endpoint: http://test-legacy-service:8081 (TestContainers)
  Service Endpoint: http://localhost:8080

Circuit Breaker (lenient for fast feedback):
  Failure Rate: 50%
  Wait Duration: 1s
  Min Calls: 1

CORS: Allow all (*)
```

### Production Profile (`-Dspring.profiles.active=prod`)

```yaml
Gateway:
  Legacy Endpoint: https://legacy-system.corporate.com (TLS)
  Service Endpoint: http://localhost:8080

Circuit Breaker (strict):
  Failure Rate: 50%
  Wait Duration: 30s
  Min Calls: 5

CORS: Strict (only https://cicsgenapp.example.com)
Logging: WARN (minimal logs for performance)
```

---

## Metrics & Monitoring

### Exposed Metrics

Gateway exposes metrics via Spring Boot Actuator (`/actuator/metrics`):

```
# Circuit Breaker Metrics
resilience4j.circuitbreaker.state
resilience4j.circuitbreaker.calls (total, success, failure, slow)
resilience4j.circuitbreaker.failure_rate
resilience4j.circuitbreaker.slow_call_rate

# Gateway Metrics
http.server.requests (per endpoint)
process.cpu.usage
jvm.memory.used
```

### Prometheus Scraping

Configure Prometheus to scrape metrics:

```yaml
scrape_configs:
  - job_name: 'cics-genapp'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

### Example Query

```promql
# Failure rate of legacy COBOL circuit breaker
resilience4j_circuitbreaker_failure_rate{name="legacy-cobol-system"}

# Request latency by endpoint
rate(http.server.requests_seconds_sum{uri="/api/v1/customers"}[5m]) / rate(http.server.requests_seconds_count{uri="/api/v1/customers"}[5m])
```

---

## Adding New Routes

### Step 1: Update GatewayConfig.java

Add route definition in `GatewayConfig.customRoutes()`:

```java
.route("new-endpoint", r ->
    r.path("/api/v1/new-endpoint/**")
        .and()
        .method(HttpMethod.GET)
        .filters(f -> f.stripPrefix(0))
        .uri(serviceEndpoint))
```

### Step 2: Update Application Profile

Add feature toggle mapping in config (Story 1.6):

```yaml
toggles:
  new-api-enabled: true
```

### Step 3: Test

Add integration test in `GatewayIntegrationTest`:

```java
@Test
void testNewEndpointRouting() {
  webTestClient.get()
      .uri("/api/v1/new-endpoint/test")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().exists("X-Trace-Id");
}
```

---

## Troubleshooting

### Issue: Requests hitting wrong endpoint

**Symptoms**: Customer requests routing to legacy instead of Spring Boot

**Solutions**:
1. Check feature toggle state in Unleash (Story 1.6)
2. Verify route order in GatewayConfig (more specific routes first)
3. Check circuit breaker state: `GET /actuator/metrics/resilience4j.circuitbreaker.state`
4. Review gateway logs: grep for "Routing" and "Feature toggle"

### Issue: Circuit breaker stuck in OPEN state

**Symptoms**: All requests returning 503, circuit won't recover

**Solutions**:
1. Fix the legacy system issue (that caused failures)
2. Wait for wait-duration (30s default) for automatic transition to HALF_OPEN
3. Or manually reset via management endpoint (if enabled): `POST /actuator/circuitbreakers/legacy-cobol-system/reset`
4. Check logs for actual failure reason

### Issue: CORS requests failing from React frontend

**Symptoms**: Browser console: "CORS policy: Cross-Origin request blocked"

**Solutions**:
1. Verify React frontend origin is in `cors.allowed-origins` in application.yml
2. Check for typos (https vs http, port number, etc.)
3. Send OPTIONS preflight request to verify CORS headers are present
4. Check gateway logs for CORS errors

### Issue: High latency or timeouts (>5s)

**Symptoms**: Requests failing with 504 Gateway Timeout

**Solutions**:
1. Check downstream service health: `GET /actuator/health`
2. Profile downstream service response times
3. Increase timeout if legitimate (edit application profile, default 5s)
4. Check for slow queries in database
5. Review circuit breaker state (may indicate larger issue)

---

## Next Steps

### Story 1.6: Unleash Feature Toggles

Implement dynamic feature toggle integration:
- Replace hardcoded routing with Unleash client
- Enable canary deployments (percentage-based rollout)
- Support A/B testing across customer and policy APIs

### Story 1.7: Structured Logging & Observability

Enhance logging infrastructure:
- Set up ELK Stack (Elasticsearch, Logstash, Kibana)
- Aggregate logs by traceId for end-to-end request tracking
- Create dashboards for gateway metrics and performance

### Story 1.11: Kubernetes & Helm

Prepare for cloud deployment:
- Configure Kubernetes health probes (liveness/readiness)
- Create Helm chart with gateway as stateless service
- Set up horizontal scaling (multiple pod replicas)

---

## References

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Resilience4j Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
- [Strangler Pattern (Martin Fowler)](https://martinfowler.com/bliki/StranglerFigApplication.html)
- [Spring Boot CORS Configuration](https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)
- [Distributed Tracing Best Practices](https://opentelemetry.io/)

---

**Document Version**: 1.0
**Last Updated**: 2025-11-03
**Story**: 1.4 (Spring Cloud Gateway and Strangler Pattern Routing)
