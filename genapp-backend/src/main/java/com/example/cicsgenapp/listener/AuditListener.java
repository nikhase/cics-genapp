package com.example.cicsgenapp.listener;

import com.example.cicsgenapp.entity.Customer;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * JPA Entity Listener for managing audit columns on Customer entity.
 * Automatically sets createdAt, updatedAt, createdBy, and updatedBy fields
 * when entities are persisted or updated.
 *
 * <p>Uses Spring Security context to populate createdBy/updatedBy from JWT token
 * or authenticated principal name.</p>
 */
@Component
public class AuditListener {

  /**
   * Called before a new entity is persisted.
   * Sets createdAt, updatedAt to current time and createdBy/updatedBy from security context.
   *
   * @param customer the customer entity about to be persisted
   */
  @PrePersist
  public void prePersist(Customer customer) {
    LocalDateTime now = LocalDateTime.now();
    customer.setCreatedAt(now);
    customer.setUpdatedAt(now);

    String principalName = getPrincipalName();
    customer.setCreatedBy(principalName);
    customer.setUpdatedBy(principalName);
  }

  /**
   * Called before an entity is updated.
   * Updates updatedAt to current time and updatedBy from security context.
   * Does NOT modify createdAt or createdBy (audit trail integrity).
   *
   * @param customer the customer entity about to be updated
   */
  @PreUpdate
  public void preUpdate(Customer customer) {
    customer.setUpdatedAt(LocalDateTime.now());

    String principalName = getPrincipalName();
    customer.setUpdatedBy(principalName);
  }

  /**
   * Gets the current principal (authenticated user) name from SecurityContext.
   * Falls back to "system" if no authentication is available.
   *
   * @return the name of the authenticated principal, or "system" if none
   */
  private String getPrincipalName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return authentication.getName();
    }
    return "system";
  }
}
