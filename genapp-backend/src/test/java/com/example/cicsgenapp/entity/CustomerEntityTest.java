package com.example.cicsgenapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Customer entity validation and field management.
 * Tests entity instantiation, field assignment, and validation annotations.
 */
class CustomerEntityTest {

  private Validator validator;
  private Customer customer;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    customer = new Customer("John", "Doe", "john@example.com");
  }

  // ========== Field Assignment Tests ==========

  @Test
  void testCustomerCreationWithRequiredFields() {
    assertNotNull(customer.getFirstName());
    assertNotNull(customer.getLastName());
    assertNotNull(customer.getEmail());
    assertEquals("John", customer.getFirstName());
    assertEquals("Doe", customer.getLastName());
    assertEquals("john@example.com", customer.getEmail());
  }

  @Test
  void testCustomerDefaultStatus() {
    assertEquals(Status.ACTIVE, customer.getStatus());
  }

  @Test
  void testAllFieldsCanBeSet() {
    UUID testId = UUID.randomUUID();
    LocalDate dob = LocalDate.of(1990, 5, 15);
    String phone = "+1-555-0123";
    String address = "123 Main St";
    String city = "Springfield";
    String state = "IL";
    String zipCode = "62701";

    customer.setCustomerId(testId);
    customer.setDateOfBirth(dob);
    customer.setPhone(phone);
    customer.setAddress(address);
    customer.setCity(city);
    customer.setState(state);
    customer.setZipCode(zipCode);
    customer.setStatus(Status.INACTIVE);
    customer.setCreatedBy("testuser");
    customer.setUpdatedBy("testuser");

    assertEquals(testId, customer.getCustomerId());
    assertEquals(dob, customer.getDateOfBirth());
    assertEquals(phone, customer.getPhone());
    assertEquals(address, customer.getAddress());
    assertEquals(city, customer.getCity());
    assertEquals(state, customer.getState());
    assertEquals(zipCode, customer.getZipCode());
    assertEquals(Status.INACTIVE, customer.getStatus());
    assertEquals("testuser", customer.getCreatedBy());
    assertEquals("testuser", customer.getUpdatedBy());
  }

  // ========== Validation Tests ==========

  @Test
  void testValidCustomerPassesValidation() {
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertTrue(violations.isEmpty(), "Valid customer should have no constraint violations");
  }

  @Test
  void testFirstNameNotNullConstraint() {
    customer.setFirstName(null);
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertViolationPresent(violations, "firstName");
  }

  @Test
  void testLastNameNotNullConstraint() {
    customer.setLastName(null);
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertViolationPresent(violations, "lastName");
  }

  @Test
  void testEmailNotNullConstraint() {
    customer.setEmail(null);
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertViolationPresent(violations, "email");
  }

  @Test
  void testEmailFormatValidation() {
    customer.setEmail("invalid-email");
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertViolationPresent(violations, "email");
  }

  @Test
  void testEmailFormatWithValidDomainPasses() {
    customer.setEmail("jane.doe+tag@example.com");
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertTrue(violations.stream()
        .noneMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void testPhoneFormatValidation() {
    customer.setPhone("invalid");
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertViolationPresent(violations, "phone");
  }

  @Test
  void testPhoneFormatWithValidInternationalFormat() {
    customer.setPhone("+1-555-0123");
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertTrue(violations.stream()
        .noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
  }

  @Test
  void testPhoneFormatWithValidDomesticUSFormat() {
    customer.setPhone("5550123");
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertTrue(violations.stream()
        .noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
  }

  @Test
  void testPhoneCanBeEmpty() {
    customer.setPhone(null);
    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertTrue(violations.stream()
        .noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
  }

  @Test
  void testOptionalFieldsCanBeNull() {
    customer.setDateOfBirth(null);
    customer.setPhone(null);
    customer.setAddress(null);
    customer.setCity(null);
    customer.setState(null);
    customer.setZipCode(null);

    assertNull(customer.getDateOfBirth());
    assertNull(customer.getPhone());
    assertNull(customer.getAddress());
    assertNull(customer.getCity());
    assertNull(customer.getState());
    assertNull(customer.getZipCode());

    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
    assertTrue(violations.isEmpty());
  }

  // ========== Equals and HashCode Tests ==========

  @Test
  void testEqualsBasedOnCustomerId() {
    UUID customerId = UUID.randomUUID();
    customer.setCustomerId(customerId);

    Customer customer2 = new Customer("Jane", "Doe", "jane@example.com");
    customer2.setCustomerId(customerId);

    assertEquals(customer, customer2);
  }

  @Test
  void testNotEqualsWithDifferentCustomerId() {
    customer.setCustomerId(UUID.randomUUID());
    Customer customer2 = new Customer("Jane", "Doe", "jane@example.com");
    customer2.setCustomerId(UUID.randomUUID());

    assertNotEquals(customer, customer2);
  }

  @Test
  void testHashCodeEqualForSameCustomerId() {
    UUID customerId = UUID.randomUUID();
    customer.setCustomerId(customerId);

    Customer customer2 = new Customer("Jane", "Doe", "jane@example.com");
    customer2.setCustomerId(customerId);

    assertEquals(customer.hashCode(), customer2.hashCode());
  }

  @Test
  void testEqualsWithNull() {
    assertNotEquals(customer, null);
  }

  @Test
  void testEqualsWithDifferentClass() {
    assertNotEquals(customer, "notACustomer");
  }

  // ========== toString Tests ==========

  @Test
  void testToStringContainsRelevantFields() {
    String toString = customer.toString();
    assertTrue(toString.contains("Customer"));
    assertTrue(toString.contains("firstName"));
    assertTrue(toString.contains("John"));
    assertTrue(toString.contains("lastName"));
    assertTrue(toString.contains("Doe"));
    assertTrue(toString.contains("email"));
    assertTrue(toString.contains("john@example.com"));
  }

  // ========== Helper Methods ==========

  private void assertViolationPresent(Set<ConstraintViolation<Customer>> violations,
      String propertyPath) {
    assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals(propertyPath)),
        "Expected violation for property: " + propertyPath);
  }
}
