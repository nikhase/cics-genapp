package com.example.cicsgenapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for email uniqueness and format.
 *
 * <p>Validates that an email address has a valid format AND is unique in the system.
 * This validator can be used on request DTOs to perform async validation against the database.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailValidator.class)
@Documented
public @interface ValidEmail {

  String message() default "Email must be valid and unique";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * Optional customer ID to exclude from uniqueness check (for update scenarios).
   */
  String excludeCustomerId() default "";
}
