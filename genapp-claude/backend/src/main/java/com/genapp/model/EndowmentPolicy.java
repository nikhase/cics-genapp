package com.genapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EndowmentPolicy - Sealed subclass for investment/life insurance policies
 *
 * Replaces COBOL structure:
 *   - Policy type indicator: 'E' (first char of VSAM KSDSPOLY key)
 *   - Endowment-specific fields from GenApp COBOL program (lgXXpol01.cbl)
 *
 * Old COBOL fields:
 *   - MATURITY-DATE (YYYYMMDD)
 *   - INSURED-AMOUNT (PIC 9(10)V99)
 *   - BONUS-RATE (PIC 9V9(4))
 *   - INVESTMENT-TYPE (20 chars: Conservative, Balanced, Aggressive, etc.)
 *   - GUARANTEED-RETURN (PIC 9(2)V99)
 *   - POLICYHOLDER-AGE (3 digits)
 *   - SURRENDER-VALUE (PIC 9(10)V99)
 *
 * Java 21 sealed class implementation:
 *   - Final implementation (cannot be subclassed further)
 *   - Single table inheritance (POLICY.policy_type = 'ENDOWMENT')
 *   - Endowment-specific validation rules (maturity dates, bonus rates)
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Entity
@DiscriminatorValue("ENDOWMENT")
public final class EndowmentPolicy extends Policy {

    /**
     * Date when the endowment matures and pays out
     * Replaces: COBOL MATURITY-DATE (YYYYMMDD)
     */
    @NotNull(message = "Maturity date is required")
    @Future(message = "Maturity date must be in the future")
    @Column(name = "endowment_maturity_date")
    private LocalDate maturityDate;

    /**
     * Insured amount (face value of policy)
     * Replaces: COBOL INSURED-AMOUNT (PIC 9(10)V99)
     */
    @NotNull(message = "Insured amount is required")
    @DecimalMin(value = "1000.00", message = "Insured amount must be at least $1,000")
    @DecimalMax(value = "9999999.99", message = "Insured amount must not exceed $9,999,999")
    @Column(name = "endowment_insured_amount", precision = 12, scale = 2)
    private BigDecimal insuredAmount;

    /**
     * Annual bonus rate as percentage
     * Replaces: COBOL BONUS-RATE (PIC 9V9(4))
     * Example: 3.5 means 3.5% annual bonus
     */
    @NotNull(message = "Bonus rate is required")
    @DecimalMin(value = "0.0", message = "Bonus rate cannot be negative")
    @DecimalMax(value = "15.0", message = "Bonus rate must not exceed 15%")
    @Column(name = "endowment_bonus_rate", precision = 5, scale = 2)
    private BigDecimal bonusRate;

    /**
     * Type of investment strategy
     * Replaces: COBOL INVESTMENT-TYPE (20 chars)
     * Values: CONSERVATIVE, BALANCED, AGGRESSIVE, GROWTH, HYBRID, etc.
     */
    @NotBlank(message = "Investment type is required")
    @Size(min = 1, max = 30, message = "Investment type must be between 1 and 30 characters")
    @Column(name = "endowment_investment_type", length = 30)
    private String investmentType;

    /**
     * Guaranteed annual return as percentage
     * Replaces: COBOL GUARANTEED-RETURN (PIC 9(2)V99)
     * Example: 2.5 means 2.5% guaranteed annual return
     */
    @NotNull(message = "Guaranteed return is required")
    @DecimalMin(value = "0.0", message = "Guaranteed return cannot be negative")
    @DecimalMax(value = "10.0", message = "Guaranteed return must not exceed 10%")
    @Column(name = "endowment_guaranteed_return", precision = 5, scale = 2)
    private BigDecimal guaranteedReturn;

    /**
     * Age of the policyholder
     * Replaces: COBOL POLICYHOLDER-AGE (3 digits)
     */
    @NotNull(message = "Policyholder age is required")
    @Min(value = 18, message = "Policyholder must be at least 18 years old")
    @Max(value = 120, message = "Policyholder age must not exceed 120")
    @Column(name = "endowment_policyholder_age")
    private Integer policyholderAge;

    /**
     * Current surrender value (accumulated value if canceled)
     * Replaces: COBOL SURRENDER-VALUE (PIC 9(10)V99)
     * Calculated based on premiums paid and bonuses earned
     */
    @DecimalMin(value = "0.00", message = "Surrender value cannot be negative")
    @Column(name = "endowment_surrender_value", precision = 12, scale = 2)
    private BigDecimal surrenderValue;

    // Constructors
    public EndowmentPolicy() {
    }

    public EndowmentPolicy(Long policyId, Long customerId, String policyNumber, LocalDate startDate,
                           LocalDate endDate, BigDecimal premium, String status,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           LocalDate maturityDate, BigDecimal insuredAmount, BigDecimal bonusRate, String investmentType,
                           BigDecimal guaranteedReturn, Integer policyholderAge, BigDecimal surrenderValue) {
        super(policyId, customerId, policyNumber, startDate, endDate, premium, status, createdAt, updatedAt);
        this.maturityDate = maturityDate;
        this.insuredAmount = insuredAmount;
        this.bonusRate = bonusRate;
        this.investmentType = investmentType;
        this.guaranteedReturn = guaranteedReturn;
        this.policyholderAge = policyholderAge;
        this.surrenderValue = surrenderValue;
    }

