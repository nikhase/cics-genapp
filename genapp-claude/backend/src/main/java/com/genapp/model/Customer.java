package com.genapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer Entity - JPA representation of a customer
 *
 * Replaces the COBOL COMMAREA structure from lgcmarea.cpy and VSAM KSDSCUST file.
 * Maps to the 'customers' table in PostgreSQL.
 *
 * Old COBOL Structure (from lgcmarea.cpy):
 *   01 WS-CUST-REC.
 *      05 WS-CUST-ID        PIC 9(10) VALUE ZERO.
 *      05 WS-FIRST-NAME     PIC X(30).
 *      05 WS-LAST-NAME      PIC X(30).
 *      05 WS-ADDRESS        PIC X(50).
 *      05 WS-CITY           PIC X(20).
 *      05 WS-STATE          PIC XX.
 *      05 WS-ZIP-CODE       PIC X(10).
 *      05 WS-PHONE          PIC X(15).
 *      05 WS-EMAIL          PIC X(40).
 *
 * New Java Record approach (used in DTOs):
 *   public record CustomerDTO(Long id, String firstName, String lastName, ...) {}
 *
 * This entity uses traditional JPA with Lombok to keep domain model clean.
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customers_first_name", columnList = "first_name"),
    @Index(name = "idx_customers_last_name", columnList = "last_name"),
    @Index(name = "idx_customers_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    /**
     * Primary key - auto-generated customer ID
     * Replaces: COBOL WS-CUST-ID (10 digits)
     * Auto-incremented via PostgreSQL sequence: seq_customer_id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "seq_customer_id", allocationSize = 1)
    private Long customerId;

    /**
     * Customer's first name
     * Replaces: COBOL WS-FIRST-NAME (30 chars)
     */
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Customer's last name
     * Replaces: COBOL WS-LAST-NAME (30 chars)
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Street address
     * Replaces: COBOL WS-ADDRESS (50 chars)
     */
    @Size(max = 100, message = "Address must not exceed 100 characters")
    @Column(name = "address", length = 100)
    private String address;

    /**
     * City name
     * Replaces: COBOL WS-CITY (20 chars)
     */
    @Size(max = 50, message = "City must not exceed 50 characters")
    @Column(name = "city", length = 50)
    private String city;

    /**
     * State abbreviation (US)
     * Replaces: COBOL WS-STATE (2 chars)
     */
    @Size(max = 2, message = "State must be 2 characters (e.g., CA, NY)")
    @Column(name = "state", length = 2)
    private String state;

    /**
     * Postal code
     * Replaces: COBOL WS-ZIP-CODE (10 chars)
     */
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$|^$", message = "Invalid ZIP code format")
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    /**
     * Phone number
     * Replaces: COBOL WS-PHONE (15 chars)
     */
    @Pattern(regexp = "^[\\d\\-\\+\\(\\)\\s]*$|^$", message = "Invalid phone number format")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Email address (unique)
     * Replaces: COBOL WS-EMAIL (40 chars)
     * Modern addition not in original COBOL
     */
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", length = 100, unique = true)
    private String email;

    /**
     * Timestamp when customer record was created
     * Modern audit field (not in original COBOL)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when customer record was last updated
     * Modern audit field (not in original COBOL)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback: Set createdAt and updatedAt before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback: Update updatedAt before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Get full name (convenience method)
     * @return firstName + lastName
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
