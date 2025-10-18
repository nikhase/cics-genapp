package com.genapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling for all REST endpoints
 *
 * Replaces the error handling pattern from COBOL:
 *   COBOL: RESP codes, RESP2 codes, manual error checking on every EXEC CICS call
 *   Java: Exceptions and @ControllerAdvice for centralized handling
 *
 * Old COBOL Error Handling:
 *   EXEC CICS READ FILE('KSDSCUST')
 *       INTO WS-CUST-REC
 *       RESP WS-RESP
 *       RESP2 WS-RESP2
 *   END-EXEC.
 *   EVALUATE WS-RESP
 *       WHEN DFHRESP(NORMAL)     → success
 *       WHEN DFHRESP(NOTFND)     → not found error
 *       WHEN DFHRESP(NOTOPEN)    → file not open error
 *       WHEN DFHRESP(ILLOGIC)    → logic error
 *   END-EVALUATE.
 *
 * New Java Error Handling:
 *   throw new IllegalArgumentException(...) → caught by handler
 *   → returns ResponseEntity with proper HTTP status and error details
 *
 * Benefits:
 *   ✅ Consistent error responses across all endpoints
 *   ✅ Automatic validation error messages
 *   ✅ Proper HTTP status codes (400, 404, 409, 500)
 *   ✅ JSON error format with timestamp and details
 *   ✅ No need to check status codes on every call
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (400 Bad Request)
     *
     * Triggered when @Valid validation fails on @RequestBody
     *
     * Example:
     *   POST /api/customers with invalid email → MethodArgumentNotValidException
     *   → returns 400 with field validation errors
     *
     * @param ex validation exception
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle resource not found errors (404 Not Found)
     *
     * Triggered when customer is not found
     *
     * Example:
     *   GET /api/customers/999 where 999 doesn't exist
     *   → IllegalArgumentException("Customer not found with ID: 999")
     *   → returns 404
     *
     * @param ex exception with error message
     * @return ResponseEntity with 404 status and error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            IllegalArgumentException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle duplicate resource errors (409 Conflict)
     *
     * Triggered when trying to create customer with existing email
     *
     * Old COBOL equivalent: Check if customer already exists and return error code
     *
     * @param ex exception with error message
     * @return ResponseEntity with 409 status and error message
     */
    @ExceptionHandler(value = {})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleConflictException(Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("already exists")) {
            log.warn("Conflict: {}", ex.getMessage());

            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    ex.getMessage(),
                    null,
                    LocalDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        return handleGeneralException(ex);
    }

    /**
     * Handle all other unexpected exceptions (500 Internal Server Error)
     *
     * @param ex unexpected exception
     * @return ResponseEntity with 500 status and error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * ErrorResponse - Standard error response format for all API errors
     *
     * Old COBOL approach: Return numeric error codes
     * New approach: Return JSON with timestamp, message, and validation details
     */
    public record ErrorResponse(
            int status,
            String message,
            Map<String, String> errors,
            LocalDateTime timestamp
    ) {}
}
