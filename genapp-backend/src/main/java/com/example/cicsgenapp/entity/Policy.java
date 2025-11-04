package com.example.cicsgenapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Policy entity representing an insurance policy record in the system.
 * Maps to PostgreSQL policy table with audit columns managed automatically.
 * Supports soft-delete pattern via deletion_at field.
 *
 * <p>Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
@Entity
@Table(name = "policy")
@EntityListeners(AuditingEntityListener.class)
public class Policy {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "policy_id")
  private UUID policyId;

  @NotNull(message = "Policy number is required")
  @Column(name = "policy_number", unique = true, nullable = false, length = 50)
  private String policyNumber;

  @NotNull(message = "Customer is required")
  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @NotNull(message = "Policy type is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "policy_type", nullable = false, length = 20)
  private PolicyType policyType;

  @NotNull(message = "Effective date is required")
  @Column(name = "effective_date", nullable = false)
  private LocalDate effectiveDate;

  @NotNull(message = "Expiration date is required")
  @Column(name = "expiration_date", nullable = false)
  private LocalDate expirationDate;

  @NotNull(message = "Premium amount is required")
  @Positive(message = "Premium amount must be positive")
  @Column(name = "premium_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal premiumAmount;

  @NotNull(message = "Status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private PolicyStatus status = PolicyStatus.ACTIVE;

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

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "deletion_reason", length = 500)
  private String deletionReason;

  /**
   * Default constructor for JPA.
   */
  public Policy() {
  }

  /**
   * Constructor for creating a new policy with required fields.
   */
  public Policy(String policyNumber, Customer customer, PolicyType policyType,
      LocalDate effectiveDate, LocalDate expirationDate, BigDecimal premiumAmount) {
    this.policyNumber = policyNumber;
    this.customer = customer;
    this.policyType = policyType;
    this.effectiveDate = effectiveDate;
    this.expirationDate = expirationDate;
    this.premiumAmount = premiumAmount;
    this.status = PolicyStatus.ACTIVE;
  }

  // Getters and Setters

  public UUID getPolicyId() {
    return policyId;
  }

  public void setPolicyId(UUID policyId) {
    this.policyId = policyId;
  }

  public String getPolicyNumber() {
    return policyNumber;
  }

  public void setPolicyNumber(String policyNumber) {
    this.policyNumber = policyNumber;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public PolicyType getPolicyType() {
    return policyType;
  }

  public void setPolicyType(PolicyType policyType) {
    this.policyType = policyType;
  }

  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }

  public void setEffectiveDate(LocalDate effectiveDate) {
    this.effectiveDate = effectiveDate;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public BigDecimal getPremiumAmount() {
    return premiumAmount;
  }

  public void setPremiumAmount(BigDecimal premiumAmount) {
    this.premiumAmount = premiumAmount;
  }

  public PolicyStatus getStatus() {
    return status;
  }

  public void setStatus(PolicyStatus status) {
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

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  public String getDeletionReason() {
    return deletionReason;
  }

  public void setDeletionReason(String deletionReason) {
    this.deletionReason = deletionReason;
  }
}
