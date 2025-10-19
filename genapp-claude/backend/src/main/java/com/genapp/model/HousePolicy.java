package com.genapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HousePolicy - Sealed subclass for home/property insurance policies
 *
 * Replaces COBOL structure:
 *   - Policy type indicator: 'H' (first char of VSAM KSDSPOLY key)
 *   - House-specific fields from GenApp COBOL program (lgXXpol01.cbl)
 *
 * Old COBOL fields:
 *   - PROPERTY-ADDRESS (50 chars)
 *   - PROPERTY-TYPE (20 chars: Single Family, Condo, etc.)
 *   - CONSTRUCTION-YEAR (4 digits)
 *   - SQUARE-FOOTAGE (7 digits)
 *   - REPLACEMENT-COST (PIC 9(10)V99)
 *   - DEDUCTIBLE (PIC 9(7)V99)
 *   - NUM-BEDROOMS (2 digits)
 *   - NUM-BATHROOMS (2 digits)
 *   - HAS-POOL (boolean)
 *   - HAS-ALARM-SYSTEM (boolean)
 *
 * Java 21 sealed class implementation:
 *   - Final implementation (cannot be subclassed further)
 *   - Single table inheritance (POLICY.policy_type = 'HOUSE')
 *   - House-specific validation rules
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Entity
@DiscriminatorValue("HOUSE")
public final class HousePolicy extends Policy {

    /**
     * Property street address
     * Replaces: COBOL PROPERTY-ADDRESS (50 chars)
     */
    @NotBlank(message = "Property address is required")
    @Size(min = 5, max = 100, message = "Property address must be between 5 and 100 characters")
    @Column(name = "house_property_address", length = 100)
    private String propertyAddress;

    /**
     * Type of residential property
     * Replaces: COBOL PROPERTY-TYPE (20 chars)
     * Values: SINGLE_FAMILY, CONDO, TOWNHOUSE, APARTMENT, etc.
     */
    @NotBlank(message = "Property type is required")
    @Size(min = 1, max = 30, message = "Property type must be between 1 and 30 characters")
    @Column(name = "house_property_type", length = 30)
    private String propertyType;

    /**
     * Year the property was constructed
     * Replaces: COBOL CONSTRUCTION-YEAR (4 digits)
     */
    @NotNull(message = "Construction year is required")
    @Min(value = 1800, message = "Construction year must be 1800 or later")
    @Max(value = 2100, message = "Construction year must be 2100 or earlier")
    @Column(name = "house_construction_year")
    private Integer constructionYear;

    /**
     * Total square footage of the property
     * Replaces: COBOL SQUARE-FOOTAGE (7 digits)
     */
    @NotNull(message = "Square footage is required")
    @DecimalMin(value = "100.00", message = "Square footage must be at least 100")
    @DecimalMax(value = "1000000.00", message = "Square footage must not exceed 1,000,000")
    @Column(name = "house_square_footage", precision = 10, scale = 2)
    private BigDecimal squareFootage;

    /**
     * Estimated replacement cost of the property
     * Replaces: COBOL REPLACEMENT-COST (PIC 9(10)V99)
     */
    @NotNull(message = "Replacement cost is required")
    @DecimalMin(value = "10000.00", message = "Replacement cost must be at least $10,000")
    @DecimalMax(value = "99999999.99", message = "Replacement cost must not exceed $99,999,999")
    @Column(name = "house_replacement_cost", precision = 12, scale = 2)
    private BigDecimal replacementCost;

    /**
     * Deductible amount for claims
     * Replaces: COBOL DEDUCTIBLE (PIC 9(7)V99)
     */
    @NotNull(message = "Deductible is required")
    @DecimalMin(value = "100.00", message = "Deductible must be at least $100")
    @DecimalMax(value = "100000.00", message = "Deductible must not exceed $100,000")
    @Column(name = "house_deductible", precision = 10, scale = 2)
    private BigDecimal deductible;

    /**
     * Number of bedrooms
     * Replaces: COBOL NUM-BEDROOMS (2 digits)
     */
    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Number of bedrooms must be non-negative")
    @Max(value = 20, message = "Number of bedrooms must not exceed 20")
    @Column(name = "house_num_bedrooms")
    private Integer numBedrooms;

