package com.example.cicsgenapp.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.cicsgenapp.entity.Customer;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for AuditListener.
 * Tests automatic population of audit columns (createdAt, updatedAt, createdBy, updatedBy)
 * during entity lifecycle events (@PrePersist, @PreUpdate).
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(AuditListener.class)
class AuditListenerTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AuditListener auditListener;

  private Customer customer;

  @BeforeEach
  void setUp() {
    customer = new Customer("John", "Doe", "john@example.com");
    SecurityContextHolder.clearContext();
  }

  // ========== PrePersist Tests ==========

  @Test
  @WithMockUser(username = "testuser")
  void testPrePersistSetsCreatedAtAndUpdatedAt() {
    auditListener.prePersist(customer);

    assertNotNull(customer.getCreatedAt());
    assertNotNull(customer.getUpdatedAt());
    assertEquals(customer.getCreatedAt(), customer.getUpdatedAt());
  }

  @Test
  @WithMockUser(username = "testuser")
  void testPrePersistSetsCreatedByAndUpdatedBy() {
    auditListener.prePersist(customer);

    assertEquals("testuser", customer.getCreatedBy());
    assertEquals("testuser", customer.getUpdatedBy());
  }

  @Test
  @WithMockUser(username = "admin")
  void testPrePersistSetsCreatedByFromSecurityContext() {
    auditListener.prePersist(customer);

    assertEquals("admin", customer.getCreatedBy());
  }

  @Test
  void testPrePersistSetsCreatedByToSystemWhenNoAuthentication() {
    SecurityContextHolder.clearContext();
    auditListener.prePersist(customer);

    assertEquals("system", customer.getCreatedBy());
    assertEquals("system", customer.getUpdatedBy());
  }

  @Test
  @WithMockUser(username = "testuser")
  void testPrePersistTimestampsAreCloseToNow() {
    LocalDateTime beforeCall = LocalDateTime.now();
    auditListener.prePersist(customer);
    LocalDateTime afterCall = LocalDateTime.now();

    assertTrue(customer.getCreatedAt().isAfter(beforeCall) ||
        customer.getCreatedAt().isEqual(beforeCall));
    assertTrue(customer.getCreatedAt().isBefore(afterCall) ||
        customer.getCreatedAt().isEqual(afterCall));
  }

  // ========== PreUpdate Tests ==========

  @Test
  @WithMockUser(username = "testuser")
  void testPreUpdateUpdatesUpdatedAt() {
    LocalDateTime originalCreatedAt = LocalDateTime.now().minusHours(1);
    customer.setCreatedAt(originalCreatedAt);
    customer.setUpdatedAt(originalCreatedAt);

    // Add a small delay to ensure updatedAt will be different
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    auditListener.preUpdate(customer);

    assertEquals(originalCreatedAt, customer.getCreatedAt());
    assertTrue(customer.getUpdatedAt().isAfter(originalCreatedAt));
  }

  @Test
  @WithMockUser(username = "newuser")
  void testPreUpdateUpdatesUpdatedBy() {
    customer.setCreatedBy("originaluser");
    customer.setUpdatedBy("originaluser");
    customer.setCreatedAt(LocalDateTime.now().minusHours(1));
    customer.setUpdatedAt(LocalDateTime.now().minusHours(1));

    auditListener.preUpdate(customer);

    assertEquals("originaluser", customer.getCreatedBy());
    assertEquals("newuser", customer.getUpdatedBy());
  }

  @Test
  void testPreUpdateSetsUpdatedByToSystemWhenNoAuthentication() {
    customer.setCreatedBy("originaluser");
    customer.setUpdatedBy("originaluser");
    customer.setCreatedAt(LocalDateTime.now().minusHours(1));
    customer.setUpdatedAt(LocalDateTime.now().minusHours(1));

    SecurityContextHolder.clearContext();
    auditListener.preUpdate(customer);

    assertEquals("originaluser", customer.getCreatedBy());
    assertEquals("system", customer.getUpdatedBy());
  }

  @Test
  @WithMockUser(username = "testuser")
  void testPreUpdateDoesNotModifyCreatedAt() {
    LocalDateTime originalCreatedAt = LocalDateTime.now().minusHours(1);
    customer.setCreatedAt(originalCreatedAt);
    customer.setUpdatedAt(originalCreatedAt);

    auditListener.preUpdate(customer);

    assertEquals(originalCreatedAt, customer.getCreatedAt());
  }

  @Test
  @WithMockUser(username = "testuser")
  void testPreUpdateDoesNotModifyCreatedBy() {
    customer.setCreatedBy("originaluser");
    customer.setUpdatedBy("originaluser");

    auditListener.preUpdate(customer);

    assertEquals("originaluser", customer.getCreatedBy());
  }

  // ========== Integration Tests ==========

  @Test
  @WithMockUser(username = "testuser")
  void testCustomerAuditColumnsPopulatedOnPersist() {
    auditListener.prePersist(customer);
    entityManager.persistAndFlush(customer);
    entityManager.clear();

    Customer retrieved = entityManager.find(Customer.class, customer.getCustomerId());
    assertNotNull(retrieved.getCreatedAt());
    assertNotNull(retrieved.getUpdatedAt());
    assertEquals("testuser", retrieved.getCreatedBy());
    assertEquals("testuser", retrieved.getUpdatedBy());
  }

  @Test
  @WithMockUser(username = "user1")
  void testCustomerCreatedByAndUpdatedByTrackingAcrossUpdates() {
    auditListener.prePersist(customer);
    entityManager.persistAndFlush(customer);

    LocalDateTime originalUpdatedAt = customer.getUpdatedAt();
    LocalDateTime originalCreatedAt = customer.getCreatedAt();

    // Simulate a second user updating the record
    customer.setFirstName("Jane");

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Switch security context to different user
    SecurityContextHolder.clearContext();
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken("user2", null);
    SecurityContextHolder.getContext().setAuthentication(token);

    auditListener.preUpdate(customer);
    entityManager.flush();
    entityManager.clear();

    Customer retrieved = entityManager.find(Customer.class, customer.getCustomerId());
    assertEquals("user1", retrieved.getCreatedBy());
    assertEquals("user2", retrieved.getUpdatedBy());
    assertEquals(originalCreatedAt, retrieved.getCreatedAt());
    assertTrue(retrieved.getUpdatedAt().isAfter(originalUpdatedAt));
  }
}
