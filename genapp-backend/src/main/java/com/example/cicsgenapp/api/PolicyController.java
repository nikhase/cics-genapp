package com.example.cicsgenapp.api;

import com.example.cicsgenapp.dto.ApiResponse;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.PolicyResponse;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import com.example.cicsgenapp.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for policy management.
 *
 * <p>Provides endpoints for policy search, filtering, and retrieval.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
@RestController
@RequestMapping("/api/v1/policies")
@Tag(name = "Policies", description = "Policy management APIs")
public class PolicyController {

  private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

  private final PolicyService policyService;

  public PolicyController(PolicyService policyService) {
    this.policyService = policyService;
  }

  /**
   * Search and list policies with filtering and pagination.
   *
   * <p>GET /api/v1/policies endpoint for searching policies.
   * Supports optional search query, type filter, status filter, and pagination.
   *
   * @param query search term (searches policy number or customer name)
   * @param type policy type filter
   * @param status policy status filter
   * @param limit results per page (default 50, max 100)
   * @param offset page offset (default 0)
   * @param sortBy field to sort by (default policyNumber)
   * @param sortOrder sort direction ASC/DESC (default ASC)
   * @return ResponseEntity with 200 status and ApiResponse containing paginated policy list
   */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Search and list policies",
      description = "Search for policies with optional filtering by type and status. Returns paginated results.",
      security = @SecurityRequirement(name = "bearer-jwt"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Policies found successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Bad Request - invalid parameters"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Unauthorized - authentication required")
  })
  public ResponseEntity<ApiResponse<PagedResponse<PolicyResponse>>> searchPolicies(
      @RequestParam(required = false)
      @Parameter(description = "Search query (searches policyNumber, customerName)")
      String query,

      @RequestParam(required = false)
      @Parameter(description = "Filter by policy type (MOTOR, ENDOWMENT, HOUSE, COMMERCIAL)")
      PolicyType type,

      @RequestParam(required = false)
      @Parameter(description = "Filter by policy status (ACTIVE, LAPSED, RENEWED)")
      PolicyStatus status,

      @RequestParam(defaultValue = "50")
      @Parameter(description = "Results per page (1-100)")
      int limit,

      @RequestParam(defaultValue = "0")
      @Parameter(description = "Page offset (0-indexed)")
      int offset,

      @RequestParam(defaultValue = "policyNumber")
      @Parameter(description = "Sort field (policyNumber, customerName, policyType, status, premium, effectiveDate)")
      String sortBy,

      @RequestParam(defaultValue = "ASC")
      @Parameter(description = "Sort order (ASC or DESC)")
      String sortOrder) {

    logger.debug(
        "Searching policies - query: {}, type: {}, status: {}, limit: {}, offset: {}",
        query, type, status, limit, offset);

    PagedResponse<PolicyResponse> policies = policyService.searchPolicies(
        query, type, status, limit, offset, sortBy, sortOrder);

    ApiResponse<PagedResponse<PolicyResponse>> response = new ApiResponse<>(policies);
    response.withMetadata("message", "Policies retrieved successfully");
    return ResponseEntity.ok(response);
  }

  /**
   * Get policy details by ID.
   *
   * <p>GET /api/v1/policies/{policyId} endpoint for retrieving specific policy.
   *
   * @param policyId the policy ID (UUID)
   * @return ResponseEntity with 200 status and ApiResponse containing policy details
   */
  @GetMapping("/{policyId}")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Get policy by ID",
      description = "Retrieve full details of a specific policy by ID",
      security = @SecurityRequirement(name = "bearer-jwt"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Policy found successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "404",
          description = "Policy not found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Unauthorized - authentication required")
  })
  public ResponseEntity<ApiResponse<PolicyResponse>> getPolicy(
      @PathVariable
      @Parameter(description = "Policy ID (UUID)")
      UUID policyId) {

    logger.debug("Getting policy details - policyId: {}", policyId);

    PolicyResponse policy = policyService.getPolicyById(policyId);

    ApiResponse<PolicyResponse> response = new ApiResponse<>(policy);
    response.withMetadata("message", "Policy retrieved successfully");
    return ResponseEntity.ok(response);
  }
}
