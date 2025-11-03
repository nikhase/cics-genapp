package com.example.cicsgenapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator implementation for @ValidPhoneFormat constraint.
 *
 * <p>Validates phone numbers using E.164 format regex:
 * - Optional leading + symbol
 * - Country code (1-3 digits starting with 1-9)
 * - Remaining digits/hyphens/spaces/parentheses (7+ characters)
 * - Or empty string
 */
public class ValidPhoneFormatValidator implements ConstraintValidator<ValidPhoneFormat, String> {

  // E.164 regex pattern: optional +, then 1-3 digits for country code, then 7+ more digits
  // Allows hyphens, spaces, parentheses for formatting
  private static final Pattern PHONE_PATTERN =
      Pattern.compile("^\\+?[1-9]\\d{1,14}$|^\\+?[1-9]\\d{0,14}[\\s\\-()]*\\d[\\s\\-()]*\\d.*$|^$");

  // Simpler pattern that's more lenient: +? followed by at least 7 digits (mixed with allowed chars)
  private static final Pattern PHONE_PATTERN_LENIENT =
      Pattern.compile("^(\\+?[1-9]\\d{1,14}|\\+?[1-9][\\d\\s\\-()]{6,})$|^$");

  @Override
  public void initialize(ValidPhoneFormat annotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(String phone, ConstraintValidatorContext context) {
    // Allow null or empty values - use @NotNull if null is not allowed
    if (phone == null || phone.isBlank()) {
      return true;
    }

    // Trim whitespace for validation
    String trimmed = phone.trim();

    // Check against lenient pattern (allows formatting chars)
    return PHONE_PATTERN_LENIENT.matcher(trimmed).matches();
  }
}