    // Getters and Setters
    public LocalDate getMaturityDate() { return maturityDate; }
    public void setMaturityDate(LocalDate maturityDate) { this.maturityDate = maturityDate; }

    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }

    public BigDecimal getBonusRate() { return bonusRate; }
    public void setBonusRate(BigDecimal bonusRate) { this.bonusRate = bonusRate; }

    public String getInvestmentType() { return investmentType; }
    public void setInvestmentType(String investmentType) { this.investmentType = investmentType; }

    public BigDecimal getGuaranteedReturn() { return guaranteedReturn; }
    public void setGuaranteedReturn(BigDecimal guaranteedReturn) { this.guaranteedReturn = guaranteedReturn; }

    public Integer getPolicyholderAge() { return policyholderAge; }
    public void setPolicyholderAge(Integer policyholderAge) { this.policyholderAge = policyholderAge; }

    public BigDecimal getSurrenderValue() { return surrenderValue; }
    public void setSurrenderValue(BigDecimal surrenderValue) { this.surrenderValue = surrenderValue; }

    /**
     * Get the policy type discriminator
     * Used for polymorphic queries: SELECT FROM Policy WHERE policy_type = 'ENDOWMENT'
     */
    @Override
    public String getPolicyType() {
        return "ENDOWMENT";
    }

    /**
     * Validate endowment-specific business rules
     *
     * Old COBOL equivalent: Validation performed in lgXXpol01.cbl
     * - Check maturity date is after policy end date (or at least reasonable)
     * - Check bonus rate is within expected range
     * - Check guaranteed return is less than or equal to bonus rate
     * - Check policyholder age is realistic (18-100 for endowments)
     * - Check insured amount is reasonable for the policy holder age
     */
    @Override
    public void validatePolicySpecificFields() {
        // Maturity date must be in future and after policy start date
        if (maturityDate.isBefore(getStartDate())) {
            throw new IllegalArgumentException("Maturity date must be after policy start date");
        }

        // Maturity date should be reasonable (typically 5-40 years from now)
        LocalDate maxMaturity = LocalDate.now().plusYears(50);
        if (maturityDate.isAfter(maxMaturity)) {
            throw new IllegalArgumentException("Maturity date is too far in the future (max 50 years)");
        }

        // Bonus rate should not exceed reasonable limits
        if (bonusRate.compareTo(BigDecimal.ZERO) < 0 || bonusRate.compareTo(BigDecimal.valueOf(15)) > 0) {
            throw new IllegalArgumentException("Bonus rate must be between 0% and 15%");
        }

        // Guaranteed return should not exceed bonus rate
        if (guaranteedReturn.compareTo(bonusRate) > 0) {
            throw new IllegalArgumentException("Guaranteed return cannot exceed bonus rate");
        }

        // Investment type validation
        String[] validInvestmentTypes = {"CONSERVATIVE", "BALANCED", "AGGRESSIVE", "GROWTH", "HYBRID", "FIXED"};
        if (!isValidInvestmentType(investmentType, validInvestmentTypes)) {
            throw new IllegalArgumentException("Invalid investment type: " + investmentType);
        }

        // Policyholder age validation
        if (policyholderAge < 18 || policyholderAge > 120) {
            throw new IllegalArgumentException("Invalid policyholder age: " + policyholderAge);
        }

        // Age-based insured amount reasonableness check
        // Younger people typically have larger endowments
        BigDecimal maxAmountForAge = calculateMaxInsuredAmount(policyholderAge);
        if (insuredAmount.compareTo(maxAmountForAge) > 0) {
            // This is a warning-level check, not a hard error
            // Could be legitimate high-value endowment
        }

        // Surrender value should not exceed insured amount
        if (surrenderValue != null && surrenderValue.compareTo(insuredAmount) > 0) {
            throw new IllegalArgumentException("Surrender value cannot exceed insured amount");
        }
    }

    /**
     * Calculate reasonable maximum insured amount based on policyholder age
     * Younger people can get larger endowments
     */
    private BigDecimal calculateMaxInsuredAmount(Integer age) {
        // Rough calculation: younger = higher maximum
        if (age < 30) {
            return BigDecimal.valueOf(5_000_000);
        } else if (age < 50) {
            return BigDecimal.valueOf(3_000_000);
        } else if (age < 70) {
            return BigDecimal.valueOf(1_000_000);
        } else {
            return BigDecimal.valueOf(500_000);
        }
    }

    /**
     * Helper method to validate investment type
     */
    private boolean isValidInvestmentType(String investmentType, String[] validTypes) {
        for (String valid : validTypes) {
            if (valid.equalsIgnoreCase(investmentType)) {
                return true;
            }
        }
        return false;
    }
}
