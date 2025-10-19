package com.genapp.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MotorPolicyDTO - Data Transfer Object for Motor/Vehicle Insurance Policies
 *
 * Extends the base PolicyDTO with motor-specific fields.
 *
 * Old COBOL equivalent: POLICY-REC-MOTOR structure with vehicle-specific fields
 *   - VEHICLE-MAKE, VEHICLE-MODEL, VEHICLE-YEAR, VEHICLE-VIN
 *   - USAGE-TYPE, ANNUAL-MILEAGE, COVERAGE-TYPE
 *
 * Java 21 Record with validation annotations for REST API contract enforcement.
 *
 * Usage:
 *   - Request: POST /api/policies with MotorPolicyDTO JSON
 *   - Response: GET /api/policies/123 returns MotorPolicyDTO JSON
 *
 * Example JSON:
 *   {
 *     "policyId": null,
 *     "customerId": 1,
 *     "policyNumber": "MOT-2025-001",
 *     "startDate": "2025-10-19",
 *     "endDate": "2026-10-19",
 *     "premium": 1200.00,
 *     "status": "ACTIVE",
 *     "policyType": "MOTOR",
 *     "vehicleMake": "Toyota",
 *     "vehicleModel": "Camry",
 *     "vehicleYear": 2022,
 *     "vehicleVin": "WVWZZZ3CZ5E123456",
 *     "usageType": "PERSONAL",
 *     "annualMileage": 15000,
 *     "coverageType": "COMPREHENSIVE"
 *   }
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21
 */
public record MotorPolicyDTO(
    Long policyId,
    Long customerId,
    String policyNumber,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal premium,
    String status,
    String policyType,

    // Motor-specific fields
    @NotBlank(message = "Vehicle make is required")
    @Size(min = 1, max = 50, message = "Vehicle make must be between 1 and 50 characters")
    String vehicleMake,

    @NotBlank(message = "Vehicle model is required")
    @Size(min = 1, max = 50, message = "Vehicle model must be between 1 and 50 characters")
    String vehicleModel,

    @NotNull(message = "Vehicle year is required")
    @Min(value = 1900, message = "Vehicle year must be 1900 or later")
    @Max(value = 2100, message = "Vehicle year must be 2100 or earlier")
    Integer vehicleYear,

    @NotBlank(message = "Vehicle VIN is required")
    @Size(min = 17, max = 17, message = "Vehicle VIN must be exactly 17 characters")
    String vehicleVin,

    @NotBlank(message = "Usage type is required")
    @Size(min = 1, max = 30, message = "Usage type must be between 1 and 30 characters")
    String usageType,

    @NotNull(message = "Annual mileage is required")
    @Min(value = 0, message = "Annual mileage must be non-negative")
    @Max(value = 100_000, message = "Annual mileage must not exceed 100,000")
    Integer annualMileage,

    @NotBlank(message = "Coverage type is required")
    @Size(min = 1, max = 30, message = "Coverage type must be between 1 and 30 characters")
    String coverageType
) {
    /**
     * Compact constructor: Normalize motor policy data
     */
    public MotorPolicyDTO {
        // Normalize strings
        vehicleMake = vehicleMake != null ? vehicleMake.trim() : null;
        vehicleModel = vehicleModel != null ? vehicleModel.trim() : null;
        vehicleVin = vehicleVin != null ? vehicleVin.trim().toUpperCase() : null;
        usageType = usageType != null ? usageType.trim().toUpperCase() : null;
        coverageType = coverageType != null ? coverageType.trim().toUpperCase() : null;
    }

    /**
     * Validate motor-specific business rules
     */
    public void validateMotorSpecificFields() {
        // VIN must be exactly 17 characters
        if (vehicleVin == null || vehicleVin.length() != 17) {
            throw new IllegalArgumentException("VIN must be exactly 17 characters");
        }

        // Annual mileage must be reasonable
        if (annualMileage < 0 || annualMileage > 100_000) {
            throw new IllegalArgumentException("Annual mileage must be between 0 and 100,000");
        }

        // Vehicle year must be reasonable
        if (vehicleYear < 1900 || vehicleYear > 2100) {
            throw new IllegalArgumentException("Invalid vehicle year: " + vehicleYear);
        }

        // Validate usage type
        String[] validUsageTypes = {"PERSONAL", "COMMERCIAL", "RIDESHARE", "TAXI"};
        validateAgainstList(usageType, validUsageTypes, "usage type");

        // Validate coverage type
        String[] validCoverageTypes = {"COMPREHENSIVE", "THIRD_PARTY", "COLLISION", "FIRE_THEFT"};
        validateAgainstList(coverageType, validCoverageTypes, "coverage type");
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
