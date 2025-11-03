package com.example.cicsgenapp.service;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Operation;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.exception.CustomerAlreadyExistsException;
import com.example.cicsgenapp.exception.ResourceNotFoundException;
import com.example.cicsgenapp.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for customer operations.
 *
 * <p>Encapsulates business logic for customer management including validation,
 * persistence, and audit trail generation.
 */
@Service
public class CustomerService {

  private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

  private final CustomerRepository customerRepository;
  private final AuditService auditService;

  /**
   * Constructs CustomerService with required dependencies.
   *
   * @param customerRepository repository for customer data access
   * @param auditService service for audit trail creation
   */
  public CustomerService(CustomerRepository customerRepository, AuditService auditService) {
    this.customerRepository = customerRepository;
    this.auditService = auditService;
  }

  /**
   * Creates a new customer from the request data.
   *
   * <p>Validates that email is unique, sets default status to ACTIVE, and creates audit entry.
   *
   * @param request the customer creation request
   * @return CustomerResponse with created customer data
   * @throws CustomerAlreadyExistsException if email already exists in the system
   */
  @Transactional
  public CustomerResponse createCustomer(CreateCustomerRequest request) {
    logger.debug("Creating customer with email: {}", request.getEmail());

    // Check email uniqueness
    Optional<Customer> existingCustomer = customerRepository.findByEmail(request.getEmail());
    if (existingCustomer.isPresent()) {
      logger.warn("Attempted to create customer with duplicate email: {}", request.getEmail());
      throw new CustomerAlreadyExistsException(
          "Customer with this email already exists");
    }

    // Map request to entity
    Customer customer = new Customer();
    customer.setFirstName(request.getFirstName());
    customer.setLastName(request.getLastName());
    customer.setDateOfBirth(request.getDateOfBirth());
    customer.setEmail(request.getEmail());
    customer.setPhone(request.getPhone());
    customer.setAddress(request.getAddress());
    customer.setCity(request.getCity());
    customer.setState(request.getState());
    customer.setZipCode(request.getZipCode());

    // Set default status to ACTIVE
    customer.setStatus(Status.ACTIVE);

    // Set audit fields
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());

    // Persist customer
    Customer savedCustomer = customerRepository.save(customer);
    logger.info("Customer created successfully with ID: {}", savedCustomer.getCustomerId());

    // Create audit entry
    auditService.createAuditEntry(
        Operation.CREATE,
        "CUSTOMER",
        savedCustomer.getCustomerId(),
        savedCustomer,
        "Customer created via API: " + savedCustomer.getEmail()
    );

    // Return response DTO
    return CustomerResponse.from(savedCustomer);
  }

  /**
   * Retrieves a customer by ID with audit logging.
   *
   * <p>Fetches the customer from the database and creates an audit log entry for the READ operation.
   *
   * @param customerId the customer ID
   * @return CustomerResponse containing customer data
   * @throws ResourceNotFoundException if customer is not found
   */
  @Transactional(readOnly = true)
  public CustomerResponse getCustomer(UUID customerId) {
    logger.debug("Retrieving customer with ID: {}", customerId);

    // Retrieve customer from database
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Customer " + customerId + " not found"));

    // Create audit entry for READ operation
    auditService.createAuditEntry(
        Operation.READ,
        "CUSTOMER",
        customerId,
        customer,
        "Customer retrieved via API"
    );

    logger.info("Customer retrieved successfully: {}", customerId);
    return CustomerResponse.from(customer);
  }

  /**
   * Retrieves a customer by ID.
   *
   * @param customerId the customer ID
   * @return CustomerResponse if found, null otherwise
   */
  public CustomerResponse getCustomerById(UUID customerId) {
    logger.debug("Retrieving customer with ID: {}", customerId);
    Optional<Customer> customer = customerRepository.findById(customerId);
    return customer.map(CustomerResponse::from).orElse(null);
  }

  /**
   * Retrieves a customer by email address.
   *
   * @param email the email address
   * @return CustomerResponse if found, null otherwise
   */
  public CustomerResponse getCustomerByEmail(String email) {
    logger.debug("Retrieving customer with email: {}", email);
    Optional<Customer> customer = customerRepository.findByEmail(email);
    return customer.map(CustomerResponse::from).orElse(null);
  }

  /**
   * Checks if a customer exists by email.
   *
   * @param email the email address to check
   * @return true if customer exists, false otherwise
   */
  public boolean customerExistsByEmail(String email) {
    return customerRepository.existsByEmail(email);
  }
}
