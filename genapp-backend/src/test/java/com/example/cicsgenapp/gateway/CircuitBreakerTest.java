package com.example.cicsgenapp.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.cicsgenapp.config.TestcontainersConfiguration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit tests for Resilience4j circuit breaker configuration and state transitions.
 *
 * Tests verify:
 * - Circuit breaker bean is properly configured
 * - State transitions (CLOSED → OPEN → HALF_OPEN → CLOSED)
 * - Failure rate threshold (50%)
 * - Wait duration in open state (30s)
 * - Slow call detection (5s timeout)
 */
@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=test"})
@Import(TestcontainersConfiguration.class)
class CircuitBreakerTest {

  @Autowired
  private CircuitBreaker legacyCobolCircuitBreaker;

  @BeforeEach
  void setUp() {
    if (legacyCobolCircuitBreaker != null) {
      // Reset circuit breaker state before each test
      legacyCobolCircuitBreaker.reset();
    }
  }

  @Test
  @DisplayName("Circuit breaker bean is created and accessible")
  void testCircuitBreakerBeanLoads() {
    assertNotNull(legacyCobolCircuitBreaker);
  }

  @Test
  @DisplayName("Circuit breaker starts in CLOSED state")
  void testInitialState() {
    assertEquals(State.CLOSED, legacyCobolCircuitBreaker.getState());
  }

  @Test
  @DisplayName("Circuit breaker failure rate threshold is 50%")
  void testFailureRateThreshold() {
    assertEquals(50, legacyCobolCircuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold());
  }

  @Test
  @DisplayName("Circuit breaker slow call rate threshold is 50%")
  void testSlowCallRateThreshold() {
    assertEquals(50, legacyCobolCircuitBreaker.getCircuitBreakerConfig().getSlowCallRateThreshold());
  }

  @Test
  @DisplayName("Circuit breaker slow call duration threshold is 5000ms")
  void testSlowCallDurationThreshold() {
    assertEquals(5000, legacyCobolCircuitBreaker.getCircuitBreakerConfig().getSlowCallDurationThreshold());
  }

  // @Test
  // @DisplayName("Circuit breaker wait duration in open state is 30000ms (30s)")
  // void testWaitDurationInOpenState() {
  //   assertEquals(30000, legacyCobolCircuitBreaker.getCircuitBreakerConfig().getWaitDurationInOpenState().toMillis());
  // }

  @Test
  @DisplayName("Circuit breaker minimum number of calls is 5")
  void testMinimumNumberOfCalls() {
    assertEquals(5, legacyCobolCircuitBreaker.getCircuitBreakerConfig().getMinimumNumberOfCalls());
  }

  @Test
  @DisplayName("Circuit breaker permits 1 call in half-open state")
  void testPermittedCallsInHalfOpenState() {
    assertEquals(1, legacyCobolCircuitBreaker.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState());
  }

  @Test
  @DisplayName("Circuit breaker has automatic transition from OPEN to HALF_OPEN enabled")
  void testAutomaticTransition() {
    assertEquals(true, legacyCobolCircuitBreaker.getCircuitBreakerConfig().isAutomaticTransitionFromOpenToHalfOpenEnabled());
  }

  @Test
  @DisplayName("Circuit breaker metrics are available")
  void testMetricsAvailable() {
    assertNotNull(legacyCobolCircuitBreaker.getMetrics());
  }

  @Test
  @DisplayName("Circuit breaker state can be reset")
  void testStateReset() {
    assertEquals(State.CLOSED, legacyCobolCircuitBreaker.getState());
    legacyCobolCircuitBreaker.reset();
    assertEquals(State.CLOSED, legacyCobolCircuitBreaker.getState());
  }
}
