package com.example.cicsgenapp.dto;

import com.example.cicsgenapp.validator.ValidAge;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for customer creation request.
 *
 * <p>Accepts POST request body with customer information. Includes validation annotations
 * to enforce field constraints before processing.
 */
@Schema(
    title = "Create Customer Request",
    description = "Request body for creating a new customer. All required fields must be provided.")
public class CreateCustomerRequest {

  @NotNull(message = "First name is required")
  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @JsonProperty("firstName")
  @Schema(description = "Customer first name (required, 1-100 characters)", example = "Jane")
  private String firstName;

  @NotNull(message = "Last name is required")
  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @JsonProperty("lastName")
  @Schema(description = "Customer last name (required, 1-100 characters)", example = "Smith")
  private String lastName;

  @PastOrPresent(message = "Date of birth must be in the past or today")
  @ValidAge(message = "Customer must be at least 18 years old")
  @JsonProperty("dateOfBirth")
  @Schema(description = "Customer date of birth (optional, must be at least 18 years old)", example = "1990-05-15")
  private LocalDate dateOfBirth;

  @NotNull(message = "Email is required")
  @Email(message = "Email must be a valid email address")
  @JsonProperty("email")
  @Schema(description = "Customer email address (required, must be unique and valid email format)", format = "email", example = "jane.smith@example.com")
  private String email;

  @Pattern(
      regexp = "^\\+?[1-9]\\d{1,14}$|^$",
      message = "Phone number must be in valid international format (E.164) or empty")
  @JsonProperty("phone")
  @Schema(description = "Customer phone number in E.164 format (optional)", example = "+1-555-123-4567")
  private String phone;

  @JsonProperty("address")
  @Schema(description = "Street address (optional)", example = "123 Main Street")
  private String address;

  @JsonProperty("city")
  @Schema(description = "City name (optional)", example = "Springfield")
  private String city;

  @JsonProperty("state")
  @Schema(description = "State or province code (optional)", example = "IL")
  private String state;

  @JsonProperty("zipCode")
  @Schema(description = "Postal code (optional)", example = "62701")
  private String zipCode;

  /**
   * Default constructor for JSON deserialization.
   */
  public CreateCustomerRequest() {
  }

  /**
   * Constructor with required fields.
   */
  public CreateCustomerRequest(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  // Getters and setters

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  @Override
  public String toString() {
    return "CreateCustomerRequest{"
        + "firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + ", phone='" + phone + '\''
        + '}';
  }
}
