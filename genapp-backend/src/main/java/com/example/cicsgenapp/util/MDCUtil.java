package com.example.cicsgenapp.util;

import java.util.UUID;
import org.slf4j.MDC;

/**
 * Utility class for managing Mapped Diagnostic Context (MDC) trace IDs.
 *
 * <p>Provides convenience methods for setting and retrieving correlation IDs (trace IDs) in the MDC.
 * Trace IDs are used to track requests through the system for logging and debugging purposes.
 */
public class MDCUtil {

  private static final String TRACE_ID_KEY = "traceId";

  /**
   * Gets the current trace ID from MDC, or creates a new one if not present.
   *
   * @return the trace ID (either existing or newly generated UUID)
   */
  public static String getOrCreateTraceId() {
    String traceId = MDC.get(TRACE_ID_KEY);
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
      setTraceId(traceId);
    }
    return traceId;
  }

  /**
   * Gets the current trace ID from MDC, returning null if not present.
   *
   * @return the trace ID, or null if not set
   */
  public static String getTraceId() {
    return MDC.get(TRACE_ID_KEY);
  }

  /**
   * Sets the trace ID in MDC.
   *
   * @param traceId the trace ID to set
   */
  public static void setTraceId(String traceId) {
    if (traceId != null) {
      MDC.put(TRACE_ID_KEY, traceId);
    }
  }

  /**
   * Clears the trace ID from MDC.
   */
  public static void clearTraceId() {
    MDC.remove(TRACE_ID_KEY);
  }

  /**
   * Generates a new trace ID.
   *
   * @return a new UUID-based trace ID
   */
  public static String generateTraceId() {
    return UUID.randomUUID().toString();
  }

  private MDCUtil() {
    // Utility class - prevent instantiation
  }
}
