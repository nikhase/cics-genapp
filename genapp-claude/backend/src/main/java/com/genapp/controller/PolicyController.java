package com.genapp.controller;

import com.genapp.dto.*;
import com.genapp.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PolicyController - REST API endpoints for Policy management
 *
 * Replaces the COBOL presentation layer (3270 BMS screens):
 *   Old COBOL pattern:
 *   1. Display SSP1 menu (Motor policy options)
 *   2. Agent enters data
 *   3. ACCEPT input into COMMAREA
 *   4. LINK to business logic (lgXXpol01.cbl)
 *   5. Display results or error message
 *
 * New Spring pattern:
 *   1. Client POSTs JSON to REST endpoint
 *   2. Spring deserializes to DTO with validation
 *   3. Controller calls service
 *   4. Service returns DTO
 *   5. Controller serializes to JSON response
 *
 * Benefits:
 *   ✅ RESTful API instead of CICS transactions
 *   ✅ JSON instead of COMMAREA (2-3KB vs 32.5KB)
 *   ✅ Automatic Swagger/OpenAPI documentation
 *   ✅ Standard HTTP status codes (200, 201, 404, 400, etc.)
 *   ✅ Web browser and mobile client compatible
 *
 * Endpoints:
 *   POST   /api/policies/motor              - Create motor policy
 *   POST   /api/policies/house              - Create house policy
 *   POST   /api/policies/endowment          - Create endowment policy
 *   POST   /api/policies/commercial         - Create commercial policy
 *   GET    /api/policies/{policyId}         - Get policy details
 *   GET    /api/customers/{customerId}/policies  - Get all policies for customer
 *   GET    /api/customers/{customerId}/policies?type=MOTOR  - Get policies by type
 *   DELETE /api/policies/{policyId}         - Cancel/terminate policy
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policy Management", description = "APIs for managing insurance policies (Motor, House, Endowment, Commercial)")
public class PolicyController {

    private static final Logger log = LoggerFactory.getLogger(PolicyController.class);

    private final PolicyService policyService;

    /**
     * Constructor for dependency injection
     */
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // ===== CREATE ENDPOINTS =====

