package com.example.cicsgenapp.dto;

import com.example.cicsgenapp.validator.ValidAge;
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
public class UpdateCustomerRequest {

  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  @JsonProperty("firstName")
  private String firstName;

  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  @JsonProperty("lastName")
  private String lastName;

  @PastOrPresent(message = "Date of birth must be in the past or today")
  @ValidAge(message = "Customer must be at least 18 years old")
  @JsonProperty("dateOfBirth")
  private LocalDate dateOfBirth;

  @Email(message = "Email must be a valid email address")
  @JsonProperty("email")
  private String email;

  @Pattern(
      regexp = "^\\+?[1-9]\\d{1,14}$|^$",
      message = "Phone number must be in valid international format (E.164) or empty")
  @JsonProperty("phone")
  private String phone;

  @Size(max = 255, message = "Address must not exceed 255 characters")
  @JsonProperty("address")
  private String address;

  @Size(max = 100, message = "City must not exceed 100 characters")
  @JsonProperty("city")
  private String city;

  @Size(max = 50, message = "State must not exceed 50 characters")
  @JsonProperty("state")
  private String state;

  @Size(max = 10, message = "Zip code must not exceed 10 characters")
  @JsonProperty("zipCode")
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
