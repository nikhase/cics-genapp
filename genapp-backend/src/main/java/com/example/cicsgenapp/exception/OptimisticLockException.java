package com.example.cicsgenapp.exception;

/**
 * Exception thrown when an optimistic locking conflict is detected during an update operation.
 *
 * <p>This exception indicates that the resource was modified by another concurrent request.
 * Typically mapped to HTTP 409 Conflict response by GlobalExceptionHandler.
 *
 * <p>Causes:
 * - Customer entity version field doesn't match the expected version
 * - Another user updated the customer between this user's read and write operations
 */
public class OptimisticLockException extends RuntimeException {

  /**
   * Constructs a new OptimisticLockException with the specified detail message.
   *
   * @param message the detail message
   */
  public OptimisticLockException(String message) {
    super(message);
  }

  /**
   * Constructs a new OptimisticLockException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public OptimisticLockException(String message, Throwable cause) {
    super(message, cause);
  }
}
