package com.example.cicsgenapp.gateway.filter;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global gateway filter for structured JSON logging of all requests and responses.
 *
 * This filter logs:
 * - Request metadata: method, path, headers, client IP, user agent
 * - Response metadata: status code, latency, route destination
 * - All logs are in JSON format with traceId field for ELK Stack aggregation
 *
 * Logging is performed at:
 * - INFO level for normal successful requests
 * - WARN level for client errors (4xx)
 * - ERROR level for server errors (5xx)
 */
@Component
public class GatewayLoggingFilter implements GlobalFilter, Ordered {

  private static final Logger LOG = LoggerFactory.getLogger(GatewayLoggingFilter.class);
  private static final String TRACE_ID_HEADER = "X-Trace-Id";
  private static final String REQUEST_TIME_ATTRIBUTE = "requestStartTime";

  /**
   * Processes requests and responses with structured JSON logging.
   *
   * @param exchange the ServerWebExchange
   * @param chain the GatewayFilterChain
   * @return Mono<Void> representing the completion of the filter chain
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    // Extract or generate trace ID
    String traceId = extractTraceId(request);

    // Store request start time for latency calculation
    long startTime = System.currentTimeMillis();
    exchange.getAttributes().put(REQUEST_TIME_ATTRIBUTE, startTime);

    // Set trace ID in MDC for all downstream logs
    MDC.put("traceId", traceId);
    MDC.put("requestId", traceId); // Alias for compatibility

    // Log incoming request
    logRequest(request, traceId);

    // Continue with the filter chain and log response
    return chain.filter(exchange)
        .then(Mono.fromRunnable(() -> {
          long endTime = System.currentTimeMillis();
          long latency = endTime - startTime;

          logResponse(request, response, latency, traceId);

          // Clean up MDC
          MDC.remove("traceId");
          MDC.remove("requestId");
        }));
  }

  /**
   * Extracts trace ID from request header or generates a new UUID.
   *
   * @param request the ServerHttpRequest
   * @return traceId string
   */
  private String extractTraceId(ServerHttpRequest request) {
    String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
    if (traceId == null || traceId.trim().isEmpty()) {
      traceId = UUID.randomUUID().toString();
    }
    return traceId;
  }

  /**
   * Logs structured JSON for incoming request.
   *
   * @param request the ServerHttpRequest
   * @param traceId correlation ID
   */
  private void logRequest(ServerHttpRequest request, String traceId) {
    String method = request.getMethod() != null ? request.getMethod().toString() : "UNKNOWN";
    String path = request.getPath().toString();
    String clientIp = getClientIp(request);
    String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);

    String logMessage = String.format(
        "{\"level\":\"INFO\",\"timestamp\":\"%s\",\"traceId\":\"%s\",\"type\":\"GATEWAY_REQUEST\","
            + "\"method\":\"%s\",\"path\":\"%s\",\"clientIp\":\"%s\",\"userAgent\":\"%s\"}",
        java.time.Instant.now(),
        traceId,
        method,
        path,
        clientIp != null ? clientIp : "unknown",
        userAgent != null ? userAgent : "unknown");

    LOG.info(logMessage);
  }

  /**
   * Logs structured JSON for outgoing response.
   *
   * @param request the ServerHttpRequest
   * @param response the ServerHttpResponse
   * @param latency request processing time in milliseconds
   * @param traceId correlation ID
   */
  private void logResponse(ServerHttpRequest request,
                          ServerHttpResponse response,
                          long latency,
                          String traceId) {
    String method = request.getMethod() != null ? request.getMethod().toString() : "UNKNOWN";
    String path = request.getPath().toString();
    int statusCode = response.getStatusCode() != null
        ? response.getStatusCode().value()
        : 0;

    // Determine log level based on HTTP status code
    String level = getLogLevel(statusCode);

    String logMessage = String.format(
        "{\"level\":\"%s\",\"timestamp\":\"%s\",\"traceId\":\"%s\",\"type\":\"GATEWAY_RESPONSE\","
            + "\"method\":\"%s\",\"path\":\"%s\",\"statusCode\":%d,\"latencyMs\":%d}",
        level,
        java.time.Instant.now(),
        traceId,
        method,
        path,
        statusCode,
        latency);

    // Log at appropriate level
    switch (level) {
      case "WARN":
        LOG.warn(logMessage);
        break;
      case "ERROR":
        LOG.error(logMessage);
        break;
      default:
        LOG.info(logMessage);
    }
  }

  /**
   * Determines log level based on HTTP status code.
   *
   * @param statusCode HTTP status code
   * @return log level string: "INFO", "WARN", or "ERROR"
   */
  private String getLogLevel(int statusCode) {
    if (statusCode >= 500) {
      return "ERROR";
    } else if (statusCode >= 400) {
      return "WARN";
    }
    return "INFO";
  }

  /**
   * Extracts client IP address from request headers.
   *
   * Checks X-Forwarded-For header (for proxied requests) before falling back to
   * remote address.
   *
   * @param request the ServerHttpRequest
   * @return client IP address or null
   */
  private String getClientIp(ServerHttpRequest request) {
    String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      // Take the first IP if there are multiple (comma-separated)
      return xForwardedFor.split(",")[0].trim();
    }

    if (request.getRemoteAddress() != null) {
      return request.getRemoteAddress().getHostString();
    }

    return null;
  }

  /**
   * Filter order: Execute this filter early (before other filters).
   *
   * @return filter order (lower values execute first)
   */
  @Override
  public int getOrder() {
    // Execute before route filters (which have order 0)
    return -1;
  }
}
