package com.example.cicsgenapp.api;

import com.example.cicsgenapp.dto.ApiResponse;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.entity.AuditLog;
import com.example.cicsgenapp.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for audit log queries.
 *
 * <p>Provides endpoints for compliance officers and administrators to query audit logs.
 * Access is restricted to ADMIN and COMPLIANCE_OFFICER roles. Audit logs are immutable
 * and can only be read, never updated or deleted.
 */
@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
@Tag(name = "Audit Logs", description = "Audit log query APIs for compliance and auditing")
public class AuditController {

  private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

  private final AuditLogRepository auditLogRepository;

  /**
   * Constructs AuditController with required dependencies.
   *
   * @param auditLogRepository the audit log repository
   */
  public AuditController(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  /**
   * Queries audit logs with optional filtering by entity type, operation, and entity ID.
   *
   * <p>GET /api/v1/audit endpoint for querying audit logs. Supports filtering and pagination.
   * Only accessible to ADMIN and COMPLIANCE_OFFICER roles.
   *
   * <p>Query parameters:
   * - entity (optional): filter by entity type (CUSTOMER, POLICY, etc.)
   * - entityId (optional): filter by specific entity ID
   * - operation (optional): filter by operation type (CREATE, READ, UPDATE, DELETE)
   * - limit (optional, default 100, max 500): page size
   * - offset (optional, default 0): pagination offset
   *
   * @param entity optional entity type filter
   * @param entityId optional entity ID filter
   * @param operation optional operation type filter
   * @param limit page size (default 100, max 500)
   * @param offset pagination offset (default 0)
   * @return ResponseEntity with 200 status and paginated audit log results
   */
  @GetMapping
  @Operation(
      summary = "Query audit logs",
      description = "Query audit logs with optional filters by entity, operation, and date range. "
          + "Returns paginated results. Restricted to ADMIN and COMPLIANCE_OFFICER roles.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Audit logs retrieved successfully",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Bad Request - invalid query parameters"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Unauthorized - authentication required"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "403",
          description = "Forbidden - requires ADMIN or COMPLIANCE_OFFICER role"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<PagedResponse<AuditLog>>> queryAuditLogs(
      @RequestParam(required = false)
      @Parameter(description = "Filter by entity type (CUSTOMER, POLICY, etc.)")
      String entity,

      @RequestParam(required = false)
      @Parameter(description = "Filter by specific entity ID")
      UUID entityId,

      @RequestParam(required = false)
      @Parameter(description = "Filter by operation (CREATE, READ, UPDATE, DELETE)")
      String operation,

      @RequestParam(defaultValue = "100")
      @Parameter(description = "Page size (default 100, max 500)")
      int limit,

      @RequestParam(defaultValue = "0")
      @Parameter(description = "Pagination offset (default 0)")
      int offset
  ) {

    logger.info(
        "GET /api/v1/audit - Querying audit logs: entity={}, entityId={}, operation={}, limit={}, offset={}",
        entity, entityId, operation, limit, offset
    );

    try {
      // Validate and enforce limits
      if (limit > 500) {
        limit = 500;
      }
      if (limit <= 0) {
        limit = 100;
      }
      if (offset < 0) {
        offset = 0;
      }

      // Query audit logs based on filters
      List<AuditLog> auditLogs;
      if (entity != null && entityId != null) {
        auditLogs = auditLogRepository.findByEntityTypeAndEntityId(entity, entityId);
      } else if (operation != null) {
        try {
          com.example.cicsgenapp.entity.Operation op =
              com.example.cicsgenapp.entity.Operation.valueOf(operation.toUpperCase());
          auditLogs = auditLogRepository.findByOperation(op);
        } catch (IllegalArgumentException ex) {
          logger.warn("Invalid operation filter: {}", operation);
          auditLogs = List.of();
        }
      } else if (entity != null) {
        // Filter by entity type only - get all audit logs for that entity type
        auditLogs = auditLogRepository.findAll()
            .stream()
            .filter(log -> log.getEntityType().equals(entity))
            .toList();
      } else {
        // No filters - get all audit logs
        auditLogs = auditLogRepository.findAll();
      }

      // Apply pagination
      int totalCount = auditLogs.size();
      int fromIndex = Math.min(offset, auditLogs.size());
      int toIndex = Math.min(fromIndex + limit, auditLogs.size());
      List<AuditLog> paginatedLogs = auditLogs.subList(fromIndex, toIndex);

      // Build pagination info using factory method
      PagedResponse.PaginationInfo pagination = PagedResponse.PaginationInfo.of(
          limit,
          offset,
          (long) totalCount
      );

      // Create response
      PagedResponse<AuditLog> response = new PagedResponse<>(paginatedLogs, pagination);

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "QUERY_AUDIT");
      metadata.put("resultCount", paginatedLogs.size());
      metadata.put("totalCount", totalCount);

      // Create response envelope
      ApiResponse<PagedResponse<AuditLog>> apiResponse = new ApiResponse<>(response, metadata);

      logger.info(
          "Audit log query completed: returned {} of {} total results (limit={}, offset={})",
          paginatedLogs.size(),
          totalCount,
          limit,
          offset
      );

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error querying audit logs: {}", ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }

  /**
   * Retrieves a specific audit log entry by ID.
   *
   * <p>GET /api/v1/audit/{auditId} endpoint for retrieving a single audit log entry.
   * Only accessible to ADMIN and COMPLIANCE_OFFICER roles.
   *
   * @param auditId the audit log ID (UUID)
   * @return ResponseEntity with 200 status and audit log details
   */
  @GetMapping("/{auditId}")
  @Operation(
      summary = "Get audit log by ID",
      description = "Retrieves a specific audit log entry by ID. Restricted to ADMIN and COMPLIANCE_OFFICER roles.",
      security = @SecurityRequirement(name = "bearer-jwt")
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Audit log retrieved successfully",
          content = @Content(schema = @Schema(implementation = ApiResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "404",
          description = "Not Found - audit log with specified ID does not exist"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Unauthorized - authentication required"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "403",
          description = "Forbidden - requires ADMIN or COMPLIANCE_OFFICER role"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  public ResponseEntity<ApiResponse<AuditLog>> getAuditLog(
      @PathVariable UUID auditId) {

    logger.info("GET /api/v1/audit/{} - Retrieving audit log", auditId);

    try {
      AuditLog auditLog = auditLogRepository.findById(auditId)
          .orElseThrow(() -> new RuntimeException("Audit log not found: " + auditId));

      // Build metadata
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("timestamp", LocalDateTime.now());
      metadata.put("version", "v1");
      metadata.put("operation", "READ_AUDIT");

      // Create response envelope
      ApiResponse<AuditLog> apiResponse = new ApiResponse<>(auditLog, metadata);

      logger.info("Audit log retrieved successfully: {}", auditId);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(apiResponse);

    } catch (Exception ex) {
      logger.error("Error retrieving audit log {}: {}", auditId, ex.getMessage(), ex);
      throw ex; // Let GlobalExceptionHandler handle it
    }
  }
}
