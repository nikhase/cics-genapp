package com.example.cicsgenapp.dto;

import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for customer response.
 *
 * <p>Returned in API responses to clients. Maps from Customer JPA entity, excluding sensitive
 * internal fields.
 */
@Schema(
    title = "Customer Response",
    description = "Customer data returned by API, including all customer details and audit fields")
public class CustomerResponse {

  @JsonProperty("customerId")
  @Schema(description = "Unique customer identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID customerId;

  @JsonProperty("firstName")
  @Schema(description = "Customer first name", example = "Jane")
  private String firstName;

  @JsonProperty("lastName")
  @Schema(description = "Customer last name", example = "Smith")
  private String lastName;

  @JsonProperty("dateOfBirth")
  @Schema(description = "Customer date of birth", example = "1990-05-15")
  private LocalDate dateOfBirth;

  @JsonProperty("email")
  @Schema(description = "Customer email address", format = "email", example = "jane.smith@example.com")
  private String email;

  @JsonProperty("phone")
  @Schema(description = "Customer phone number", example = "+1-555-123-4567")
  private String phone;

  @JsonProperty("address")
  @Schema(description = "Street address", example = "123 Main Street")
  private String address;

  @JsonProperty("city")
  @Schema(description = "City name", example = "Springfield")
  private String city;

  @JsonProperty("state")
  @Schema(description = "State or province code", example = "IL")
  private String state;

  @JsonProperty("zipCode")
  @Schema(description = "Postal code", example = "62701")
  private String zipCode;

  @JsonProperty("status")
  @Schema(description = "Customer status (ACTIVE or INACTIVE)", example = "ACTIVE")
  private Status status;

  @JsonProperty("createdAt")
  @Schema(description = "Timestamp when customer record was created", example = "2025-11-01T10:30:00")
  private LocalDateTime createdAt;

  @JsonProperty("updatedAt")
  @Schema(description = "Timestamp when customer record was last updated", example = "2025-11-03T14:45:00")
  private LocalDateTime updatedAt;

  @JsonProperty("createdBy")
  @Schema(description = "User ID who created the customer record", example = "user@example.com")
  private String createdBy;

  @JsonProperty("updatedBy")
  @Schema(description = "User ID who last updated the customer record", example = "user@example.com")
  private String updatedBy;

  /**
   * Default constructor for JSON serialization.
   */
  public CustomerResponse() {
  }

  /**
   * Constructor with common fields.
   */
  public CustomerResponse(UUID customerId, String firstName, String lastName, String email,
      Status status, LocalDateTime createdAt) {
    this.customerId = customerId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.status = status;
    this.createdAt = createdAt;
  }

  /**
   * Maps a Customer entity to CustomerResponse DTO.
   *
   * @param customer the customer entity
   * @return CustomerResponse containing customer data
   */
  public static CustomerResponse from(Customer customer) {
    CustomerResponse response = new CustomerResponse();
    response.customerId = customer.getCustomerId();
    response.firstName = customer.getFirstName();
    response.lastName = customer.getLastName();
    response.dateOfBirth = customer.getDateOfBirth();
    response.email = customer.getEmail();
    response.phone = customer.getPhone();
    response.address = customer.getAddress();
    response.city = customer.getCity();
    response.state = customer.getState();
    response.zipCode = customer.getZipCode();
    response.status = customer.getStatus();
    response.createdAt = customer.getCreatedAt();
    response.updatedAt = customer.getUpdatedAt();
    response.createdBy = customer.getCreatedBy();
    response.updatedBy = customer.getUpdatedBy();
    return response;
  }

  // Getters and setters

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public String toString() {
    return "CustomerResponse{"
        + "customerId=" + customerId
        + ", firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + ", status=" + status
        + ", createdAt=" + createdAt
        + '}';
  }
}
