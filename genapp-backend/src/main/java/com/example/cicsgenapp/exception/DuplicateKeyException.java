package com.example.cicsgenapp.exception;

/**
 * Exception thrown when attempting to create or modify a resource with a duplicate key/identifier.
 *
 * <p>This exception is typically mapped to HTTP 409 Conflict response.
 */
public class DuplicateKeyException extends RuntimeException {

  /**
   * Constructs a new DuplicateKeyException with the specified detail message.
   *
   * @param message the detail message
   */
  public DuplicateKeyException(String message) {
    super(message);
  }

  /**
   * Constructs a new DuplicateKeyException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public DuplicateKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
