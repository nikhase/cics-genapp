package com.example.cicsgenapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit log entity for tracking all data mutations in the system.
 *
 * <p>Records every CREATE, UPDATE, DELETE operation with timestamp, user, entity type,
 * and detailed change information for compliance and debugging purposes.
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "audit_id")
  private UUID auditId;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @Column(name = "user_id", length = 255)
  private String userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "operation", length = 20, nullable = false)
  private Operation operation;

  @Column(name = "entity_type", length = 100, nullable = false)
  private String entityType;

  @Column(name = "entity_id")
  private UUID entityId;

  @Column(name = "changes", columnDefinition = "jsonb")
  private String changes;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  /**
   * Default constructor for JPA.
   */
  public AuditLog() {
  }

  /**
   * Constructor with minimal required fields.
   */
  public AuditLog(Operation operation, String entityType, UUID entityId, String userId) {
    this.operation = operation;
    this.entityType = entityType;
    this.entityId = entityId;
    this.userId = userId;
    this.timestamp = LocalDateTime.now();
  }

  // Getters and Setters

  public UUID getAuditId() {
    return auditId;
  }

  public void setAuditId(UUID auditId) {
    this.auditId = auditId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public UUID getEntityId() {
    return entityId;
  }

  public void setEntityId(UUID entityId) {
    this.entityId = entityId;
  }

  public String getChanges() {
    return changes;
  }

  public void setChanges(String changes) {
    this.changes = changes;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuditLog auditLog = (AuditLog) o;
    return Objects.equals(auditId, auditLog.auditId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auditId);
  }

  @Override
  public String toString() {
    return "AuditLog{"
        + "auditId=" + auditId
        + ", timestamp=" + timestamp
        + ", userId='" + userId + '\''
        + ", operation=" + operation
        + ", entityType='" + entityType + '\''
        + ", entityId=" + entityId
        + '}';
  }
}
