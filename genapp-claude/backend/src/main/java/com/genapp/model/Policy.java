package com.genapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Policy Entity - Base sealed class for all insurance policy types
 *
 * Replaces the COBOL policy structures from GenApp:
 *   - lgapol01.cbl: Add Policy (INSERT)
 *   - lgipol01.cbl: Inquire Policy (SELECT)
 *   - lgupol01.cbl: Update Policy (UPDATE)
 *   - lgdpol01.cbl: Delete/Cancel Policy (UPDATE status)
 *   - VSAM KSDSPOLY file with key: PolicyType(1) + CustomerId(10) + PolicyNumber(10)
 *
 * Java 21 sealed class allows controlled inheritance:
 *   - Only MotorPolicy, HousePolicy, EndowmentPolicy, CommercialPolicy can extend this
 *   - Enables exhaustive pattern matching at compile time
 *   - Type-safe and maintainable
 *
 * Old COBOL equivalent: Multiple policy record structures (POLICY-REC-MOTOR, POLICY-REC-HOUSE, etc.)
 * Now: Single inheritance hierarchy with type-specific subclasses
 *
 * Benefits over COBOL:
 *   ✅ Type-safe inheritance
 *   ✅ Compile-time verification of all policy types
 *   ✅ Database single-table inheritance
 *   ✅ Polymorphic queries
 *   ✅ Modern OOP patterns
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Entity
@Table(name = "policies", indexes = {
    @Index(name = "idx_policies_customer_id", columnList = "customer_id"),
    @Index(name = "idx_policies_policy_number", columnList = "policy_number"),
    @Index(name = "idx_policies_policy_type", columnList = "policy_type"),
    @Index(name = "idx_policies_status", columnList = "status")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "policy_type", discriminatorType = DiscriminatorType.STRING)
public abstract sealed class Policy permits MotorPolicy, HousePolicy, EndowmentPolicy, CommercialPolicy {

    /**
     * Primary key - auto-generated policy ID
     * Replaces: COBOL composite key (customer_id + policy_number)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_seq")
    @SequenceGenerator(name = "policy_seq", sequenceName = "seq_policy_id", allocationSize = 1)
    private Long policyId;

    /**
     * Foreign key to customer
     * Replaces: COBOL policy record customer_id field
     */
    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * Unique policy number within customer's policies
     * Replaces: COBOL policy_number field (10 chars)
     * Format examples: MOT-2025-001, HSE-2025-001, END-2025-001, COM-2025-001
     */
    @NotBlank(message = "Policy number is required")
    @Size(min = 5, max = 20, message = "Policy number must be between 5 and 20 characters")
    @Column(name = "policy_number", nullable = false, length = 20)
    private String policyNumber;

    /**
     * Policy start date
     * Replaces: COBOL start_date field (YYYYMMDD)
     */
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Policy end date
     * Replaces: COBOL end_date field (YYYYMMDD)
     */
    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Annual premium amount in dollars
     * Replaces: COBOL premium field (PIC 9(7)V99)
     */
    @NotNull(message = "Premium is required")
    @DecimalMin(value = "0.01", message = "Premium must be greater than 0")
    @Column(name = "premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal premium;

    /**
     * Policy status: ACTIVE, TERMINATED, EXPIRED, SUSPENDED
     * Replaces: COBOL policy_status field
     */
    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Timestamp when policy was created
     * Modern audit field (not in original COBOL)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when policy was last updated
     * Modern audit field (not in original COBOL)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    protected Policy() {
    }

    protected Policy(Long policyId, Long customerId, String policyNumber, LocalDate startDate,
                     LocalDate endDate, BigDecimal premium, String status,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.policyId = policyId;
        this.customerId = customerId;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.premium = premium;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    /**
     * Lifecycle callback: Set createdAt and updatedAt before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
    }

    /**
     * Lifecycle callback: Update updatedAt before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Validate that end date is after start date
     * Old COBOL equivalent: COBOL validation logic in lgXXpol01.cbl
     */
    @AssertTrue(message = "End date must be after start date")
    public boolean isDateRangeValid() {
        return endDate.isAfter(startDate);
    }

    /**
     * Get policy type discriminator (e.g., "MOTOR", "HOUSE", etc.)
     * Used in queries and responses
     */
    public abstract String getPolicyType();

    /**
     * Validate policy-specific rules (implemented in subclasses)
     * Each policy type has different validation requirements
     */
    public abstract void validatePolicySpecificFields();
}
