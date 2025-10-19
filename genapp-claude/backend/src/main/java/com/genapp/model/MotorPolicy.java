package com.genapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MotorPolicy - Sealed subclass for vehicle/motor insurance policies
 *
 * Replaces COBOL structure:
 *   - Policy type indicator: 'M' (first char of VSAM KSDSPOLY key)
 *   - Motor-specific fields from GenApp COBOL program (lgXXpol01.cbl)
 *
 * Old COBOL fields:
 *   - VEHICLE-MAKE (20 chars)
 *   - VEHICLE-MODEL (20 chars)
 *   - VEHICLE-YEAR (4 digits)
 *   - VEHICLE-VIN (20 chars)
 *   - USAGE-TYPE (20 chars: Commercial, Personal, etc.)
 *   - ANNUAL-MILEAGE (6 digits)
 *   - COVERAGE-TYPE (20 chars: Comprehensive, Third Party, etc.)
 *
 * Java 21 sealed class implementation:
 *   - Final implementation (cannot be subclassed further)
 *   - Single table inheritance (POLICY.policy_type = 'MOTOR')
 *   - Motor-specific validation rules
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Entity
@DiscriminatorValue("MOTOR")
public final class MotorPolicy extends Policy {

    /**
     * Vehicle manufacturer
     * Replaces: COBOL VEHICLE-MAKE (20 chars)
     */
    @NotBlank(message = "Vehicle make is required")
    @Size(min = 1, max = 50, message = "Vehicle make must be between 1 and 50 characters")
    @Column(name = "motor_vehicle_make", length = 50)
    private String vehicleMake;

    /**
     * Vehicle model name
     * Replaces: COBOL VEHICLE-MODEL (20 chars)
     */
    @NotBlank(message = "Vehicle model is required")
    @Size(min = 1, max = 50, message = "Vehicle model must be between 1 and 50 characters")
    @Column(name = "motor_vehicle_model", length = 50)
    private String vehicleModel;

    /**
     * Vehicle year of manufacture
     * Replaces: COBOL VEHICLE-YEAR (4 digits)
     */
    @NotNull(message = "Vehicle year is required")
    @Min(value = 1900, message = "Vehicle year must be 1900 or later")
    @Max(value = 2100, message = "Vehicle year must be 2100 or earlier")
    @Column(name = "motor_vehicle_year")
    private Integer vehicleYear;

    /**
     * Vehicle Identification Number (unique identifier for vehicle)
     * Replaces: COBOL VEHICLE-VIN (20 chars)
     * Standard VIN is 17 characters
     */
    @NotBlank(message = "Vehicle VIN is required")
    @Size(min = 17, max = 17, message = "Vehicle VIN must be exactly 17 characters")
    @Column(name = "motor_vehicle_vin", length = 17)
    private String vehicleVin;

    /**
     * How the vehicle is used
     * Replaces: COBOL USAGE-TYPE (20 chars)
     * Values: PERSONAL, COMMERCIAL, RIDESHARE, etc.
     */
    @NotBlank(message = "Usage type is required")
    @Size(min = 1, max = 30, message = "Usage type must be between 1 and 30 characters")
    @Column(name = "motor_usage_type", length = 30)
    private String usageType;

    /**
     * Expected annual mileage
     * Replaces: COBOL ANNUAL-MILEAGE (6 digits)
     */
    @NotNull(message = "Annual mileage is required")
    @Min(value = 0, message = "Annual mileage must be non-negative")
    @Column(name = "motor_annual_mileage")
    private Integer annualMileage;

    /**
     * Type of coverage
     * Replaces: COBOL COVERAGE-TYPE (20 chars)
     * Values: COMPREHENSIVE, THIRD_PARTY, COLLISION, etc.
     */
    @NotBlank(message = "Coverage type is required")
    @Size(min = 1, max = 30, message = "Coverage type must be between 1 and 30 characters")
    @Column(name = "motor_coverage_type", length = 30)
    private String coverageType;

    // Constructors
    public MotorPolicy() {
    }

    public MotorPolicy(Long policyId, Long customerId, String policyNumber, LocalDate startDate,
                       LocalDate endDate, BigDecimal premium, String status,
                       LocalDateTime createdAt, LocalDateTime updatedAt,
                       String vehicleMake, String vehicleModel, Integer vehicleYear, String vehicleVin,
                       String usageType, Integer annualMileage, String coverageType) {
        super(policyId, customerId, policyNumber, startDate, endDate, premium, status, createdAt, updatedAt);
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.vehicleVin = vehicleVin;
        this.usageType = usageType;
        this.annualMileage = annualMileage;
        this.coverageType = coverageType;
    }

    // Getters and Setters
    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Integer getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(Integer vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public Integer getAnnualMileage() {
        return annualMileage;
    }

    public void setAnnualMileage(Integer annualMileage) {
        this.annualMileage = annualMileage;
    }

    public String getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    /**
     * Get the policy type discriminator
     * Used for polymorphic queries: SELECT FROM Policy WHERE policy_type = 'MOTOR'
     */
    @Override
    public String getPolicyType() {
        return "MOTOR";
    }

    /**
     * Validate motor-specific business rules
     *
     * Old COBOL equivalent: Validation performed in lgXXpol01.cbl
     * - Check vehicle year is reasonable
     * - Check annual mileage is within expected range
     * - Check coverage type matches usage type
     */
    @Override
    public void validatePolicySpecificFields() {
        // Vehicle year validation
        if (vehicleYear < 1900 || vehicleYear > 2100) {
            throw new IllegalArgumentException("Invalid vehicle year: " + vehicleYear);
        }

        // Annual mileage validation (reasonable values: 0 to 100,000)
        if (annualMileage < 0 || annualMileage > 100_000) {
            throw new IllegalArgumentException("Annual mileage must be between 0 and 100,000");
        }

        // VIN validation (basic format check)
        if (vehicleVin == null || vehicleVin.length() != 17) {
            throw new IllegalArgumentException("VIN must be exactly 17 characters");
        }

        // Usage type validation
        String[] validUsageTypes = {"PERSONAL", "COMMERCIAL", "RIDESHARE", "TAXI"};
        if (!isValidUsageType(usageType, validUsageTypes)) {
            throw new IllegalArgumentException("Invalid usage type: " + usageType);
        }

        // Coverage type validation
        String[] validCoverageTypes = {"COMPREHENSIVE", "THIRD_PARTY", "COLLISION", "FIRE_THEFT"};
        if (!isValidCoverageType(coverageType, validCoverageTypes)) {
            throw new IllegalArgumentException("Invalid coverage type: " + coverageType);
        }
    }

    /**
     * Helper method to validate usage type against allowed values
     */
    private boolean isValidUsageType(String usageType, String[] validTypes) {
        for (String valid : validTypes) {
            if (valid.equalsIgnoreCase(usageType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to validate coverage type against allowed values
     */
    private boolean isValidCoverageType(String coverageType, String[] validTypes) {
        for (String valid : validTypes) {
            if (valid.equalsIgnoreCase(coverageType)) {
                return true;
            }
        }
        return false;
    }
}
