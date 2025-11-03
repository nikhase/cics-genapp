package com.example.cicsgenapp.exception;

/**
 * Exception thrown when attempting to create a customer with a duplicate email address.
 *
 * <p>This exception is typically mapped to HTTP 409 Conflict response by GlobalExceptionHandler.
 * Used to enforce unique email constraint at the application level before database-level errors.
 */
public class CustomerAlreadyExistsException extends DuplicateKeyException {

  /**
   * Constructs a new CustomerAlreadyExistsException with the specified detail message.
   *
   * @param message the detail message
   */
  public CustomerAlreadyExistsException(String message) {
    super(message);
  }

  /**
   * Constructs a new CustomerAlreadyExistsException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public CustomerAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
