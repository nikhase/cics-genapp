package com.example.cicsgenapp.validator;

import com.example.cicsgenapp.repository.CustomerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validator implementation for @ValidEmail constraint.
 *
 * <p>Validates email uniqueness by checking if the email exists in the database.
 * This validator requires access to CustomerRepository via Spring autowiring.
 */
@Component
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public void initialize(ValidEmail annotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    // Allow null values - use @NotNull if null is not allowed
    if (email == null || email.isBlank()) {
      return true;
    }

    // Check if email already exists
    return !customerRepository.existsByEmail(email);
  }
}
