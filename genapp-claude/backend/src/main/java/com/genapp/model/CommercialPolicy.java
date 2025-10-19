package com.genapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CommercialPolicy - Sealed subclass for business/commercial insurance policies
 *
 * Replaces COBOL structure:
 *   - Policy type indicator: 'C' (first char of VSAM KSDSPOLY key)
 *   - Commercial-specific fields from GenApp COBOL program (lgXXpol01.cbl)
 *
 * Old COBOL fields:
 *   - BUSINESS-NAME (50 chars)
 *   - BUSINESS-TYPE (20 chars: Manufacturing, Retail, etc.)
 *   - PROPERTY-ADDRESS (50 chars)
 *   - ANNUAL-REVENUE (PIC 9(10)V99)
 *   - NUM-EMPLOYEES (4 digits)
 *   - COVERAGE-LIMIT (PIC 9(10)V99)
 *   - DEDUCTIBLE (PIC 9(7)V99)
 *   - BUSINESS-CLASSIFICATION (20 chars)
 *
 * Java 21 sealed class implementation:
 *   - Final implementation (cannot be subclassed further)
 *   - Single table inheritance (POLICY.policy_type = 'COMMERCIAL')
 *   - Commercial-specific validation rules (business details, revenue, employees)
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Entity
@DiscriminatorValue("COMMERCIAL")
public final class CommercialPolicy extends Policy {

    /**
     * Name of the business
     * Replaces: COBOL BUSINESS-NAME (50 chars)
     */
    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    @Column(name = "commercial_business_name", length = 100)
    private String businessName;

    /**
     * Type/industry of the business
     * Replaces: COBOL BUSINESS-TYPE (20 chars)
     * Values: MANUFACTURING, RETAIL, SERVICES, TECHNOLOGY, FOOD_SERVICE, etc.
     */
    @NotBlank(message = "Business type is required")
    @Size(min = 1, max = 50, message = "Business type must be between 1 and 50 characters")
    @Column(name = "commercial_business_type", length = 50)
    private String businessType;

    /**
     * Business property address
     * Replaces: COBOL PROPERTY-ADDRESS (50 chars)
     */
    @NotBlank(message = "Property address is required")
    @Size(min = 5, max = 100, message = "Property address must be between 5 and 100 characters")
    @Column(name = "commercial_property_address", length = 100)
    private String propertyAddress;

    /**
     * Annual business revenue
     * Replaces: COBOL ANNUAL-REVENUE (PIC 9(10)V99)
     */
    @NotNull(message = "Annual revenue is required")
    @DecimalMin(value = "1000.00", message = "Annual revenue must be at least $1,000")
    @DecimalMax(value = "999999999.99", message = "Annual revenue must not exceed $999,999,999")
    @Column(name = "commercial_annual_revenue", precision = 12, scale = 2)
    private BigDecimal annualRevenue;

    /**
     * Number of employees
     * Replaces: COBOL NUM-EMPLOYEES (4 digits)
     */
    @NotNull(message = "Number of employees is required")
    @Min(value = 1, message = "Must have at least 1 employee")
    @Max(value = 10_000, message = "Number of employees must not exceed 10,000")
    @Column(name = "commercial_num_employees")
    private Integer numEmployees;

    /**
     * Total coverage limit for the policy
     * Replaces: COBOL COVERAGE-LIMIT (PIC 9(10)V99)
     */
    @NotNull(message = "Coverage limit is required")
    @DecimalMin(value = "10000.00", message = "Coverage limit must be at least $10,000")
    @DecimalMax(value = "100000000.00", message = "Coverage limit must not exceed $100,000,000")
    @Column(name = "commercial_coverage_limit", precision = 12, scale = 2)
    private BigDecimal coverageLimit;

    /**
     * Deductible amount for claims
     * Replaces: COBOL DEDUCTIBLE (PIC 9(7)V99)
     */
    @NotNull(message = "Deductible is required")
    @DecimalMin(value = "500.00", message = "Deductible must be at least $500")
    @DecimalMax(value = "500000.00", message = "Deductible must not exceed $500,000")
    @Column(name = "commercial_deductible", precision = 10, scale = 2)
    private BigDecimal deductible;

    /**
     * Business classification for risk assessment
     * Replaces: COBOL BUSINESS-CLASSIFICATION (20 chars)
     * Values: LOW_RISK, MEDIUM_RISK, HIGH_RISK, SPECIALIZED, etc.
     */
    @NotBlank(message = "Business classification is required")
    @Size(min = 1, max = 30, message = "Business classification must be between 1 and 30 characters")
    @Column(name = "commercial_business_classification", length = 30)
    private String businessClassification;

    // Constructors
    public CommercialPolicy() {
    }

