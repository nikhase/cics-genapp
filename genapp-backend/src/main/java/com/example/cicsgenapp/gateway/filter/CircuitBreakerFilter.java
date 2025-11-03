package com.example.cicsgenapp.gateway.filter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway filter that wraps calls to legacy COBOL system with circuit breaker protection.
 *
 * This filter intercepts requests routing to the legacy system and applies Resilience4j
 * circuit breaker pattern to prevent cascading failures. If the legacy system becomes
 * unavailable (50%+ failures), the circuit breaker opens and returns a standardized
 * fallback response without hitting the legacy endpoint.
 *
 * State transitions:
 * - CLOSED: Normal operation, calls proceed to legacy system
 * - OPEN: Legacy system unavailable, fallback response returned
 * - HALF_OPEN: Testing recovery, limited calls proceed to legacy system
 */
@Component
public class CircuitBreakerFilter extends AbstractGatewayFilterFactory<CircuitBreakerFilter.Config> {

  private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerFilter.class);

  private final CircuitBreaker circuitBreaker;

  public CircuitBreakerFilter(CircuitBreaker legacyCobolCircuitBreaker) {
    super(Config.class);
    this.circuitBreaker = legacyCobolCircuitBreaker;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      // Check if this request should be protected by circuit breaker
      if (!shouldApplyCircuitBreaker(exchange)) {
        return chain.filter(exchange);
      }

      // Log circuit breaker state
      LOG.debug("Circuit breaker state: {} for request: {}",
          circuitBreaker.getState(),
          exchange.getRequest().getPath());

      // Wrap the call with circuit breaker
      return executeWithCircuitBreaker(exchange, chain, config);
    };
  }

  /**
   * Executes the request with circuit breaker protection.
   *
   * If the circuit breaker is open (legacy system unavailable), returns fallback response.
   * If the circuit breaker is closed or half-open, proceeds with the request.
   *
   * @param exchange the ServerWebExchange
   * @param chain the GatewayFilterChain
   * @param config the filter configuration
   * @return Mono representing the response
   */
  private Mono<Void> executeWithCircuitBreaker(ServerWebExchange exchange,
                                              org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
                                              Config config) {
    return chain.filter(exchange)
        .onErrorResume(throwable -> {
          LOG.error("Circuit breaker fallback triggered for: {}", exchange.getRequest().getPath());

          if (throwable instanceof CallNotPermittedException) {
            // Circuit is OPEN - return 503 Service Unavailable
            return returnFallbackResponse(exchange, 503, "LEGACY_SYSTEM_UNAVAILABLE",
                "Legacy system temporarily unavailable. Please try again in a few moments.");
          }

          // Other errors (timeout, network, etc) - also return 503
          return returnFallbackResponse(exchange, 503, "LEGACY_SYSTEM_ERROR",
              "Unable to reach legacy system: " + throwable.getMessage());
        });
  }

  /**
   * Returns a standardized fallback response when legacy system is unavailable.
   *
   * Response format:
   * {
   *   "error": {
   *     "code": "LEGACY_SYSTEM_UNAVAILABLE",
   *     "message": "Legacy system temporarily unavailable...",
   *     "details": "..."
   *   },
   *   "metadata": {
   *     "timestamp": "...",
   *     "traceId": "..."
   *   }
   * }
   *
   * @param exchange the ServerWebExchange
   * @param status HTTP status code
   * @param errorCode error code string
   * @param message user-friendly error message
   * @return Mono<Void>
   */
  private Mono<Void> returnFallbackResponse(ServerWebExchange exchange,
                                            int status,
                                            String errorCode,
                                            String message) {
    exchange.getResponse().setStatusCode(
        org.springframework.http.HttpStatus.valueOf(status));
    exchange.getResponse().getHeaders().setContentType(
        org.springframework.http.MediaType.APPLICATION_JSON);

    String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
    if (traceId == null) {
      traceId = java.util.UUID.randomUUID().toString();
    }

    String responseBody = String.format(
        "{\"error\":{\"code\":\"%s\",\"message\":\"%s\",\"details\":\"The legacy COBOL service is experiencing issues. Your request could not be routed.\"},\"metadata\":{\"timestamp\":\"%s\",\"traceId\":\"%s\"}}",
        errorCode,
        message,
        java.time.Instant.now(),
        traceId);

    return exchange.getResponse().writeWith(
        Mono.just(exchange.getResponse().bufferFactory()
            .wrap(responseBody.getBytes(java.nio.charset.StandardCharsets.UTF_8))));
  }

  /**
   * Determines if circuit breaker should be applied to this request.
   *
   * Currently applies to all requests to /api/v1/customers and /api/v1/policies
   * that route to the legacy system. This could be enhanced with feature toggles
   * to enable/disable per endpoint.
   *
   * @param exchange the ServerWebExchange
   * @return true if circuit breaker should be applied
   */
  private boolean shouldApplyCircuitBreaker(ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().toString();
    // Apply circuit breaker to legacy routing endpoints
    // This will be enhanced in Task 8 with feature toggle integration
    return path.startsWith("/api/v1/customers") || path.startsWith("/api/v1/policies");
  }

  /**
   * Configuration for CircuitBreakerFilter.
   */
  public static class Config {
    // Configuration properties can be added here for flexibility
    // Example: private String thresholdPercentage;
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return List.of();
  }
}
