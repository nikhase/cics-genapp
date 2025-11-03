package com.example.cicsgenapp.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Resilience4j Circuit Breaker configuration for legacy COBOL system fallback.
 *
 * This configuration implements the circuit breaker pattern to protect against
 * cascading failures when the legacy COBOL system becomes unavailable. The circuit
 * breaker monitors the failure rate of calls to the legacy system and transitions
 * through states: CLOSED (normal) → OPEN (failed) → HALF_OPEN (testing recovery) → CLOSED.
 *
 * Configuration parameters (from acceptance criteria):
 * - Failure rate threshold: 50% (opens circuit if 50%+ of calls fail)
 * - Slow call rate threshold: 50% (opens if 50%+ calls exceed timeout)
 * - Slow call duration: 5000ms (calls taking longer are considered "slow")
 * - Wait duration in open state: 30000ms (waits 30s before testing recovery)
 * - Minimum number of calls: 5 (requires at least 5 calls to measure failure)
 * - Half-open permitted calls: 1 (tests recovery with 1 request in half-open state)
 */
@Configuration
public class CircuitBreakerConfig {

  private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerConfig.class);

  // Circuit breaker instance name
  private static final String LEGACY_CB_NAME = "legacy-cobol-system";

  /**
   * Creates and configures the circuit breaker registry.
   *
   * @return CircuitBreakerRegistry with legacy system circuit breaker
   */
  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
        // Failure rate threshold: 50% (from AC #4)
        // If 50% or more calls fail, open the circuit
        .failureRateThreshold(50)
        // Slow call rate threshold: 50%
        // If 50% or more calls exceed slowCallDurationThreshold, open the circuit
        .slowCallRateThreshold(50)
        // Slow call duration: 5000ms (from AC #5 timeout)
        // Calls taking longer than 5s are considered "slow"
        .slowCallDurationThreshold(Duration.ofMillis(5000))
        // Wait duration in open state: 30000ms (from AC #4)
        // After opening, wait 30s before transitioning to HALF_OPEN
        .waitDurationInOpenState(Duration.ofMillis(30000))
        // Minimum number of calls: 5
        // Need at least 5 calls before measuring failure rate
        .minimumNumberOfCalls(5)
        // Permitted calls in HALF_OPEN state: 1
        // Test recovery with only 1 request in half-open state
        .permittedNumberOfCallsInHalfOpenState(1)
        // Automatic transition from HALF_OPEN to OPEN after all permitted calls complete
        .automaticTransitionFromOpenToHalfOpenEnabled(true)
        .build();

    CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(cbConfig);

    // Register event consumer for monitoring circuit breaker state changes
    registry.getEventPublisher()
        .onEntryAdded(event -> LOG.info("Circuit breaker registered: {}", event.getAddedEntry().getName()))
        .onEntryRemoved(event -> LOG.info("Circuit breaker removed: {}", event.getRemovedEntry().getName()));

    return registry;
  }

  /**
   * Creates the circuit breaker instance for legacy COBOL system calls.
   *
   * This bean is used by CircuitBreakerFilter to wrap legacy service calls and
   * prevent cascading failures.
   *
   * @param registry the CircuitBreakerRegistry
   * @return configured CircuitBreaker for legacy COBOL system
   */
  @Bean
  public CircuitBreaker legacyCobolCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreaker circuitBreaker = registry.circuitBreaker(LEGACY_CB_NAME);

    // Register event listeners for state transitions (for debugging/monitoring)
    circuitBreaker.getEventPublisher()
        .onStateTransition(event -> LOG.warn(
            "Legacy COBOL circuit breaker state transition: {} → {}",
            event.getStateTransition().getFromState(),
            event.getStateTransition().getToState()))
        .onError(event -> LOG.error(
            "Legacy COBOL circuit breaker recorded error: {}",
            event.getThrowable().getMessage()))
        .onSuccess(event -> LOG.debug(
            "Legacy COBOL circuit breaker successful call"));

    return circuitBreaker;
  }

  /**
   * Registers circuit breaker metrics with Micrometer for Prometheus monitoring.
   *
   * Exposed metrics include:
   * - Circuit breaker state (0=CLOSED, 1=OPEN, 2=HALF_OPEN)
   * - Call counts (total, success, failure, slow)
   * - Failure rate percentage
   *
   * @param registry the CircuitBreakerRegistry
   * @param meterRegistry the Micrometer MeterRegistry
   */
  @Bean
  public TaggedCircuitBreakerMetrics circuitBreakerMetrics(
      CircuitBreakerRegistry registry,
      MeterRegistry meterRegistry) {
    TaggedCircuitBreakerMetrics metrics = TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(registry);
    meterRegistry.getMeters().forEach(meterRegistry::remove);
    metrics.bindTo(meterRegistry);
    return metrics;
  }

  /**
   * Event consumer for monitoring circuit breaker lifecycle events.
   *
   * @return RegistryEventConsumer that logs all circuit breaker events
   */
  @Bean
  public RegistryEventConsumer<CircuitBreaker> circuitBreakerEventConsumer() {
    return new RegistryEventConsumer<CircuitBreaker>() {
      @Override
      public void onEntryAdded(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
        CircuitBreaker circuitBreaker = entryAddedEvent.getAddedEntry();
        LOG.info("Circuit breaker created: {} with config: failureRate={}%, slowCallRate={}%",
            circuitBreaker.getName(),
            circuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold(),
            circuitBreaker.getCircuitBreakerConfig().getSlowCallRateThreshold());
      }

      @Override
      public void onEntryRemoved(EntryRemovedEvent<CircuitBreaker> entryRemovedEvent) {
        LOG.info("Circuit breaker removed: {}", entryRemovedEvent.getRemovedEntry().getName());
      }
    };
  }
}
