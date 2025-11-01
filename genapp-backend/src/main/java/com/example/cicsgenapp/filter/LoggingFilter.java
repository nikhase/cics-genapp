package com.example.cicsgenapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Servlet filter for request/response correlation and structured logging.
 *
 * <p>This filter:
 * <ul>
 *   <li>Extracts or generates a unique traceId for each request
 *   <li>Adds the traceId to SLF4J Mapped Diagnostic Context (MDC)
 *   <li>Propagates traceId through application execution
 *   <li>Includes traceId in all log messages and error responses
 * </ul>
 *
 * <p>The traceId is extracted from the {@code X-Trace-Id} header if present,
 * otherwise a new UUID is generated.
 *
 * @author Development Team
 * @version 1.0.0
 */
@Component
public class LoggingFilter implements Filter {

  private static final String TRACE_ID_HEADER = "X-Trace-Id";
  private static final String TRACE_ID_MDC_KEY = "traceId";
  private static final String REQUEST_ID_HEADER = "X-Request-ID";
  private static final String REQUEST_ID_MDC_KEY = "requestId";

  /**
   * Filters a request and response, adding correlation IDs to MDC.
   *
   * @param servletRequest the servlet request
   * @param servletResponse the servlet response
   * @param filterChain the filter chain
   * @throws IOException if an I/O error occurs
   * @throws ServletException if a servlet error occurs
   */
  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

    // Extract or generate traceId
    String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }

    // Extract or generate requestId
    String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
    if (requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }

    try {
      // Add to MDC for all subsequent logging
      MDC.put(TRACE_ID_MDC_KEY, traceId);
      MDC.put(REQUEST_ID_MDC_KEY, requestId);

      // Continue filter chain with MDC context
      filterChain.doFilter(servletRequest, servletResponse);

    } finally {
      // Clean up MDC to prevent memory leaks
      MDC.remove(TRACE_ID_MDC_KEY);
      MDC.remove(REQUEST_ID_MDC_KEY);
    }
  }
}
