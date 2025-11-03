package com.example.cicsgenapp.api;

import com.example.cicsgenapp.dto.ApiResponse;
import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