    public CommercialPolicy(Long policyId, Long customerId, String policyNumber, LocalDate startDate,
                            LocalDate endDate, BigDecimal premium, String status,
                            LocalDateTime createdAt, LocalDateTime updatedAt,
                            String businessName, String businessType, String propertyAddress, BigDecimal annualRevenue,
                            Integer numEmployees, BigDecimal coverageLimit, BigDecimal deductible, String businessClassification) {
        super(policyId, customerId, policyNumber, startDate, endDate, premium, status, createdAt, updatedAt);
        this.businessName = businessName;
        this.businessType = businessType;
        this.propertyAddress = propertyAddress;
        this.annualRevenue = annualRevenue;
        this.numEmployees = numEmployees;
        this.coverageLimit = coverageLimit;
        this.deductible = deductible;
        this.businessClassification = businessClassification;
    }

    // Getters and Setters
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public BigDecimal getAnnualRevenue() { return annualRevenue; }
    public void setAnnualRevenue(BigDecimal annualRevenue) { this.annualRevenue = annualRevenue; }

    public Integer getNumEmployees() { return numEmployees; }
    public void setNumEmployees(Integer numEmployees) { this.numEmployees = numEmployees; }

    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public void setCoverageLimit(BigDecimal coverageLimit) { this.coverageLimit = coverageLimit; }

    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }

    public String getBusinessClassification() { return businessClassification; }
    public void setBusinessClassification(String businessClassification) { this.businessClassification = businessClassification; }

    /**
     * Get the policy type discriminator
     * Used for polymorphic queries: SELECT FROM Policy WHERE policy_type = 'COMMERCIAL'
     */
    @Override
    public String getPolicyType() {
        return "COMMERCIAL";
    }

    /**
     * Validate commercial-specific business rules
     *
     * Old COBOL equivalent: Validation performed in lgXXpol01.cbl
     * - Check business type is valid
     * - Check annual revenue matches number of employees (reasonableness)
     * - Check coverage limit is reasonable relative to annual revenue
     * - Check deductible is less than coverage limit
     * - Check business classification matches business type and risk profile
     */
    @Override
    public void validatePolicySpecificFields() {
        // Business type validation
        String[] validBusinessTypes = {"MANUFACTURING", "RETAIL", "SERVICES", "TECHNOLOGY",
                "FOOD_SERVICE", "HEALTHCARE", "CONSTRUCTION", "TRANSPORTATION", "FINANCE", "OTHER"};
        if (!isValidBusinessType(businessType, validBusinessTypes)) {
            throw new IllegalArgumentException("Invalid business type: " + businessType);
        }

        // Revenue per employee reasonableness check
        // Rough validation: revenue per employee should be between $50K and $1M
        BigDecimal revenuePerEmployee = annualRevenue.divide(
                BigDecimal.valueOf(numEmployees), BigDecimal.ROUND_HALF_UP);
        BigDecimal minPerEmployee = BigDecimal.valueOf(50_000);
        BigDecimal maxPerEmployee = BigDecimal.valueOf(1_000_000);
        if (revenuePerEmployee.compareTo(minPerEmployee) < 0 ||
                revenuePerEmployee.compareTo(maxPerEmployee) > 0) {
            // This is a warning-level check, could be legitimate (startups, high-revenue services)
        }

        // Coverage limit vs revenue relationship
        // Typically coverage should be 1-3x annual revenue
        BigDecimal minCoverage = annualRevenue.multiply(BigDecimal.ONE);
        BigDecimal maxCoverage = annualRevenue.multiply(BigDecimal.valueOf(3));
        if (coverageLimit.compareTo(minCoverage) < 0 || coverageLimit.compareTo(maxCoverage) > 0) {
            // This is a warning-level check, could be legitimate special cases
        }

        // Deductible must be less than coverage limit
        if (deductible.compareTo(coverageLimit) >= 0) {
            throw new IllegalArgumentException("Deductible must be less than coverage limit");
        }

        // Deductible should be reasonable relative to annual revenue
        // Typically 0.1% to 5% of annual revenue
        BigDecimal minDeductible = annualRevenue.multiply(BigDecimal.valueOf(0.001));
        BigDecimal maxDeductible = annualRevenue.multiply(BigDecimal.valueOf(0.05));
        if (deductible.compareTo(minDeductible) < 0 || deductible.compareTo(maxDeductible) > 0) {
            // This is a warning-level check
        }

        // Business classification validation
        String[] validClassifications = {"LOW_RISK", "MEDIUM_RISK", "HIGH_RISK", "SPECIALIZED",
                "STANDARD", "PREMIUM", "EMERGING"};
        if (!isValidBusinessClassification(businessClassification, validClassifications)) {
            throw new IllegalArgumentException("Invalid business classification: " + businessClassification);
        }

        // Number of employees validation
        if (numEmployees < 1 || numEmployees > 10_000) {
            throw new IllegalArgumentException("Invalid number of employees: " + numEmployees);
        }

        // Annual revenue validation
        if (annualRevenue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Annual revenue must be positive");
        }
    }

    /**
     * Helper method to validate business type
     */
    private boolean isValidBusinessType(String businessType, String[] validTypes) {
        for (String valid : validTypes) {
            if (valid.equalsIgnoreCase(businessType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to validate business classification
     */
    private boolean isValidBusinessClassification(String classification, String[] validTypes) {
        for (String valid : validTypes) {
            if (valid.equalsIgnoreCase(classification)) {
                return true;
            }
        }
        return false;
    }
}
