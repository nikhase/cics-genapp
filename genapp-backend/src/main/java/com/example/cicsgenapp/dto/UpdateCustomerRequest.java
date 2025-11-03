package com.example.cicsgenapp.dto;

import com.example.cicsgenapp.validator.ValidAge;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for customer update request.
 *
 * <p>Accepts PUT request body with customer update information. All fields are optional to support
 * partial updates. Only non-null fields will be updated. Includes validation annotations to enforce
 * field constraints when provided.
 */
@Schema(
    title = "Update Customer Request",
    description = "Request body for updating a customer. All fields are optional (null fields are not updated).")
public class UpdateCustomerRequest {

  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @JsonProperty("firstName")
  @Schema(description = "Customer first name (optional, 1-100 characters)", example = "Jane")
  private String firstName;

  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @JsonProperty("lastName")
  @Schema(description = "Customer last name (optional, 1-100 characters)", example = "Smith")
  private String lastName;

  @PastOrPresent(message = "Date of birth must be in the past or today")
  @ValidAge(message = "Customer must be at least 18 years old")
  @JsonProperty("dateOfBirth")
  @Schema(description = "Customer date of birth (optional, must be at least 18 years old)", example = "1990-05-15")
  private LocalDate dateOfBirth;

  @Email(message = "Email must be a valid email address")
  @JsonProperty("email")
  @Schema(description = "Customer email address (optional, must be unique if provided)", format = "email", example = "jane.smith@example.com")
  private String email;

  @Pattern(
      regexp = "^\\+?[1-9]\\d{1,14}$|^$",
      message = "Phone number must be in valid international format (E.164) or empty")
  @JsonProperty("phone")
  @Schema(description = "Customer phone number in E.164 format (optional)", example = "+1-555-123-4567")
  private String phone;

  @Size(max = 255, message = "Address must not exceed 255 characters")
  @JsonProperty("address")
  @Schema(description = "Street address (optional, max 255 characters)", example = "123 Main Street")
  private String address;

  @Size(max = 100, message = "City must not exceed 100 characters")
  @JsonProperty("city")
  @Schema(description = "City name (optional, max 100 characters)", example = "Springfield")
  private String city;

  @Size(max = 50, message = "State must not exceed 50 characters")
  @JsonProperty("state")
  @Schema(description = "State or province code (optional, max 50 characters)", example = "IL")
  private String state;

  @Size(max = 10, message = "Zip code must not exceed 10 characters")
  @JsonProperty("zipCode")
  @Schema(description = "Postal code (optional, max 10 characters)", example = "62701")
  private String zipCode;

  /**
   * Default constructor for JSON deserialization.
   */
  public UpdateCustomerRequest() {
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
    return "UpdateCustomerRequest{"
        + "firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + '}';
  }
}
