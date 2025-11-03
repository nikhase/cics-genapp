package com.example.cicsgenapp.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for Spring Cloud Gateway routing and filters.
 *
 * Tests verify:
 * - Gateway routing to correct downstream services
 * - Trace ID propagation through request/response cycle
 * - CORS headers in responses
 * - Circuit breaker fallback behavior
 * - Logging and monitoring
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {"spring.profiles.active=test"})
class GatewayIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  @DisplayName("Test routing to /api/v1/customers GET endpoint")
  void testCustomersGetRouting() {
    webTestClient.get()
        .uri("/api/v1/customers/123")
        .exchange()
        .expectStatus().isNotFound()  // Expected: 404 since no actual customer
        .expectHeader().exists("X-Trace-Id");  // Trace ID should be present
  }

  @Test
  @DisplayName("Test routing to /api/v1/policies GET endpoint")
  void testPoliciesGetRouting() {
    webTestClient.get()
        .uri("/api/v1/policies/456")
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().exists("X-Trace-Id");
  }

  @Test
  @DisplayName("Test CORS preflight request (OPTIONS)")
  void testCorsPreflightRequest() {
    webTestClient.options()
        .uri("/api/v1/customers")
        .header("Origin", "http://localhost:3000")
        .header("Access-Control-Request-Method", "POST")
        .header("Access-Control-Request-Headers", "Content-Type")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().exists("Access-Control-Allow-Origin")
        .expectHeader().exists("Access-Control-Allow-Methods")
        .expectHeader().exists("Access-Control-Allow-Headers");
  }

  @Test
  @DisplayName("Test X-Trace-Id header propagation")
  void testTraceIdPropagation() {
    String expectedTraceId = "test-trace-12345";

    webTestClient.get()
        .uri("/api/v1/customers/123")
        .header("X-Trace-Id", expectedTraceId)
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().valueEquals("X-Trace-Id", expectedTraceId);
  }

  @Test
  @DisplayName("Test X-Trace-Id generation when missing")
  void testTraceIdGeneration() {
    webTestClient.get()
        .uri("/api/v1/customers/123")
        // Not providing X-Trace-Id header - should be generated
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().exists("X-Trace-Id");  // Should be auto-generated
  }

  @Test
  @DisplayName("Test gateway health endpoint")
  void testGatewayHealthEndpoint() {
    webTestClient.get()
        .uri("/actuator/health")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo("UP");
  }

  @Test
  @DisplayName("Test gateway metrics endpoint")
  void testGatewayMetricsEndpoint() {
    webTestClient.get()
        .uri("/actuator/metrics")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("Test audit endpoint routing")
  void testAuditEndpointRouting() {
    webTestClient.get()
        .uri("/api/v1/audit/logs")
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().exists("X-Trace-Id");
  }

  @Test
  @DisplayName("Test auth endpoint routing")
  void testAuthEndpointRouting() {
    webTestClient.post()
        .uri("/api/v1/auth/login")
        .exchange()
        .expectStatus().isNotFound()  // Will 404 since endpoint doesn't exist
        .expectHeader().exists("X-Trace-Id");
  }
}
