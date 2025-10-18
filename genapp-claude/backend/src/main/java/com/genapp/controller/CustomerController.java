package com.genapp.controller;

import com.genapp.dto.CustomerDTO;
import com.genapp.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CustomerController - REST API endpoints for customer management
 *
 * Replaces the COBOL presentation layer programs:
 *   - lgtestc1.cbl: Customer test/demo transaction
 *   - BMS screen handling via 3270 terminals
 *
 * Old CICS Architecture:
 *   3270 Terminal → BMS Map → lgtestc1 → EXEC CICS LINK (with COMMAREA)
 *
 * New REST API Architecture:
 *   Browser/Client → HTTP REST → @RestController → @Service → Repository → PostgreSQL
 *
 * Key improvements:
 *   ✅ Standard HTTP verbs (POST, GET, PUT, DELETE)
 *   ✅ JSON payloads (2-3KB vs 32.5KB COMMAREA)
 *   ✅ Automatic OpenAPI/Swagger documentation
 *   ✅ Standard HTTP status codes (201, 200, 404, 400)
 *   ✅ No more 3270 terminal required
 *   ✅ API can be consumed by any client (web, mobile, CLI)
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Customer management operations (replaces lgtestc1.cbl)")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Create a new customer
     *
     * HTTP: POST /api/customers
     * Body: CustomerDTO JSON
     * Response: 201 Created with Location header
     *
     * Old COBOL equivalent:
     *   User enters data via 3270 screen → BMS validation → lgtestc1 calls lgacus01
     *   lgacus01 validates and inserts → returns to lgtestc1 → displays result on screen
     *
     * New REST equivalent:
     *   POST request with JSON body → validation via @Valid → service layer → database
     *   Response: 201 with created customer and ID
     *
     * Example request:
     * ```
     * POST /api/customers
     * Content-Type: application/json
     *
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "address": "123 Main St",
     *   "city": "New York",
     *   "state": "NY",
     *   "zipCode": "10001",
     *   "phone": "(555) 123-4567",
     *   "email": "john.doe@example.com"
     * }
     * ```
     *
     * Example response:
     * ```
     * HTTP/1.1 201 Created
     * Content-Type: application/json
     * Location: /api/customers/1001
     *
     * {
     *   "customerId": 1001,
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   ...
     * }
     * ```
     *
     * @param customerDTO customer data (validated with @Valid)
     * @return ResponseEntity with 201 status and created customer
     */
    @PostMapping
    @Operation(summary = "Create new customer", description = "Add a new customer to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
                content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("POST /api/customers - Creating customer: {}", customerDTO.firstName());

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        log.info("Customer created with ID: {}", createdCustomer.customerId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/customers/" + createdCustomer.customerId())
                .body(createdCustomer);
    }

    /**
     * Get customer by ID
     *
     * HTTP: GET /api/customers/{id}
     * Response: 200 OK with customer data or 404 Not Found
     *
     * Old COBOL equivalent:
     *   User enters customer ID on screen → BMS validation → lgtestc1 calls lgicus01
     *   lgicus01 reads from VSAM/Db2 → returns to lgtestc1 → displays on screen
     *
     * New REST equivalent:
     *   GET request with ID in path → service retrieves from database
     *   Response: 200 with customer data or 404
     *
     * Example request:
     * ```
     * GET /api/customers/1001
     * ```
     *
     * Example response:
     * ```
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     *
     * {
     *   "customerId": 1001,
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   ...
     * }
     * ```
     *
     * @param customerId customer ID to retrieve
     * @return ResponseEntity with 200 status and customer data
     */
    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomer(
            @Parameter(description = "Customer ID", example = "1001")
            @PathVariable Long customerId) {
        log.info("GET /api/customers/{} - Retrieving customer", customerId);

        CustomerDTO customer = customerService.getCustomer(customerId);
        log.debug("Customer retrieved: {}", customer.fullName());

        return ResponseEntity.ok(customer);
    }

    /**
     * Update existing customer
     *
     * HTTP: PUT /api/customers/{id}
     * Body: CustomerDTO JSON with updated fields
     * Response: 200 OK with updated customer or 404 Not Found
     *
     * Old COBOL equivalent:
     *   User modifies fields on screen → BMS validation → lgtestc1 calls lgucus01
     *   lgucus01 validates and updates VSAM/Db2 → returns to lgtestc1 → confirms on screen
     *
     * New REST equivalent:
     *   PUT request with ID in path and JSON body → service validates and updates
     *   Response: 200 with updated customer or 404
     *
     * Example request:
     * ```
     * PUT /api/customers/1001
     * Content-Type: application/json
     *
     * {
     *   "firstName": "Jonathan",
     *   "lastName": "Doe",
     *   "email": "jonathan.doe@example.com"
     * }
     * ```
     *
     * Example response:
     * ```
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     *
     * {
     *   "customerId": 1001,
     *   "firstName": "Jonathan",
     *   "lastName": "Doe",
     *   ...
     * }
     * ```
     *
     * @param customerId customer ID to update
     * @param customerDTO updated customer data
     * @return ResponseEntity with 200 status and updated customer
     */
    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Update an existing customer's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Customer ID", example = "1001")
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("PUT /api/customers/{} - Updating customer", customerId);

        CustomerDTO updatedCustomer = customerService.updateCustomer(customerId, customerDTO);
        log.info("Customer updated: ID {}", customerId);

        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Delete customer by ID
     *
     * HTTP: DELETE /api/customers/{id}
     * Response: 204 No Content on success or 404 Not Found
     *
     * Old COBOL equivalent:
     *   User selects delete option → BMS confirmation → lgtestc1 calls lgdcus01
     *   lgdcus01 deletes from VSAM/Db2 → returns to lgtestc1 → confirms deletion
     *
     * New REST equivalent:
     *   DELETE request with ID → service deletes from database
     *   Response: 204 No Content or 404
     *
     * @param customerId customer ID to delete
     * @return ResponseEntity with 204 No Content
     */
    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer", description = "Remove a customer from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", example = "1001")
            @PathVariable Long customerId) {
        log.info("DELETE /api/customers/{} - Deleting customer", customerId);

        customerService.deleteCustomer(customerId);
        log.info("Customer deleted: ID {}", customerId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get all customers
     *
     * HTTP: GET /api/customers
     * Response: 200 OK with list of all customers
     *
     * Old COBOL equivalent:
     *   Sequential read through entire VSAM file with manual looping
     *
     * New REST equivalent:
     *   GET request → retrieves all customers from database
     *   Response: 200 with array of customers
     *
     * @return ResponseEntity with 200 status and list of customers
     */
    @GetMapping
    @Operation(summary = "List all customers", description = "Retrieve all customers in the system")
    @ApiResponse(responseCode = "200", description = "List of customers",
            content = @Content(schema = @Schema(implementation = CustomerDTO.class)))
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("GET /api/customers - Retrieving all customers");

        List<CustomerDTO> customers = customerService.getAllCustomers();
        log.debug("Retrieved {} customers", customers.size());

        return ResponseEntity.ok(customers);
    }

    /**
     * Search customers by full name pattern
     *
     * HTTP: GET /api/customers/search?pattern=john+doe
     * Response: 200 OK with matching customers
     *
     * Old COBOL equivalent:
     *   Manual sequential read with pattern matching on name
     *
     * @param pattern name pattern to search for (using SQL LIKE)
     * @return ResponseEntity with 200 status and matching customers
     */
    @GetMapping("/search")
    @Operation(summary = "Search customers by name", description = "Find customers by full name pattern")
    @ApiResponse(responseCode = "200", description = "List of matching customers")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(
            @Parameter(description = "Name pattern to search", example = "John")
            @RequestParam String pattern) {
        log.info("GET /api/customers/search?pattern={} - Searching customers", pattern);

        List<CustomerDTO> results = customerService.searchByFullName(pattern);
        log.debug("Found {} customers matching pattern: {}", results.size(), pattern);

        return ResponseEntity.ok(results);
    }
}
