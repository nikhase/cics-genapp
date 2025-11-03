package com.example.cicsgenapp.repository;

import com.example.cicsgenapp.entity.AuditLog;
import com.example.cicsgenapp.entity.Operation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for AuditLog entity.
 * Provides data access operations for audit trail records.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

  /**
   * Find all audit logs for a specific entity.
   *
   * @param entityType the entity type (e.g., "CUSTOMER")
   * @param entityId the entity ID
   * @return list of audit logs for this entity
   */
  List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);

  /**
   * Find all audit logs of a specific operation type.
   *
   * @param operation the operation type (CREATE, UPDATE, DELETE, READ)
   * @return list of audit logs for this operation
   */
  List<AuditLog> findByOperation(Operation operation);

  /**
   * Find audit logs created within a time range.
   *
   * @param startTime the start of the time range
   * @param endTime the end of the time range
   * @return list of audit logs within the time range
   */
  List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Find all audit logs for a specific user.
   *
   * @param userId the user ID
   * @return list of audit logs for this user
   */
  List<AuditLog> findByUserId(String userId);
}
