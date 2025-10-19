package com.genapp.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PolicyDTO - Base Data Transfer Object for Policy using Java 21 Record
 *
 * Java 21 Records provide:
 *   - Immutability by default
 *   - Auto-generated constructor, getters, equals(), hashCode(), toString()
 *   - Perfect for DTOs in REST APIs
 *   - Type-safe validation
 *
 * This is the base DTO for all policy types. Specific policy types (Motor, House, etc.)
 * extend this with additional type-specific fields.
 *
 * Old COBOL equivalent: POLICY-REC structure from lgcmarea.cpy
 *   - Now using JSON REST API (2-3KB) instead of COMMAREA (32.5KB)
 *   - Type-safe validation at compile time
 *   - Browser-compatible format
 *
 * Usage:
 *   - Request: POST /api/policies with PolicyDTO JSON body
 *   - Response: GET /api/policies/123 returns PolicyDTO JSON
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21
 */
public record PolicyDTO(
    @Null(message = "Policy ID must be null for create requests")
    Long policyId,

    @NotNull(message = "Customer ID is required")
    Long customerId,

    @NotBlank(message = "Policy number is required")
    @Size(min = 5, max = 20, message = "Policy number must be between 5 and 20 characters")
    String policyNumber,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    @NotNull(message = "Premium is required")
    @DecimalMin(value = "0.01", message = "Premium must be greater than 0")
    BigDecimal premium,

    @NotBlank(message = "Status is required")
    String status,

    @NotBlank(message = "Policy type is required")
    @Pattern(regexp = "MOTOR|HOUSE|ENDOWMENT|COMMERCIAL", message = "Policy type must be MOTOR, HOUSE, ENDOWMENT, or COMMERCIAL")
    String policyType
) {
    /**
     * Compact constructor: Normalize and validate policy data
     *
     * Java 21 records support compact constructors for validation/transformation.
     * This is called automatically before record construction.
     *
     * Old COBOL equivalent: ACCEPTING input with CONVERTING and validation
     */
    public PolicyDTO {
        // Trim whitespace
        policyNumber = policyNumber != null ? policyNumber.trim() : null;
        status = status != null ? status.trim().toUpperCase() : null;
        policyType = policyType != null ? policyType.trim().toUpperCase() : null;

        // Validate date range
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    /**
     * Helper to check if dates are valid range
     */
    public boolean isDateRangeValid() {
        return startDate != null && endDate != null && endDate.isAfter(startDate);
    }

    /**
     * Helper to get full policy identifier
     */
    public String getFullPolicyId() {
        return customerId + "-" + policyNumber;
    }
}
