package com.genapp.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EndowmentPolicyDTO - Data Transfer Object for Investment/Life Insurance Policies
 *
 * Extends the base PolicyDTO with endowment-specific fields.
 *
 * Old COBOL equivalent: POLICY-REC-ENDOWMENT structure with endowment-specific fields
 *   - MATURITY-DATE, INSURED-AMOUNT, BONUS-RATE
 *   - INVESTMENT-TYPE, GUARANTEED-RETURN, POLICYHOLDER-AGE, SURRENDER-VALUE
 *
 * Java 21 Record with validation annotations for REST API contract enforcement.
 *
 * Usage:
 *   - Request: POST /api/policies with EndowmentPolicyDTO JSON
 *   - Response: GET /api/policies/123 returns EndowmentPolicyDTO JSON
 *
 * Example JSON:
 *   {
 *     "policyId": null,
 *     "customerId": 1,
 *     "policyNumber": "END-2025-001",
 *     "startDate": "2025-10-19",
 *     "endDate": "2030-10-19",
 *     "premium": 5000.00,
 *     "status": "ACTIVE",
 *     "policyType": "ENDOWMENT",
 *     "maturityDate": "2050-10-19",
 *     "insuredAmount": 100000.00,
 *     "bonusRate": 3.50,
 *     "investmentType": "BALANCED",
 *     "guaranteedReturn": 2.50,
 *     "policyholderAge": 35,
 *     "surrenderValue": 15000.00
 *   }
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21
 */
public record EndowmentPolicyDTO(
    Long policyId,
    Long customerId,
    String policyNumber,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal premium,
    String status,
    String policyType,

    // Endowment-specific fields
    @NotNull(message = "Maturity date is required")
    @Future(message = "Maturity date must be in the future")
    LocalDate maturityDate,

    @NotNull(message = "Insured amount is required")
    @DecimalMin(value = "1000.00", message = "Insured amount must be at least $1,000")
    @DecimalMax(value = "9999999.99", message = "Insured amount must not exceed $9,999,999")
    BigDecimal insuredAmount,

    @NotNull(message = "Bonus rate is required")
    @DecimalMin(value = "0.0", message = "Bonus rate cannot be negative")
    @DecimalMax(value = "15.0", message = "Bonus rate must not exceed 15%")
    BigDecimal bonusRate,

    @NotBlank(message = "Investment type is required")
    @Size(min = 1, max = 30, message = "Investment type must be between 1 and 30 characters")
    String investmentType,

    @NotNull(message = "Guaranteed return is required")
    @DecimalMin(value = "0.0", message = "Guaranteed return cannot be negative")
    @DecimalMax(value = "10.0", message = "Guaranteed return must not exceed 10%")
    BigDecimal guaranteedReturn,

    @NotNull(message = "Policyholder age is required")
    @Min(value = 18, message = "Policyholder must be at least 18 years old")
    @Max(value = 120, message = "Policyholder age must not exceed 120")
    Integer policyholderAge,

    @DecimalMin(value = "0.00", message = "Surrender value cannot be negative")
    BigDecimal surrenderValue
) {
    /**
     * Compact constructor: Normalize endowment policy data
     */
    public EndowmentPolicyDTO {
        // Normalize strings
        investmentType = investmentType != null ? investmentType.trim().toUpperCase() : null;
    }

    /**
     * Validate endowment-specific business rules
     */
    public void validateEndowmentSpecificFields() {
        // Maturity date must be after start date
        if (maturityDate != null && startDate != null && maturityDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Maturity date must be after policy start date");
        }

        // Maturity date should be reasonable (typically 5-40 years from now)
        LocalDate maxMaturity = LocalDate.now().plusYears(50);
        if (maturityDate != null && maturityDate.isAfter(maxMaturity)) {
            throw new IllegalArgumentException("Maturity date is too far in the future (max 50 years)");
        }

        // Bonus rate must be reasonable
        if (bonusRate.compareTo(BigDecimal.ZERO) < 0 || bonusRate.compareTo(BigDecimal.valueOf(15)) > 0) {
            throw new IllegalArgumentException("Bonus rate must be between 0% and 15%");
        }

        // Guaranteed return must not exceed bonus rate
        if (guaranteedReturn.compareTo(bonusRate) > 0) {
            throw new IllegalArgumentException("Guaranteed return cannot exceed bonus rate");
        }

        // Validate investment type
        String[] validInvestmentTypes = {"CONSERVATIVE", "BALANCED", "AGGRESSIVE", "GROWTH", "HYBRID", "FIXED"};
        validateAgainstList(investmentType, validInvestmentTypes, "investment type");

        // Policyholder age must be reasonable
        if (policyholderAge < 18 || policyholderAge > 120) {
            throw new IllegalArgumentException("Invalid policyholder age: " + policyholderAge);
        }

        // Surrender value should not exceed insured amount
        if (surrenderValue != null && surrenderValue.compareTo(insuredAmount) > 0) {
            throw new IllegalArgumentException("Surrender value cannot exceed insured amount");
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
