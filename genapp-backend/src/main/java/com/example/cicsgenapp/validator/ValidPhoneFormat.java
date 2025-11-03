package com.example.cicsgenapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for phone number format.
 *
 * <p>Validates that a phone number follows international E.164 format or is empty.
 * E.164 format: optional leading +, country code, followed by digits, hyphens, spaces, parentheses.
 * Examples: +14155552671, +44 20 7946 0958, (555) 123-4567
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneFormatValidator.class)
@Documented
public @interface ValidPhoneFormat {

  String message() default "Phone number must be in valid international format (E.164) or empty";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
