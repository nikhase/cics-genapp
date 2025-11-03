package com.example.cicsgenapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for customer creation request.
 *
 * <p>Accepts POST request body with customer information. Includes validation annotations
 * to enforce field constraints before processing.
 */
public class CreateCustomerRequest {

  @NotNull(message = "First name is required")
  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @JsonProperty("firstName")
  private String firstName;

  @NotNull(message = "Last name is required")
  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("dateOfBirth")
  private LocalDate dateOfBirth;

  @NotNull(message = "Email is required")
  @Email(message = "Email must be a valid email address")
  @JsonProperty("email")
  private String email;

  @Pattern(
      regexp = "^\\+?[1-9]\\d{1,14}$|^$",
      message = "Phone number must be in valid international format (E.164) or empty")
  @JsonProperty("phone")
  private String phone;

  @JsonProperty("address")
  private String address;

  @JsonProperty("city")
  private String city;

  @JsonProperty("state")
  private String state;

  @JsonProperty("zipCode")
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
