package com.example.cicsgenapp.exception;

/**
 * Exception thrown when request validation fails.
 *
 * <p>This exception is typically mapped to HTTP 400 Bad Request response.
 */
public class ValidationException extends RuntimeException {

  /**
   * Constructs a new ValidationException with the specified detail message.
   *
   * @param message the detail message
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a new ValidationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