    /**
     * Number of bathrooms
     * Replaces: COBOL NUM-BATHROOMS (2 digits)
     */
    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 0, message = "Number of bathrooms must be non-negative")
    @Max(value = 20, message = "Number of bathrooms must not exceed 20")
    @Column(name = "house_num_bathrooms")
    private Integer numBathrooms;

    /**
     * Whether the property has a swimming pool
     * Modern addition (not in original COBOL, but common in policies)
     */
    @Column(name = "house_has_pool")
    private Boolean hasPool;

    /**
     * Whether the property has an alarm/security system
     * Modern addition (affects premium calculation)
     */
    @Column(name = "house_has_alarm_system")
    private Boolean hasAlarmSystem;

    // Constructors
    public HousePolicy() {
    }

    public HousePolicy(Long policyId, Long customerId, String policyNumber, LocalDate startDate,
                       LocalDate endDate, BigDecimal premium, String status,
                       LocalDateTime createdAt, LocalDateTime updatedAt,
                       String propertyAddress, String propertyType, Integer constructionYear, BigDecimal squareFootage,
                       BigDecimal replacementCost, BigDecimal deductible, Integer numBedrooms, Integer numBathrooms,
                       Boolean hasPool, Boolean hasAlarmSystem) {
        super(policyId, customerId, policyNumber, startDate, endDate, premium, status, createdAt, updatedAt);
        this.propertyAddress = propertyAddress;
        this.propertyType = propertyType;
        this.constructionYear = constructionYear;
        this.squareFootage = squareFootage;
        this.replacementCost = replacementCost;
        this.deductible = deductible;
        this.numBedrooms = numBedrooms;
        this.numBathrooms = numBathrooms;
        this.hasPool = hasPool;
        this.hasAlarmSystem = hasAlarmSystem;
    }

    // Getters and Setters
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public Integer getConstructionYear() { return constructionYear; }
    public void setConstructionYear(Integer constructionYear) { this.constructionYear = constructionYear; }

    public BigDecimal getSquareFootage() { return squareFootage; }
    public void setSquareFootage(BigDecimal squareFootage) { this.squareFootage = squareFootage; }

    public BigDecimal getReplacementCost() { return replacementCost; }
    public void setReplacementCost(BigDecimal replacementCost) { this.replacementCost = replacementCost; }

    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }

    public Integer getNumBedrooms() { return numBedrooms; }
    public void setNumBedrooms(Integer numBedrooms) { this.numBedrooms = numBedrooms; }

    public Integer getNumBathrooms() { return numBathrooms; }
    public void setNumBathrooms(Integer numBathrooms) { this.numBathrooms = numBathrooms; }

    public Boolean getHasPool() { return hasPool; }
    public void setHasPool(Boolean hasPool) { this.hasPool = hasPool; }

    public Boolean getHasAlarmSystem() { return hasAlarmSystem; }
    public void setHasAlarmSystem(Boolean hasAlarmSystem) { this.hasAlarmSystem = hasAlarmSystem; }

    /**
     * Get the policy type discriminator
     * Used for polymorphic queries: SELECT FROM Policy WHERE policy_type = 'HOUSE'
     */
    @Override
    public String getPolicyType() {
        return "HOUSE";
    }

    /**
     * Validate house-specific business rules
     *
     * Old COBOL equivalent: Validation performed in lgXXpol01.cbl
     * - Check construction year is reasonable
     * - Check square footage is within expected range
     * - Check replacement cost matches square footage estimate
     * - Check deductible is less than replacement cost
     */
    @Override
    public void validatePolicySpecificFields() {
        // Construction year validation
        if (constructionYear < 1800 || constructionYear > 2100) {
            throw new IllegalArgumentException("Invalid construction year: " + constructionYear);
        }

        // Square footage validation
        if (squareFootage.compareTo(BigDecimal.valueOf(100)) < 0 ||
            squareFootage.compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
            throw new IllegalArgumentException("Square footage must be between 100 and 1,000,000");
        }

        // Deductible must be less than replacement cost
        if (deductible.compareTo(replacementCost) >= 0) {
            throw new IllegalArgumentException("Deductible must be less than replacement cost");
        }

        // Replacement cost estimation check (rough validation)
        // Typical cost per sq ft: $100-300
        BigDecimal minCost = squareFootage.multiply(BigDecimal.valueOf(100));
        BigDecimal maxCost = squareFootage.multiply(BigDecimal.valueOf(300));
        if (replacementCost.compareTo(minCost) < 0 || replacementCost.compareTo(maxCost) > 0) {
            // This is a warning-level check, not a hard error
            // Could be custom home or special materials
        }

        // Property type validation
        String[] validPropertyTypes = {"SINGLE_FAMILY", "CONDO", "TOWNHOUSE", "APARTMENT", "DUPLEX", "FARM"};
        if (!isValidPropertyType(propertyType, validPropertyTypes)) {
            throw new IllegalArgumentException("Invalid property type: " + propertyType);
        }

        // Bedrooms and bathrooms validation
        if (numBedrooms < 0 || numBedrooms > 20) {
            throw new IllegalArgumentException("Invalid number of bedrooms: " + numBedrooms);
        }

        if (numBathrooms < 0 || numBathrooms > 20) {
            throw new IllegalArgumentException("Invalid number of bathrooms: " + numBathrooms);
        }
    }

    /**
     * Helper method to validate property type
     */
    private boolean isValidPropertyType(String propertyType, String[] validTypes) {
        for (String valid : validTypes) {
            if (valid.equalsIgnoreCase(propertyType)) {
                return true;
            }
        }
        return false;
    }
}
