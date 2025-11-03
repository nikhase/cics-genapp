package com.example.cicsgenapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Validator implementation for @ValidAge constraint.
 *
 * <p>Checks that the date of birth is at least 18 years before today.
 */
public class ValidAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

  private static final int MIN_AGE = 18;

  @Override
  public void initialize(ValidAge annotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
    // Allow null values - use @NotNull or @PastOrPresent if null is not allowed
    if (dateOfBirth == null) {
      return true;
    }

    // Calculate age
    long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());

    // Check if age is at least 18
    return age >= MIN_AGE;
  }
}
