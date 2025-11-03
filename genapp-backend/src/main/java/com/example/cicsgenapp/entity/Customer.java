package com.example.cicsgenapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Customer entity representing a customer record in the system.
 * Maps to PostgreSQL customer table with audit columns managed automatically.
 * Supports soft-delete pattern via status field (ACTIVE/INACTIVE).
 */
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "customer_id")
  private UUID customerId;

  @NotNull(message = "First name is required")
  @Column(name = "first_name", length = 100, nullable = false)
  private String firstName;

  @NotNull(message = "Last name is required")
  @Column(name = "last_name", length = 100, nullable = false)
  private String lastName;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @NotNull(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(name = "email", length = 254, nullable = false, unique = true)
  private String email;

  @Pattern(
      regexp = "^\\+?[1-9]\\d{1,14}$|^$",
      message = "Phone number should be in valid international format (E.164)")
  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "city", length = 100)
  private String city;

  @Column(name = "state", length = 50)
  private String state;

  @Column(name = "zip_code", length = 10)
  private String zipCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private Status status = Status.ACTIVE;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "created_by", length = 255)
  private String createdBy;

  @Column(name = "updated_by", length = 255)
  private String updatedBy;

  @Version
  @Column(name = "version")
  private Long version;

  /**
   * Default constructor for JPA.
   */
  public Customer() {
  }

  /**
   * Constructor for creating a new customer with required fields.
   */
  public Customer(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.status = Status.ACTIVE;
  }

  // Getters and Setters

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

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  // equals() and hashCode() based on UUID

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(customerId, customer.customerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId);
  }

  // toString() for debugging

  @Override
  public String toString() {
    return "Customer{"
        + "customerId=" + customerId
        + ", firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + ", status=" + status
        + ", createdAt=" + createdAt
        + ", updatedAt=" + updatedAt
        + '}';
  }
}
