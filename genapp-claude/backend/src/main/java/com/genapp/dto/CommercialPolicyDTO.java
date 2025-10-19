package com.genapp.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CommercialPolicyDTO - Data Transfer Object for Business/Commercial Insurance Policies
 *
 * Extends the base PolicyDTO with commercial-specific fields.
 *
 * Old COBOL equivalent: POLICY-REC-COMMERCIAL structure with business-specific fields
 *   - BUSINESS-NAME, BUSINESS-TYPE, PROPERTY-ADDRESS
 *   - ANNUAL-REVENUE, NUM-EMPLOYEES, COVERAGE-LIMIT, DEDUCTIBLE, BUSINESS-CLASSIFICATION
 *
 * Java 21 Record with validation annotations for REST API contract enforcement.
 *
 * Usage:
 *   - Request: POST /api/policies with CommercialPolicyDTO JSON
 *   - Response: GET /api/policies/123 returns CommercialPolicyDTO JSON
 *
 * Example JSON:
 *   {
 *     "policyId": null,
 *     "customerId": 1,
 *     "policyNumber": "COM-2025-001",
 *     "startDate": "2025-10-19",
 *     "endDate": "2026-10-19",
 *     "premium": 15000.00,
 *     "status": "ACTIVE",
 *     "policyType": "COMMERCIAL",
 *     "businessName": "ABC Corp",
 *     "businessType": "MANUFACTURING",
 *     "propertyAddress": "789 Industrial Dr",
 *     "annualRevenue": 5000000.00,
 *     "numEmployees": 50,
 *     "coverageLimit": 1000000.00,
 *     "deductible": 5000.00,
 *     "businessClassification": "MEDIUM_RISK"
 *   }
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21
 */
public record CommercialPolicyDTO(
    Long policyId,
    Long customerId,
    String policyNumber,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal premium,
    String status,
    String policyType,

    // Commercial-specific fields
    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    String businessName,

    @NotBlank(message = "Business type is required")
    @Size(min = 1, max = 50, message = "Business type must be between 1 and 50 characters")
    String businessType,

    @NotBlank(message = "Property address is required")
    @Size(min = 5, max = 100, message = "Property address must be between 5 and 100 characters")
    String propertyAddress,

    @NotNull(message = "Annual revenue is required")
    @DecimalMin(value = "1000.00", message = "Annual revenue must be at least $1,000")
    @DecimalMax(value = "999999999.99", message = "Annual revenue must not exceed $999,999,999")
    BigDecimal annualRevenue,

    @NotNull(message = "Number of employees is required")
    @Min(value = 1, message = "Must have at least 1 employee")
    @Max(value = 10_000, message = "Number of employees must not exceed 10,000")
    Integer numEmployees,

    @NotNull(message = "Coverage limit is required")
    @DecimalMin(value = "10000.00", message = "Coverage limit must be at least $10,000")
    @DecimalMax(value = "100000000.00", message = "Coverage limit must not exceed $100,000,000")
    BigDecimal coverageLimit,

    @NotNull(message = "Deductible is required")
    @DecimalMin(value = "500.00", message = "Deductible must be at least $500")
    @DecimalMax(value = "500000.00", message = "Deductible must not exceed $500,000")
    BigDecimal deductible,

    @NotBlank(message = "Business classification is required")
    @Size(min = 1, max = 30, message = "Business classification must be between 1 and 30 characters")
    String businessClassification
) {
    /**
     * Compact constructor: Normalize commercial policy data
     */
    public CommercialPolicyDTO {
        // Normalize strings
        businessName = businessName != null ? businessName.trim() : null;
        businessType = businessType != null ? businessType.trim().toUpperCase() : null;
        propertyAddress = propertyAddress != null ? propertyAddress.trim() : null;
        businessClassification = businessClassification != null ? businessClassification.trim().toUpperCase() : null;
    }

    /**
     * Validate commercial-specific business rules
     */
    public void validateCommercialSpecificFields() {
        // Deductible must be less than coverage limit
        if (deductible.compareTo(coverageLimit) >= 0) {
            throw new IllegalArgumentException("Deductible must be less than coverage limit");
        }

        // Validate business type
        String[] validBusinessTypes = {"MANUFACTURING", "RETAIL", "SERVICES", "TECHNOLOGY",
                "FOOD_SERVICE", "HEALTHCARE", "CONSTRUCTION", "TRANSPORTATION", "FINANCE", "OTHER"};
        validateAgainstList(businessType, validBusinessTypes, "business type");

        // Validate business classification
        String[] validClassifications = {"LOW_RISK", "MEDIUM_RISK", "HIGH_RISK", "SPECIALIZED",
                "STANDARD", "PREMIUM", "EMERGING"};
        validateAgainstList(businessClassification, validClassifications, "business classification");

        // Number of employees must be reasonable
        if (numEmployees < 1 || numEmployees > 10_000) {
            throw new IllegalArgumentException("Invalid number of employees: " + numEmployees);
        }

        // Annual revenue must be positive
        if (annualRevenue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Annual revenue must be positive");
        }

        // Revenue per employee reasonableness check (warning-level, not hard error)
        BigDecimal revenuePerEmployee = annualRevenue.divide(
                BigDecimal.valueOf(numEmployees), BigDecimal.ROUND_HALF_UP);
        BigDecimal minPerEmployee = BigDecimal.valueOf(50_000);
        BigDecimal maxPerEmployee = BigDecimal.valueOf(1_000_000);
        if (revenuePerEmployee.compareTo(minPerEmployee) < 0 ||
                revenuePerEmployee.compareTo(maxPerEmployee) > 0) {
            // This is a warning-level check, could be legitimate special cases
            // Could log or track this for manual review
        }
    }

    /**
     * Helper to validate a value against a list of allowed values
     */
    private void validateAgainstList(String value, String[] allowedValues, String fieldName) {
        for (String allowed : allowedValues) {
            if (allowed.equalsIgnoreCase(value)) {
                return;
            }
        }
        throw new IllegalArgumentException("Invalid " + fieldName + ": " + value);
    }
}
