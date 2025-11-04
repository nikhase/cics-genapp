package com.example.cicsgenapp.service;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.SearchCriteria;
import com.example.cicsgenapp.dto.UpdateCustomerRequest;
import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Operation;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.exception.CustomerAlreadyExistsException;
import com.example.cicsgenapp.exception.OptimisticLockException;
import com.example.cicsgenapp.exception.ResourceNotFoundException;
import com.example.cicsgenapp.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  /**
   * Searches for customers based on provided search criteria with pagination and sorting.
   *
   * <p>Supports multi-field search (firstName, lastName, email, phone), optional status filtering,
   * sorting, and pagination.
   *
   * @param criteria search criteria (query, status, pagination, sorting)
   * @return PagedResponse with matched customers and pagination metadata
   */
  @Transactional(readOnly = true)
  public PagedResponse<CustomerResponse> searchCustomers(SearchCriteria criteria) {
    logger.debug("Searching customers with criteria: {}", criteria);

    // Validate and normalize search criteria
    int limit = criteria.getLimit();
    int offset = criteria.getOffset();

    // Enforce limits
    if (limit > 100) {
      limit = 100;
    }
    if (limit <= 0) {
      limit = 50;
    }
    if (offset < 0) {
      offset = 0;
    }

    // Build Sort object from sortBy and sortOrder
    Sort.Direction direction = Sort.Direction.fromString(criteria.getSortOrder().toUpperCase());
    Sort sort = Sort.by(direction, criteria.getSortBy());

    // Create Pageable with offset and limit
    Pageable pageable = PageRequest.of(offset / limit, limit, sort);

    // Execute search
    Page<Customer> customerPage = customerRepository.searchCustomers(
        criteria.getQuery(),
        criteria.getStatus(),
        pageable
    );

    // Convert to response DTOs
    List<CustomerResponse> responseList = customerPage.getContent()
        .stream()
        .map(CustomerResponse::from)
        .collect(Collectors.toList());

    // Create pagination info
    PagedResponse.PaginationInfo pagination = PagedResponse.PaginationInfo.of(
        limit,
        offset,
        customerPage.getTotalElements()
    );

    // Create and log response
    PagedResponse<CustomerResponse> response = new PagedResponse<>(responseList, pagination);

    logger.info(
        "Customer search completed: found {} results (limit={}, offset={}, total={})",
        responseList.size(),
        limit,
        offset,
        customerPage.getTotalElements()
    );

    // Create audit entry for search operation
    auditService.createAuditEntry(
        Operation.SEARCH,
        "CUSTOMER",
        null,
        criteria,
        "Customer search performed: query=" + criteria.getQuery()
            + ", status=" + criteria.getStatus()
            + ", results=" + responseList.size()
    );

    return response;
  }

  /**
   * Updates an existing customer with provided request data.
   *
   * <p>Supports partial updates - only non-null fields in the request are updated. Tracks all
   * changes for audit purposes. Validates email uniqueness if email is being updated.
   *
   * @param customerId the customer ID to update
   * @param request the customer update request with new values
   * @return CustomerResponse with updated customer data
   * @throws ResourceNotFoundException if customer is not found
   * @throws CustomerAlreadyExistsException if email is being updated to an email that already
   *     exists for another customer
   * @throws OptimisticLockException if version conflict detected (concurrent update)
   */
  @Transactional
  public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request) {
    logger.debug("Updating customer with ID: {}", customerId);

    // Retrieve existing customer
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Customer " + customerId + " not found"));

    // Track changes before updating
    ChangeTracker tracker = ChangeTracker.trackChanges(customer, request);

    // Validate email uniqueness if email is being updated
    if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
      Optional<Customer> existingWithEmail = customerRepository.findByEmail(request.getEmail());
      if (existingWithEmail.isPresent()) {
        logger.warn("Attempted to update customer with duplicate email: {}", request.getEmail());
        throw new CustomerAlreadyExistsException(
            "Email already in use by another customer");
      }
    }

    // Apply updates - only non-null fields from request
    if (request.getFirstName() != null) {
      customer.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      customer.setLastName(request.getLastName());
    }
    if (request.getDateOfBirth() != null) {
      customer.setDateOfBirth(request.getDateOfBirth());
    }
    if (request.getEmail() != null) {
      customer.setEmail(request.getEmail());
    }
    if (request.getPhone() != null) {
      customer.setPhone(request.getPhone());
    }
    if (request.getAddress() != null) {
      customer.setAddress(request.getAddress());
    }
    if (request.getCity() != null) {
      customer.setCity(request.getCity());
    }
    if (request.getState() != null) {
      customer.setState(request.getState());
    }
    if (request.getZipCode() != null) {
      customer.setZipCode(request.getZipCode());
    }

    try {
      // Save updated customer (updatedAt will be auto-managed by JPA auditing)
      Customer savedCustomer = customerRepository.save(customer);
      logger.info("Customer updated successfully with ID: {}", customerId);

      // Create audit entry with change map
      if (tracker.hasChanges()) {
        auditService.createAuditEntry(
            Operation.UPDATE,
            "CUSTOMER",
            customerId,
            tracker.getChanges(),
            "Customer updated: " + tracker.getSummary()
        );
      } else {
        // Log when no actual changes occurred
        logger.debug("Update request for customer {} contained no actual changes", customerId);
      }

      return CustomerResponse.from(savedCustomer);
    } catch (jakarta.persistence.OptimisticLockException ex) {
      logger.warn("Optimistic lock conflict detected for customer: {}", customerId);
      throw new OptimisticLockException(
          "Customer was modified by another user. Please refresh and try again.", ex);
    }
  }

  /**
   * Soft-deletes a customer by marking status as INACTIVE and recording deletion details.
   *
   * <p>Soft-delete preserves all customer data and relationships in the database while marking
   * the customer as inactive. This enables audit history retention and compliance requirements.
   * The operation is idempotent - deleting an already-inactive customer succeeds without error.
   *
   * @param customerId the customer ID to soft-delete
   * @param reason optional deletion reason (max 500 characters) for audit purposes
   * @return CustomerResponse with updated customer data (now INACTIVE)
   * @throws ResourceNotFoundException if customer is not found
   */
  @Transactional
  public CustomerResponse deleteCustomer(UUID customerId, String reason) {
    logger.debug("Soft-deleting customer with ID: {}", customerId);

    // Retrieve existing customer
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Customer " + customerId + " not found"));

    // Log the deletion reason
    if (reason != null) {
      logger.info("Deleting customer {} with reason: {}", customerId, reason);
    } else {
      logger.info("Deleting customer {} without explicit reason", customerId);
    }

    // Set status to INACTIVE (soft delete)
    customer.setStatus(Status.INACTIVE);
    customer.setDeletedAt(LocalDateTime.now());
    customer.setDeletionReason(reason);

    // Save updated customer
    Customer savedCustomer = customerRepository.save(customer);
    logger.info("Customer soft-deleted successfully: {}", customerId);

    // Create audit entry with deletion details
    auditService.createAuditEntry(
        Operation.DELETE,
        "CUSTOMER",
        customerId,
        savedCustomer,
        "Customer soft-deleted: " + (reason != null ? reason : "no reason provided")
    );

    return CustomerResponse.from(savedCustomer);
  }

  /**
   * Retrieves all policies linked to a customer.
   *
   * <p>Fetches policies associated with the given customer ID. This method is used by
   * the Customer Detail View (Story 3.5) to display linked policies.
   *
   * <p><strong>Implementation Note:</strong> This is a placeholder method that returns an empty list.
   * Once the Policy domain model and repository are implemented (Epic 5), this method
   * should be updated to query the actual policy data from the database.
   *
   * @param customerId the customer ID to retrieve policies for
   * @return List of PolicyResponse objects (currently empty list as placeholder)
   * @throws ResourceNotFoundException if customer is not found
   */
  @Transactional(readOnly = true)
  public List<?> getPoliciesByCustomerId(UUID customerId) {
    logger.debug("Retrieving policies for customer with ID: {}", customerId);

    // Verify customer exists
    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException(
          "Customer " + customerId + " not found");
    }

    // TODO: Implement policy retrieval once Policy domain model is available
    // For now, return empty list to support Story 3.5 frontend UI
    // Expected implementation:
    // List<Policy> policies = policyRepository.findByCustomerId(customerId);
    // return policies.stream().map(PolicyResponse::from).collect(Collectors.toList());

    logger.info("No policies found for customer: {} (Policy feature not yet implemented)", customerId);
    return List.of();
  }
}
