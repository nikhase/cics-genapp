package com.example.cicsgenapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for age constraint.
 *
 * <p>Validates that a date of birth results in age >= 18 years old.
 * Applies to LocalDate fields representing date of birth.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAgeValidator.class)
@Documented
public @interface ValidAge {

  String message() default "Customer must be at least 18 years old";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
