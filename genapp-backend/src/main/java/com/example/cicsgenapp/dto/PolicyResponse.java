package com.example.cicsgenapp.dto;

import com.example.cicsgenapp.entity.Policy;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for policy responses.
 * Used in policy search/list API responses and Vaadin Grid display.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
@Schema(title = "Policy Response", description = "Policy data returned by API")
public class PolicyResponse {

  @JsonProperty("policyId")
  @Schema(description = "Unique policy identifier (UUID)")
  private UUID policyId;

  @JsonProperty("policyNumber")
  @Schema(description = "Unique policy number")
  private String policyNumber;

  @JsonProperty("customerId")
  @Schema(description = "Customer ID associated with the policy")
  private UUID customerId;

  @JsonProperty("customerName")
  @Schema(description = "Customer full name")
  private String customerName;

  @JsonProperty("policyType")
  @Schema(description = "Type of policy (MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)")
  private PolicyType policyType;

  @JsonProperty("effectiveDate")
  @Schema(description = "Policy effective date")
  private LocalDate effectiveDate;

  @JsonProperty("expirationDate")
  @Schema(description = "Policy expiration date")
  private LocalDate expirationDate;

  @JsonProperty("premiumAmount")
  @Schema(description = "Policy premium amount")
  private BigDecimal premiumAmount;

  @JsonProperty("status")
  @Schema(description = "Policy status (ACTIVE, LAPSED, RENEWED)")
  private PolicyStatus status;

  @JsonProperty("createdAt")
  @Schema(description = "Creation timestamp")
  private LocalDateTime createdAt;

  @JsonProperty("updatedAt")
  @Schema(description = "Last update timestamp")
  private LocalDateTime updatedAt;

  /**
   * Default constructor for JSON deserialization.
   */
  public PolicyResponse() {
  }

  /**
   * Constructor for creating a response with all fields.
   */
  public PolicyResponse(UUID policyId, String policyNumber, UUID customerId,
      String customerName, PolicyType policyType, LocalDate effectiveDate,
      LocalDate expirationDate, BigDecimal premiumAmount, PolicyStatus status,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.policyId = policyId;
    this.policyNumber = policyNumber;
    this.customerId = customerId;
    this.customerName = customerName;
    this.policyType = policyType;
    this.effectiveDate = effectiveDate;
    this.expirationDate = expirationDate;
    this.premiumAmount = premiumAmount;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Factory method to create PolicyResponse from Policy entity.
   *
   * @param policy the Policy entity
   * @return PolicyResponse DTO with data from entity
   */
  public static PolicyResponse from(Policy policy) {
    PolicyResponse response = new PolicyResponse();
    response.policyId = policy.getPolicyId();
    response.policyNumber = policy.getPolicyNumber();
    response.customerId = policy.getCustomer().getCustomerId();
    response.customerName =
        policy.getCustomer().getFirstName() + " " + policy.getCustomer().getLastName();
    response.policyType = policy.getPolicyType();
    response.effectiveDate = policy.getEffectiveDate();
    response.expirationDate = policy.getExpirationDate();
    response.premiumAmount = policy.getPremiumAmount();
    response.status = policy.getStatus();
    response.createdAt = policy.getCreatedAt();
    response.updatedAt = policy.getUpdatedAt();
    return response;
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

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
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
}
