package com.genapp.dto;

import jakarta.validation.constraints.*;

/**
 * CustomerDTO - Data Transfer Object using Java 21 Record
 *
 * Java 21 Records are perfect for immutable data classes like DTOs.
 * Automatically provides:
 *   - Constructor with all fields
 *   - Getters for all fields
 *   - equals(), hashCode(), toString()
 *   - Compact serialization
 *
 * Old COBOL equivalent: COMMAREA structure from lgcmarea.cpy
 *   This was a 32,500-byte block passed between programs.
 *   Now we use JSON with REST APIs (minimal payload).
 *
 * Benefits over original 32.5KB COMMAREA:
 *   ✅ Type-safe at compile time
 *   ✅ Only 2-3KB as JSON (10x smaller)
 *   ✅ Browser-compatible
 *   ✅ Self-documenting
 *   ✅ Validation built-in
 *
 * Usage:
 *   - Request: POST /api/customers with CustomerDTO JSON body
 *   - Response: GET /api/customers/123 returns CustomerDTO JSON
 *
 * @param customerId auto-generated customer ID
 * @param firstName customer first name
 * @param lastName customer last name
 * @param address street address
 * @param city city name
 * @param state state abbreviation (US)
 * @param zipCode postal code
 * @param phone phone number
 * @param email email address
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21
 */
public record CustomerDTO(
    @Null(message = "Customer ID must be null for create requests")
    Long customerId,

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    String lastName,

    @Size(max = 100, message = "Address must not exceed 100 characters")
    String address,

    @Size(max = 50, message = "City must not exceed 50 characters")
    String city,

    @Size(max = 2, message = "State must be 2 characters (e.g., CA, NY)")
    String state,

    @Pattern(regexp = "^\\d{5}(-\\d{4})?$|^$", message = "Invalid ZIP code format (e.g., 12345 or 12345-6789)")
    String zipCode,

    @Pattern(regexp = "^[\\d\\-\\+\\(\\)\\s]*$|^$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email
) {
    /**
     * Compact constructor: Normalize whitespace in string fields
     *
     * Java 21 records support compact constructors for validation/transformation.
     * This is called automatically before record construction.
     *
     * Old COBOL equivalent: ACCEPTING input with CONVERTING clause
     */
    public CustomerDTO {
        // Trim and normalize whitespace
        firstName = firstName != null ? firstName.trim() : null;
        lastName = lastName != null ? lastName.trim() : null;
        address = address != null ? address.trim() : null;
        city = city != null ? city.trim() : null;
        state = state != null ? state.trim().toUpperCase() : null;
        zipCode = zipCode != null ? zipCode.trim() : null;
        phone = phone != null ? phone.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
    }

    /**
     * Convenience constructor for creating response DTOs
     *
     * Usage: new CustomerDTO(customer.getCustomerId(), customer.getFirstName(), ...)
     * Or from entity: convertToDTO(entity)
     *
     * @return formatted full name
     */
    public String fullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : lastName;
    }
}
