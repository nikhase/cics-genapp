package com.example.cicsgenapp.service;

import com.example.cicsgenapp.entity.AuditLog;
import com.example.cicsgenapp.entity.Operation;
import com.example.cicsgenapp.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for creating and managing audit log entries.
 *
 * <p>Automatically captures user context from Spring Security, IP address from HTTP request,
 * and serializes entity changes to JSON for audit trail compliance.
 */
@Service
public class AuditService {

  private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

  private final AuditLogRepository auditLogRepository;
  private final HttpServletRequest httpServletRequest;
  private final ObjectMapper objectMapper;

  /**
   * Constructs AuditService with required dependencies.
   *
   * @param auditLogRepository repository for persisting audit logs
   * @param httpServletRequest HTTP request context (may be null in non-web contexts)
   * @param objectMapper Jackson ObjectMapper for JSON serialization
   */
  public AuditService(
      AuditLogRepository auditLogRepository,
      HttpServletRequest httpServletRequest,
      ObjectMapper objectMapper) {
    this.auditLogRepository = auditLogRepository;
    this.httpServletRequest = httpServletRequest;
    this.objectMapper = objectMapper;
  }

  /**
   * Creates an audit log entry for a data mutation operation.
   *
   * @param operation the type of operation (CREATE, UPDATE, DELETE, READ)
   * @param entityType the entity type being modified (e.g., "CUSTOMER")
   * @param entityId the ID of the entity
   * @param entity the entity object (will be serialized to JSON)
   */
  public void createAuditEntry(Operation operation, String entityType, UUID entityId,
      Object entity) {
    try {
      AuditLog auditLog = new AuditLog();
      auditLog.setOperation(operation);
      auditLog.setEntityType(entityType);
      auditLog.setEntityId(entityId);
      auditLog.setUserId(extractUserId());
      auditLog.setIpAddress(extractIpAddress());
      auditLog.setUserAgent(extractUserAgent());

      // Serialize entity to JSON
      String changes = objectMapper.writeValueAsString(entity);
      auditLog.setChanges(changes);

      auditLogRepository.save(auditLog);
      logger.debug("Audit entry created: {} {} for {}", operation, entityType, entityId);
    } catch (Exception ex) {
      logger.error("Failed to create audit entry for {} {}", entityType, entityId, ex);
      // Don't throw - audit failures shouldn't block business operations
    }
  }

  /**
   * Creates an audit log entry with additional change details.
   *
   * @param operation the type of operation
   * @param entityType the entity type
   * @param entityId the entity ID
   * @param entity the entity object
   * @param changesSummary additional change description
   */
  public void createAuditEntry(Operation operation, String entityType, UUID entityId,
      Object entity, String changesSummary) {
    try {
      AuditLog auditLog = new AuditLog();
      auditLog.setOperation(operation);
      auditLog.setEntityType(entityType);
      auditLog.setEntityId(entityId);
      auditLog.setUserId(extractUserId());
      auditLog.setIpAddress(extractIpAddress());
      auditLog.setUserAgent(extractUserAgent());

      // Combine entity and summary
      String changes = objectMapper.writeValueAsString(entity);
      auditLog.setChanges(changes);

      auditLogRepository.save(auditLog);
      logger.debug("Audit entry created: {} {} for {} - {}", operation, entityType, entityId,
          changesSummary);
    } catch (Exception ex) {
      logger.error("Failed to create audit entry for {} {}", entityType, entityId, ex);
    }
  }

  /**
   * Extracts user ID from Spring Security context.
   *
   * @return user ID (principal name) or "SYSTEM" if not authenticated
   */
  private String extractUserId() {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (principal != null) {
        return principal.toString();
      }
    } catch (Exception ex) {
      logger.debug("Could not extract user ID from security context", ex);
    }
    return "SYSTEM";
  }

  /**
   * Extracts client IP address from HTTP request.
   *
   * @return client IP address or unknown if unavailable
   */
  private String extractIpAddress() {
    if (httpServletRequest == null) {
      return "unknown";
    }

    String ip = httpServletRequest.getHeader("X-Forwarded-For");
    if (ip != null && !ip.isEmpty()) {
      return ip.split(",")[0]; // Get first IP if there are multiple
    }

    ip = httpServletRequest.getHeader("X-Real-IP");
    if (ip != null && !ip.isEmpty()) {
      return ip;
    }

    return httpServletRequest.getRemoteAddr();
  }

  /**
   * Extracts User-Agent header from HTTP request.
   *
   * @return User-Agent header value or unknown if unavailable
   */
  private String extractUserAgent() {
    if (httpServletRequest == null) {
      return "unknown";
    }
    String userAgent = httpServletRequest.getHeader("User-Agent");
    return userAgent != null ? userAgent : "unknown";
  }
}
