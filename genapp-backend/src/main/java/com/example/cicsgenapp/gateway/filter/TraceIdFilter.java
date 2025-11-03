package com.example.cicsgenapp.gateway.filter;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global gateway filter for X-Trace-Id correlation ID propagation.
 *
 * This filter ensures every request has a unique trace ID (correlation ID) that flows
 * through the entire request lifecycle:
 * - Request header → Gateway MDC → Downstream service headers → Response header
 *
 * This enables distributed tracing across multiple services and is essential for
 * debugging requests that span multiple service boundaries (Spring Boot → legacy COBOL).
 *
 * Trace ID format: UUID v4 (36 characters)
 */
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

  private static final Logger LOG = LoggerFactory.getLogger(TraceIdFilter.class);
  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  /**
   * Processes each request to ensure trace ID is present and propagated.
   *
   * @param exchange the ServerWebExchange
   * @param chain the GatewayFilterChain
   * @return Mono<Void> representing the completion of the filter chain
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // Extract trace ID from request header or generate new one
    String traceId = extractOrGenerateTraceId(exchange);

    // Add trace ID to request header (for downstream services)
    exchange.getRequest().mutate()
        .header(TRACE_ID_HEADER, traceId)
        .build();

    // Add trace ID to MDC (for logging)
    MDC.put("traceId", traceId);

    // Continue with filter chain
    return chain.filter(exchange)
        .doFinally(signal -> {
          // Add trace ID to response header
          exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, traceId);

          // Clean up MDC
          MDC.remove("traceId");

          LOG.debug("Trace ID {} propagated through request lifecycle", traceId);
        });
  }

  /**
   * Extracts trace ID from request header or generates a new UUID.
   *
   * Priority:
   * 1. If X-Trace-Id header exists and is non-empty, use it
   * 2. Otherwise, generate a new UUID v4
   *
   * @param exchange the ServerWebExchange
   * @return traceId string
   */
  private String extractOrGenerateTraceId(ServerWebExchange exchange) {
    String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);

    if (traceId == null || traceId.trim().isEmpty()) {
      // Generate new UUID for correlation
      traceId = UUID.randomUUID().toString();
      LOG.debug("Generated new trace ID: {}", traceId);
    } else {
      LOG.debug("Using existing trace ID: {}", traceId);
    }

    return traceId;
  }

  /**
   * Filter order: Execute this filter very early to ensure trace ID is available
   * to all subsequent filters.
   *
   * @return filter order (lower values execute first)
   */
  @Override
  public int getOrder() {
    // Execute before GatewayLoggingFilter (order -1)
    return -2;
  }
}
