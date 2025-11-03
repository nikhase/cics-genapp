package com.example.cicsgenapp.api;

import com.example.cicsgenapp.dto.ApiResponse;
import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.dto.DeleteCustomerRequest;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.SearchCriteria;
import com.example.cicsgenapp.dto.UpdateCustomerRequest;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for customer management.
 *
 * <p>Provides endpoints for customer CRUD operations. Replaces legacy COBOL SSC1 transaction
 * with modern REST API endpoints.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  private final CustomerService customerService;

  /**
   * Constructs CustomerController with required dependencies.
   *
   * @param customerService the customer service for business logic
   */
  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  /**
   * Retrieves a customer by ID.
   *
   * <p>GET /api/v1/customers/{customerId} endpoint for retrieving customer details. Returns 200
   * OK with customer information if found, 404 Not Found if customer doesn't exist.
   *
   * <p>Requires authentication but no specific role for read-only operation.
   *
   * @param customerId the customer ID (UUID)
   * @return ResponseEntity with 200 status and ApiResponse containing customer data
   * @throws ResourceNotFoundException if customer not found (returns 404)
   */
  @GetMapping("/{customerId}")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Get customer by ID",
      description = "Retrieves customer details by ID. Returns all customer information including timestamps and audit fields.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Customer found successfully",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Bad Request - invalid customer ID format"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "404",
          description = "Not Found - customer with specified ID does not exist"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Unauthorized - authentication required"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
      @PathVariable UUID customerId) {

    logger.info("GET /api/v1/customers/{} - Retrieving customer", customerId);

    try {
      // Business logic delegated to service
      CustomerResponse response = customerService.getCustomer(customerId);

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "READ");

      // Create response envelope
      ApiResponse<CustomerResponse> apiResponse = new ApiResponse<>(response, metadata);

      logger.info("Customer retrieved successfully: {}", customerId);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error retrieving customer {}: {}", customerId, ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }

  /**
   * Searches for customers with pagination, filtering, and sorting.
   *
   * <p>GET /api/v1/customers endpoint for searching customers. Supports multi-field search
   * (firstName, lastName, email, phone), status filtering, sorting, and pagination.
   *
   * <p>Query parameters:
   * - query (optional): substring search across firstName, lastName, email, phone (case-insensitive)
   * - status (optional): filter by ACTIVE or INACTIVE status
   * - limit (optional, default 50, max 100): page size
   * - offset (optional, default 0): pagination offset
   * - sortBy (optional, default "lastName"): field to sort by
   * - sortOrder (optional, default "ASC"): sort direction (ASC or DESC)
   *
   * @param query search query string (optional)
   * @param status customer status filter (optional)
   * @param limit page size (default 50, max 100)
   * @param offset pagination offset (default 0)
   * @param sortBy field to sort by (default lastName)
   * @param sortOrder sort direction (default ASC)
   * @return ResponseEntity with 200 status and paginated customer results
   */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Search/list customers",
      description = "Search for customers with multi-field search, filtering, sorting, and pagination. "
          + "Returns paginated results with pagination metadata.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Search results retrieved successfully",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Bad Request - invalid query parameters (e.g., negative offset, invalid sortOrder)"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Unauthorized - authentication required"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>> searchCustomers(
      @RequestParam(required = false)
      @Parameter(description = "Search query (searches firstName, lastName, email, phone)")
      String query,

      @RequestParam(required = false)
      @Parameter(description = "Filter by customer status (ACTIVE, INACTIVE)")
      Status status,

      @RequestParam(defaultValue = "50")
      @Parameter(description = "Page size (default 50, max 100)")
      int limit,

      @RequestParam(defaultValue = "0")
      @Parameter(description = "Pagination offset (default 0)")
      int offset,

      @RequestParam(defaultValue = "lastName")
      @Parameter(description = "Field to sort by (firstName, lastName, email, createdAt)")
      String sortBy,

      @RequestParam(defaultValue = "ASC")
      @Parameter(description = "Sort direction (ASC or DESC)")
      String sortOrder
  ) {

    logger.info(
        "GET /api/v1/customers - Searching customers: query={}, status={}, limit={}, offset={}, sortBy={}, sortOrder={}",
        query, status, limit, offset, sortBy, sortOrder
    );

    try {
      // Build search criteria
      SearchCriteria criteria = new SearchCriteria(query, status, limit, offset, sortBy, sortOrder);

      // Call service to execute search
      PagedResponse<CustomerResponse> response = customerService.searchCustomers(criteria);

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "SEARCH");
      metadata.put("resultCount", response.getData().size());

      // Create response envelope
      ApiResponse<PagedResponse<CustomerResponse>> apiResponse = new ApiResponse<>(response, metadata);

      logger.info(
          "Customer search completed: found {} results (limit={}, offset={}, total={})",
          response.getData().size(),
          limit,
          offset,
          response.getPagination().getTotal()
      );

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error searching customers: {}", ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }

  /**
   * Creates a new customer record.
   *
   * <p>POST /api/v1/customers endpoint for creating customers. Accepts customer information
   * in request body, validates, and persists to database. Returns 201 Created with created
   * customer details.
   *
   * <p>Requires CUSTOMER_SERVICE_AGENT or ADMIN role for authorization.
   *
   * @param request the customer creation request containing firstName, lastName, email, etc.
   * @return ResponseEntity with 201 status and ApiResponse containing created customer
   *
   * @throws CustomerAlreadyExistsException if email already exists (returns 409 Conflict)
   * @throws MethodArgumentNotValidException if validation fails (returns 400 Bad Request)
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_AGENT', 'ADMIN')")
  @Operation(
      summary = "Create a new customer",
      description = "Creates a new customer record with provided details. Email must be unique.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "Customer created successfully",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Validation error - invalid request format or missing required fields"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "409",
          description = "Conflict - customer with this email already exists"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "403",
          description = "Forbidden - insufficient permissions to create customer"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
      @Valid @RequestBody CreateCustomerRequest request) {

    logger.info("POST /api/v1/customers - Creating customer with email: {}", request.getEmail());

    try {
      // Business logic delegated to service
      CustomerResponse response = customerService.createCustomer(request);

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "CREATE");

      // Create response envelope
      ApiResponse<CustomerResponse> apiResponse = new ApiResponse<>(response, metadata);

      logger.info("Customer created successfully with ID: {}", response.getCustomerId());

      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error creating customer: {}", ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }

  /**
   * Updates an existing customer record.
   *
   * <p>PUT /api/v1/customers/{customerId} endpoint for updating customers. Supports partial updates
   * - only provided fields are updated, others remain unchanged. Validates email uniqueness if email
   * is being updated. Returns 200 OK with updated customer details.
   *
   * <p>Requires CUSTOMER_SERVICE_AGENT or ADMIN role for authorization.
   *
   * @param customerId the customer ID (UUID) to update
   * @param request the customer update request containing fields to update
   * @return ResponseEntity with 200 status and ApiResponse containing updated customer
   *
   * @throws ResourceNotFoundException if customer not found (returns 404)
   * @throws CustomerAlreadyExistsException if email is being updated to one that already exists
   *     (returns 409 Conflict)
   * @throws OptimisticLockException if version conflict detected (returns 409 Conflict)
   * @throws MethodArgumentNotValidException if validation fails (returns 400 Bad Request)
   */
  @PutMapping("/{customerId}")
  @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_AGENT', 'ADMIN')")
  @Operation(
      summary = "Update a customer",
      description = "Updates an existing customer record with provided details. Supports partial updates - "
          + "only non-null fields are updated. Email must be unique if being updated.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Customer updated successfully",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Validation error - invalid request format or field constraints violated"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "404",
          description = "Not Found - customer with specified ID does not exist"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "409",
          description = "Conflict - email already exists for another customer or version conflict (concurrent update)"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "403",
          description = "Forbidden - insufficient permissions to update customer"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
      @PathVariable UUID customerId,
      @Valid @RequestBody UpdateCustomerRequest request) {

    logger.info("PUT /api/v1/customers/{} - Updating customer", customerId);

    try {
      // Business logic delegated to service
      CustomerResponse response = customerService.updateCustomer(customerId, request);

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "UPDATE");

      // Create response envelope
      ApiResponse<CustomerResponse> apiResponse = new ApiResponse<>(response, metadata);

      logger.info("Customer updated successfully: {}", customerId);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error updating customer {}: {}", customerId, ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }

  /**
   * Soft-deletes an existing customer record (marks as INACTIVE).
   *
   * <p>DELETE /api/v1/customers/{customerId} endpoint for soft-deleting customers. Marks the customer
   * as INACTIVE while preserving all data in the database for audit and compliance purposes.
   * This is not a hard delete - the customer record remains queryable. The operation is idempotent:
   * deleting an already-inactive customer returns 200 OK without error.
   *
   * <p>Requires COMPLIANCE_OFFICER or ADMIN role for authorization (compliance-sensitive operation).
   *
   * @param customerId the customer ID (UUID) to soft-delete
   * @param request optional delete request containing deletion reason
   * @return ResponseEntity with 200 status and ApiResponse containing deleted customer (now INACTIVE)
   *
   * @throws ResourceNotFoundException if customer not found (returns 404)
   */
  @DeleteMapping("/{customerId}")
  @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'ADMIN')")
  @Operation(
      summary = "Soft-delete customer (mark as inactive)",
      description = "Marks an existing customer as INACTIVE (soft delete). Does NOT hard-delete data. "
          + "Customer record remains in database for audit/compliance purposes. "
          + "Operation is idempotent - deleting an already-inactive customer succeeds with 200 OK.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Customer deleted successfully (soft delete)",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Bad Request - invalid customer ID format or request validation failed"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "404",
          description = "Not Found - customer with specified ID does not exist"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "403",
          description = "Forbidden - insufficient permissions (requires COMPLIANCE_OFFICER or ADMIN)"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<CustomerResponse>> deleteCustomer(
      @PathVariable UUID customerId,
      @RequestBody(required = false) DeleteCustomerRequest request) {

    logger.info("DELETE /api/v1/customers/{} - Soft-deleting customer", customerId);

    try {
      // Extract reason from request if provided
      String reason = (request != null) ? request.getReason() : null;

      // Business logic delegated to service
      CustomerResponse response = customerService.deleteCustomer(customerId, reason);

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "DELETE");
      metadata.put("deletionType", "SOFT_DELETE");

      // Create response envelope
      ApiResponse<CustomerResponse> apiResponse = new ApiResponse<>(response, metadata);

      logger.info("Customer soft-deleted successfully: {}", customerId);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error deleting customer {}: {}", customerId, ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }
}