    /**
     * Create a new motor insurance policy
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGACPOL01') - Add motor policy
     *   READ SSP1 motor policy form
     *
     * Acceptance Criteria (AC7):
     *   - Given: customer exists
     *   - When: valid motor policy data provided
     *   - Then: HTTP 201 Created with policy ID
     *
     * @param motorPolicyDTO motor policy details
     * @return 201 Created with created policy
     */
    @PostMapping("/motor")
    @Operation(summary = "Create a new motor insurance policy",
            description = "Creates a motor (vehicle) insurance policy for a customer. Returns 201 Created on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Motor policy created successfully",
                    content = @Content(schema = @Schema(implementation = MotorPolicyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Policy number already exists for this customer")
    })
    public ResponseEntity<MotorPolicyDTO> createMotorPolicy(
            @Valid @RequestBody MotorPolicyDTO motorPolicyDTO) {
        log.info("Creating motor policy for customer: {}", motorPolicyDTO.customerId());

        MotorPolicyDTO createdPolicy = policyService.createMotorPolicy(motorPolicyDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    /**
     * Create a new house insurance policy
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGACPOL01') - Add house policy
     *   READ SSP3 house policy form
     *
     * Acceptance Criteria (AC8):
     *   - Given: customer exists
     *   - When: valid house policy data provided
     *   - Then: HTTP 201 Created with policy ID
     *
     * @param housePolicyDTO house policy details
     * @return 201 Created with created policy
     */
    @PostMapping("/house")
    @Operation(summary = "Create a new house insurance policy",
            description = "Creates a house (property) insurance policy for a customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "House policy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Policy number already exists for this customer")
    })
    public ResponseEntity<HousePolicyDTO> createHousePolicy(
            @Valid @RequestBody HousePolicyDTO housePolicyDTO) {
        log.info("Creating house policy for customer: {}", housePolicyDTO.customerId());

        HousePolicyDTO createdPolicy = policyService.createHousePolicy(housePolicyDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    /**
     * Create a new endowment insurance policy
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGACPOL01') - Add endowment policy
     *   READ SSP2 endowment policy form
     *
     * Acceptance Criteria (AC9):
     *   - Given: customer exists
     *   - When: valid endowment policy data provided
     *   - Then: HTTP 201 Created with policy ID
     *
     * @param endowmentPolicyDTO endowment policy details
     * @return 201 Created with created policy
     */
    @PostMapping("/endowment")
    @Operation(summary = "Create a new endowment insurance policy",
            description = "Creates an endowment (investment/life) insurance policy for a customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Endowment policy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Policy number already exists for this customer")
    })
    public ResponseEntity<EndowmentPolicyDTO> createEndowmentPolicy(
            @Valid @RequestBody EndowmentPolicyDTO endowmentPolicyDTO) {
        log.info("Creating endowment policy for customer: {}", endowmentPolicyDTO.customerId());

        EndowmentPolicyDTO createdPolicy = policyService.createEndowmentPolicy(endowmentPolicyDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    /**
     * Create a new commercial insurance policy
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGACPOL01') - Add commercial policy
     *   READ SSP4 commercial policy form
     *
     * Acceptance Criteria (AC10):
     *   - Given: customer exists
     *   - When: valid commercial policy data provided
     *   - Then: HTTP 201 Created with policy ID
     *
     * @param commercialPolicyDTO commercial policy details
     * @return 201 Created with created policy
     */
    @PostMapping("/commercial")
    @Operation(summary = "Create a new commercial insurance policy",
            description = "Creates a commercial (business) insurance policy for a customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commercial policy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Policy number already exists for this customer")
    })
    public ResponseEntity<CommercialPolicyDTO> createCommercialPolicy(
            @Valid @RequestBody CommercialPolicyDTO commercialPolicyDTO) {
        log.info("Creating commercial policy for customer: {}", commercialPolicyDTO.customerId());

        CommercialPolicyDTO createdPolicy = policyService.createCommercialPolicy(commercialPolicyDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    // ===== READ ENDPOINTS =====

    /**
     * Get a specific policy by ID
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGICPOL01') - Inquire policy
     *   EXEC CICS READ FILE('KSDSPOLY')
     *
     * Acceptance Criteria (AC5 modified for policies):
     *   - Given: policy exists
     *   - When: GET /api/policies/{id}
     *   - Then: HTTP 200 OK with policy details
     *
     * @param policyId policy ID to retrieve
     * @return 200 OK with policy details
     */
    @GetMapping("/{policyId}")
    @Operation(summary = "Get policy details",
            description = "Retrieves a specific policy by ID. Returns the appropriate type (Motor, House, etc.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Policy found"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    public ResponseEntity<?> getPolicy(
            @Parameter(description = "Policy ID") @PathVariable Long policyId) {
        log.info("Retrieving policy ID: {}", policyId);

        Object policy = policyService.getPolicyById(policyId);

        return ResponseEntity.ok(policy);
    }

    /**
     * Get all policies for a customer
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGICPOL01') with customer ID
     *   Manual sequential read of all customer's policies from KSDSPOLY
     *
     * Acceptance Criteria (AC11):
     *   - Given: customer with 3 policies exists
     *   - When: GET /api/customers/{customerId}/policies
     *   - Then: HTTP 200 OK with array of 3 policies
     *
     * @param customerId customer ID
     * @return 200 OK with list of policies
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all policies for a customer",
            description = "Retrieves all insurance policies for a specific customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Policies retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<List<?>> getPoliciesByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Retrieving policies for customer: {}", customerId);

        List<?> policies = policyService.getPoliciesByCustomerId(customerId);

        return ResponseEntity.ok(policies);
    }

    /**
     * Get policies of specific type for a customer
     *
     * Endpoint: GET /api/policies/customer/1?type=MOTOR
     *
     * @param customerId customer ID
     * @param type policy type (MOTOR, HOUSE, ENDOWMENT, COMMERCIAL)
     * @return 200 OK with filtered policies
     */
    @GetMapping("/customer/{customerId}/type/{type}")
    @Operation(summary = "Get policies of specific type for a customer",
            description = "Retrieves policies of a specific type (Motor, House, Endowment, Commercial) for a customer.")
    public ResponseEntity<?> getPoliciesByCustomerAndType(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @Parameter(description = "Policy type: MOTOR, HOUSE, ENDOWMENT, or COMMERCIAL") @PathVariable String type) {
        log.info("Retrieving {} policies for customer: {}", type, customerId);

        return switch (type.toUpperCase()) {
            case "MOTOR" -> ResponseEntity.ok(policyService.getMotorPoliciesByCustomerId(customerId));
            case "HOUSE" -> ResponseEntity.ok(policyService.getHousePoliciesByCustomerId(customerId));
            case "ENDOWMENT" -> ResponseEntity.ok(policyService.getEndowmentPoliciesByCustomerId(customerId));
            case "COMMERCIAL" -> ResponseEntity.ok(policyService.getCommercialPoliciesByCustomerId(customerId));
            default -> ResponseEntity.badRequest().body("Invalid policy type: " + type);
        };
    }

    /**
     * Get active policies for a customer
     *
     * Old COBOL equivalent: Filter for status = 'ACTIVE'
     *
     * @param customerId customer ID
     * @return 200 OK with active policies
     */
    @GetMapping("/customer/{customerId}/active")
    @Operation(summary = "Get active policies for a customer",
            description = "Retrieves only active (non-terminated) policies for a customer.")
    public ResponseEntity<List<?>> getActivePolicies(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Retrieving active policies for customer: {}", customerId);

        List<?> policies = policyService.getActivePoliciesByCustomerId(customerId);

        return ResponseEntity.ok(policies);
    }

    // ===== DELETE/CANCEL ENDPOINTS =====

    /**
     * Cancel/terminate a policy
     *
     * Old COBOL equivalent:
     *   LINK PROGRAM('LGDCPOL01') - Delete/cancel policy
     *   UPDATE policy status to 'TERMINATED'
     *
     * Acceptance Criteria (AC13):
     *   - Given: policy exists with status ACTIVE
     *   - When: DELETE /api/policies/{id}
     *   - Then: HTTP 204 No Content, policy status changed to TERMINATED
     *
     * @param policyId policy ID to cancel
     * @return 204 No Content
     */
    @DeleteMapping("/{policyId}")
    @Operation(summary = "Cancel/terminate a policy",
            description = "Changes policy status from ACTIVE to TERMINATED. Policy is not deleted, just marked as terminated.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Policy canceled successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "400", description = "Policy is already terminated or cannot be canceled")
    })
    public ResponseEntity<Void> cancelPolicy(
            @Parameter(description = "Policy ID") @PathVariable Long policyId) {
        log.info("Canceling policy ID: {}", policyId);

        policyService.cancelPolicy(policyId);

        return ResponseEntity.noContent().build();
    }

    // ===== UTILITY ENDPOINTS =====

    /**
     * Get count of policies for a customer
     *
     * @param customerId customer ID
     * @return 200 OK with policy count
     */
    @GetMapping("/customer/{customerId}/count")
    @Operation(summary = "Count policies for a customer",
            description = "Returns the total number of policies (active and terminated) for a customer.")
    public ResponseEntity<Long> countPolicies(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Counting policies for customer: {}", customerId);

        long count = policyService.countPoliciesByCustomerId(customerId);

        return ResponseEntity.ok(count);
    }

    /**
     * Get count of active policies for a customer
     *
     * @param customerId customer ID
     * @return 200 OK with active policy count
     */
    @GetMapping("/customer/{customerId}/active/count")
    @Operation(summary = "Count active policies for a customer",
            description = "Returns the number of active (non-terminated) policies for a customer.")
    public ResponseEntity<Long> countActivePolicies(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        log.info("Counting active policies for customer: {}", customerId);

        long count = policyService.countActivePoliciesByCustomerId(customerId);

        return ResponseEntity.ok(count);
    }

    /**
     * Get all expired policies
     *
     * Useful for reporting and analysis
     *
     * @return 200 OK with expired policies
     */
    @GetMapping("/expired")
    @Operation(summary = "Get all expired policies",
            description = "Retrieves all policies that have ended (end date before today). Useful for reporting.")
    public ResponseEntity<List<?>> getExpiredPolicies() {
        log.info("Retrieving expired policies");

        List<?> policies = policyService.getExpiredPolicies();

        return ResponseEntity.ok(policies);
    }
}
