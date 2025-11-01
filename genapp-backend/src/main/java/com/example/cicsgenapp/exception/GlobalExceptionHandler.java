package com.example.cicsgenapp.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for consistent error response formatting.
 *
 * <p>Handles all application exceptions and formats them into standardized JSON error responses
 * with HTTP status codes, error codes, messages, and optional field-level details.
 *
 * <p>All error responses include:
 * <ul>
 *   <li>error.code - Machine-readable error code
 *   <li>error.message - User-friendly error message
 *   <li>error.details - Field-level errors (if applicable)
 *   <li>metadata.timestamp - When the error occurred
 *   <li>metadata.traceId - Correlation ID for support reference
 * </ul>
 *
 * @author Development Team
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Error response envelope structure.
   */
  public record ErrorResponse(
      ErrorDetails error,
      ErrorMetadata metadata
  ) {}

  /**
   * Error details with code, message, and field-level information.
   */
  public record ErrorDetails(
      String code,
      String message,
      List<FieldError> details
  ) {}

  /**
   * Field-level error information.
   */
  public record FieldError(
      String field,
      String message,
      Object value
  ) {}

  /**
   * Error metadata with timestamp and correlation ID.
   */
  public record ErrorMetadata(
      LocalDateTime timestamp,
      String traceId
  ) {}

  /**
   * Handles ResourceNotFoundException - returns 404 Not Found.
   *
   * @param ex the exception
   * @param request the web request
   * @return 404 error response
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex,
      WebRequest request) {

    ErrorResponse error = createErrorResponse(
        "RESOURCE_NOT_FOUND",
        ex.getMessage(),
        HttpStatus.NOT_FOUND,
        null
    );

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(error);
  }

  /**
   * Handles DuplicateKeyException - returns 409 Conflict.
   *
   * @param ex the exception
   * @param request the web request
   * @return 409 error response
   */
  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateKey(
      DuplicateKeyException ex,
      WebRequest request) {

    ErrorResponse error = createErrorResponse(
        "DUPLICATE_KEY",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        null
    );

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(error);
  }

  /**
   * Handles ValidationException - returns 400 Bad Request.
   *
   * @param ex the exception
   * @param request the web request
   * @return 400 error response
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      ValidationException ex,
      WebRequest request) {

    ErrorResponse error = createErrorResponse(
        "VALIDATION_ERROR",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        null
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(error);
  }

  /**
   * Handles MethodArgumentNotValidException from @Valid annotation - returns 400 Bad Request.
   *
   * @param ex the exception
   * @param request the web request
   * @return 400 error response with field-level details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      WebRequest request) {

    List<FieldError> fieldErrors = new ArrayList<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error ->
            fieldErrors.add(new FieldError(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ))
        );

    ErrorDetails details = new ErrorDetails(
        "VALIDATION_ERROR",
        "Validation failed",
        fieldErrors
    );

    ErrorMetadata metadata = new ErrorMetadata(
        LocalDateTime.now(),
        getOrCreateTraceId()
    );

    ErrorResponse error = new ErrorResponse(details, metadata);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(error);
  }

  /**
   * Handles all other exceptions - returns 500 Internal Server Error.
   *
   * @param ex the exception
   * @param request the web request
   * @return 500 error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex,
      WebRequest request) {

    ErrorResponse error = createErrorResponse(
        "INTERNAL_SERVER_ERROR",
        "An unexpected error occurred. Please contact support with trace ID.",
        HttpStatus.INTERNAL_SERVER_ERROR,
        null
    );

    // Log the actual error for debugging
    String traceId = getOrCreateTraceId();
    org.slf4j.LoggerFactory.getLogger(this.getClass())
        .error("Unexpected error with traceId: {}", traceId, ex);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error);
  }

  /**
   * Creates a standardized error response.
   *
   * @param code the error code
   * @param message the error message
   * @param status the HTTP status
   * @param fieldErrors optional field-level errors
   * @return error response
   */
  private ErrorResponse createErrorResponse(
      String code,
      String message,
      HttpStatus status,
      List<FieldError> fieldErrors) {

    ErrorDetails details = new ErrorDetails(code, message, fieldErrors);
    ErrorMetadata metadata = new ErrorMetadata(
        LocalDateTime.now(),
        getOrCreateTraceId()
    );

    return new ErrorResponse(details, metadata);
  }

  /**
   * Gets the trace ID from MDC or creates a new one if not present.
   *
   * <p>The trace ID should have been set by LoggingFilter for all requests.
   * This method ensures there's always a trace ID in error responses.
   *
   * @return the trace ID
   */
  private String getOrCreateTraceId() {
    String traceId = MDC.get("traceId");
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
      MDC.put("traceId", traceId);
    }
    return traceId;
  }
}
