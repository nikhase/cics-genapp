package com.genapp.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * HousePolicyDTO - Data Transfer Object for Home/Property Insurance Policies
 *
 * Extends the base PolicyDTO with house-specific fields.
 *
 * Old COBOL equivalent: POLICY-REC-HOUSE structure with property-specific fields
 *   - PROPERTY-ADDRESS, PROPERTY-TYPE, CONSTRUCTION-YEAR, SQUARE-FOOTAGE
 *   - REPLACEMENT-COST, DEDUCTIBLE, NUM-BEDROOMS, NUM-BATHROOMS
 *   - HAS-POOL, HAS-ALARM-SYSTEM
 *
 * Java 21 Record with validation annotations for REST API contract enforcement.
 *
 * Usage:
 *   - Request: POST /api/policies with HousePolicyDTO JSON
 *   - Response: GET /api/policies/123 returns HousePolicyDTO JSON
 *
 * Example JSON:
 *   {
 *     "policyId": null,
 *     "customerId": 1,
 *     "policyNumber": "HSE-2025-001",
 *     "startDate": "2025-10-19",
 *     "endDate": "2026-10-19",
 *     "premium": 800.00,
 *     "status": "ACTIVE",
 *     "policyType": "HOUSE",
 *     "propertyAddress": "456 Oak Ave",
 *     "propertyType": "SINGLE_FAMILY",
 *     "constructionYear": 1995,
 *     "squareFootage": 2000,
 *     "replacementCost": 400000.00,
 *     "deductible": 1000.00,
 *     "numBedrooms": 3,
 *     "numBathrooms": 2,
 *     "hasPool": false,
 *     "hasAlarmSystem": true
 *   }
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21
 */
public record HousePolicyDTO(
    Long policyId,
    Long customerId,
    String policyNumber,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal premium,
    String status,
    String policyType,

    // House-specific fields
    @NotBlank(message = "Property address is required")
    @Size(min = 5, max = 100, message = "Property address must be between 5 and 100 characters")
    String propertyAddress,

    @NotBlank(message = "Property type is required")
    @Size(min = 1, max = 30, message = "Property type must be between 1 and 30 characters")
    String propertyType,

    @NotNull(message = "Construction year is required")
    @Min(value = 1800, message = "Construction year must be 1800 or later")
    @Max(value = 2100, message = "Construction year must be 2100 or earlier")
    Integer constructionYear,

    @NotNull(message = "Square footage is required")
    @DecimalMin(value = "100.00", message = "Square footage must be at least 100")
    @DecimalMax(value = "1000000.00", message = "Square footage must not exceed 1,000,000")
    BigDecimal squareFootage,

    @NotNull(message = "Replacement cost is required")
    @DecimalMin(value = "10000.00", message = "Replacement cost must be at least $10,000")
    @DecimalMax(value = "99999999.99", message = "Replacement cost must not exceed $99,999,999")
    BigDecimal replacementCost,

    @NotNull(message = "Deductible is required")
    @DecimalMin(value = "100.00", message = "Deductible must be at least $100")
    @DecimalMax(value = "100000.00", message = "Deductible must not exceed $100,000")
    BigDecimal deductible,

    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Number of bedrooms must be non-negative")
    @Max(value = 20, message = "Number of bedrooms must not exceed 20")
    Integer numBedrooms,

    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 0, message = "Number of bathrooms must be non-negative")
    @Max(value = 20, message = "Number of bathrooms must not exceed 20")
    Integer numBathrooms,

    Boolean hasPool,
    Boolean hasAlarmSystem
) {
    /**
     * Compact constructor: Normalize house policy data
     */
    public HousePolicyDTO {
        // Normalize strings
        propertyAddress = propertyAddress != null ? propertyAddress.trim() : null;
        propertyType = propertyType != null ? propertyType.trim().toUpperCase() : null;
    }

    /**
     * Validate house-specific business rules
     */
    public void validateHouseSpecificFields() {
        // Deductible must be less than replacement cost
        if (deductible.compareTo(replacementCost) >= 0) {
            throw new IllegalArgumentException("Deductible must be less than replacement cost");
        }

        // Construction year must be reasonable
        if (constructionYear < 1800 || constructionYear > 2100) {
            throw new IllegalArgumentException("Invalid construction year: " + constructionYear);
        }

        // Square footage must be reasonable
        if (squareFootage.compareTo(BigDecimal.valueOf(100)) < 0 ||
            squareFootage.compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
            throw new IllegalArgumentException("Square footage must be between 100 and 1,000,000");
        }

        // Validate property type
        String[] validPropertyTypes = {"SINGLE_FAMILY", "CONDO", "TOWNHOUSE", "APARTMENT", "DUPLEX", "FARM"};
        validateAgainstList(propertyType, validPropertyTypes, "property type");

        // Bedrooms and bathrooms must be reasonable
        if (numBedrooms < 0 || numBedrooms > 20) {
            throw new IllegalArgumentException("Invalid number of bedrooms: " + numBedrooms);
        }

        if (numBathrooms < 0 || numBathrooms > 20) {
            throw new IllegalArgumentException("Invalid number of bathrooms: " + numBathrooms);
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
