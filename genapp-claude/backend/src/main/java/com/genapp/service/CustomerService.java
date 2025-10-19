package com.genapp.service;

import com.genapp.dto.CustomerDTO;
import com.genapp.model.Customer;
import com.genapp.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomerService - Business Logic Layer
 *
 * Replaces the COBOL business logic programs:
 *   - lgacus01.cbl: Add customer (with validation)
 *   - lgicus01.cbl: Inquire customer (search/retrieve)
 *   - lgucus01.cbl: Update customer (with validation)
 *
 * Old COBOL flow:
 *   Presentation (lgtestc1) → EXEC CICS LINK → Business (lgacus01)
 *     ↓ Validation, business rules
 *   Data Layer (lgacdb01 + lgacvs01) → Db2 + VSAM
 *     ↓ Two-phase commit
 *   Return to presentation
 *
 * New flow (Spring Boot):
 *   REST Controller → @Service → Repository → PostgreSQL
 *     ↓ @Transactional automatic
 *   Single database, no dual-write problems
 *
 * Key improvements:
 *   ✅ Single source of truth (PostgreSQL)
 *   ✅ Automatic transaction management (@Transactional)
 *   ✅ Exception handling with clear error messages
 *   ✅ Logging for debugging
 *   ✅ Dependency injection (no LINKAGE SECTION)
 *   ✅ No more COMMAREA passing (use DTOs)
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    /**
     * Constructor for dependency injection
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Create a new customer
     *
     * Old COBOL equivalent: lgacus01.cbl (Add Customer Business Logic)
     *   1. VALIDATE input fields (MOVE, IF statements)
     *   2. CHECK for duplicate email (READ VSAM/Db2)
     *   3. GENERATE customer ID (GET COUNTER or Db2 IDENTITY)
     *   4. WRITE to Db2 (INSERT)
     *   5. WRITE to VSAM (dual-write pattern)
     *   6. RETURN to caller (EXEC CICS RETURN)
     *
     * New Java equivalent:
     *   1. Validation annotations (@NotNull, @Email, etc.)
     *   2. Check for duplicate (existsByEmail)
     *   3. Generate ID (PostgreSQL SEQUENCE)
     *   4. Save to PostgreSQL (one write)
     *   5. Return DTO
     *
     * @param customerDTO customer data to create
     * @return created customer with generated ID
     * @throws IllegalArgumentException if email already exists
     */
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Creating new customer: {}", customerDTO.firstName());

        // Validation: Check for duplicate email
        if (customerDTO.email() != null && customerRepository.existsByEmail(customerDTO.email())) {
            log.warn("Customer creation failed: email already exists: {}", customerDTO.email());
            throw new IllegalArgumentException("Customer with email '" + customerDTO.email() + "' already exists");
        }

        // Map DTO to Entity
        Customer customer = new Customer();
        customer.setFirstName(customerDTO.firstName());
        customer.setLastName(customerDTO.lastName());
        customer.setAddress(customerDTO.address());
        customer.setCity(customerDTO.city());
        customer.setState(customerDTO.state());
        customer.setZipCode(customerDTO.zipCode());
        customer.setPhone(customerDTO.phone());
        customer.setEmail(customerDTO.email());

        // Save to database (PostgreSQL SEQUENCE auto-generates ID)
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getCustomerId());

        // Return DTO
        return convertToDTO(savedCustomer);
    }

    /**
     * Get customer by ID
     *
     * Old COBOL equivalent: lgicus01.cbl (Inquire Customer Business Logic)
     *   EXEC CICS READ FILE('KSDSCUST')
     *       INTO WS-CUST-REC
     *       KEYLENGTH(10)
     *       NOTFND CONTINUE
     *   END-EXEC.
     *
     * New Java equivalent: JPA findById (with automatic lazy loading)
     *
     * @param customerId customer ID to retrieve
     * @return CustomerDTO if found
     * @throws IllegalArgumentException if not found
     */
    public CustomerDTO getCustomer(Long customerId) {
        log.debug("Retrieving customer with ID: {}", customerId);

        return customerRepository.findById(customerId)
                .map(customer -> {
                    log.debug("Customer found: {}", customer.getFullName());
                    return convertToDTO(customer);
                })
                .orElseThrow(() -> {
                    log.warn("Customer not found with ID: {}", customerId);
                    return new IllegalArgumentException("Customer not found with ID: " + customerId);
                });
    }

    /**
     * Verify customer exists by ID (for validation purposes)
     *
     * Used by policy creation to ensure customer exists before creating a policy.
     * Throws exception if customer not found.
     *
     * @param customerId customer ID to verify
     * @throws IllegalArgumentException if customer not found
     */
    public void getCustomerById(Long customerId) {
        log.debug("Verifying customer exists with ID: {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            log.warn("Customer not found with ID: {}", customerId);
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
    }

    /**
     * Update existing customer
     *
     * Old COBOL equivalent: lgucus01.cbl (Update Customer Business Logic)
     *   1. FIND existing customer (READ)
     *   2. UPDATE fields (MOVE)
     *   3. VALIDATE updated fields (IF statements)
     *   4. CHECK for email duplicate (if email changed)
     *   5. WRITE to Db2 (UPDATE)
     *   6. WRITE to VSAM (dual-write pattern)
     *   7. RETURN to caller (EXEC CICS RETURN)
     *
     * New Java equivalent:
     *   1. Find existing (findById)
     *   2. Update fields (setters)
     *   3. Validate (annotations)
     *   4. Check duplicate (if email changed)
     *   5. Save to PostgreSQL (one update)
     *   6. Return DTO
     *
     * @param customerId customer ID to update
     * @param customerDTO updated customer data
     * @return updated CustomerDTO
     * @throws IllegalArgumentException if not found
     */
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Update failed: Customer not found with ID: {}", customerId);
                    return new IllegalArgumentException("Customer not found with ID: " + customerId);
                });

        // Validation: Check for duplicate email (if email changed)
        if (customerDTO.email() != null &&
            !customerDTO.email().equals(customer.getEmail()) &&
            customerRepository.existsByEmail(customerDTO.email())) {
            log.warn("Update failed: email already exists: {}", customerDTO.email());
            throw new IllegalArgumentException("Customer with email '" + customerDTO.email() + "' already exists");
        }

        // Update fields (only non-null values from DTO)
        if (customerDTO.firstName() != null) customer.setFirstName(customerDTO.firstName());
        if (customerDTO.lastName() != null) customer.setLastName(customerDTO.lastName());
        if (customerDTO.address() != null) customer.setAddress(customerDTO.address());
        if (customerDTO.city() != null) customer.setCity(customerDTO.city());
        if (customerDTO.state() != null) customer.setState(customerDTO.state());
        if (customerDTO.zipCode() != null) customer.setZipCode(customerDTO.zipCode());
        if (customerDTO.phone() != null) customer.setPhone(customerDTO.phone());
        if (customerDTO.email() != null) customer.setEmail(customerDTO.email());

        // Save updated customer
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: ID {}", customerId);

        return convertToDTO(updatedCustomer);
    }

    /**
     * Delete customer by ID
     *
     * Old COBOL equivalent: lgdcus01.cbl (if it existed)
     *   DELETE from Db2 and VSAM
     *
     * New Java equivalent: Simple repository delete
     *
     * @param customerId customer ID to delete
     * @throws IllegalArgumentException if not found
     */
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            log.warn("Delete failed: Customer not found with ID: {}", customerId);
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }

        customerRepository.deleteById(customerId);
        log.info("Customer deleted successfully: ID {}", customerId);
    }

    /**
     * Search customers by full name pattern
     *
     * Old COBOL equivalent: Manual loop through VSAM, comparing names
     *
     * @param pattern search pattern
     * @return list of matching customers
     */
    public List<CustomerDTO> searchByFullName(String pattern) {
        log.debug("Searching customers by full name pattern: {}", pattern);

        return customerRepository.searchByFullName(pattern)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all customers
     *
     * Old COBOL equivalent: Manual sequential read through entire VSAM file
     *
     * @return list of all customers
     */
    public List<CustomerDTO> getAllCustomers() {
        log.debug("Retrieving all customers");

        return customerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Customer entity to CustomerDTO
     *
     * Mapper function for transforming JPA entities to DTOs.
     * Useful for API responses.
     *
     * @param customer JPA Customer entity
     * @return CustomerDTO
     */
    private CustomerDTO convertToDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getZipCode(),
                customer.getPhone(),
                customer.getEmail()
        );
    }
}
