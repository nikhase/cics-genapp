package com.example.cicsgenapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.cicsgenapp.config.TestcontainersConfiguration;
import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for CustomerRepository.
 * Tests repository query methods and database interactions.
 * Uses PostgreSQL via Testcontainers for testing PostgreSQL-specific features.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CustomerRepositoryIntegrationTest {

  @Autowired
  private CustomerRepository customerRepository;

  private Customer testCustomer;

  @BeforeEach
  void setUp() {
    customerRepository.deleteAll();

    testCustomer = new Customer("John", "Doe", "john@example.com");
    testCustomer.setPhone("+1-555-0001");
    testCustomer.setStatus(Status.ACTIVE);
    testCustomer.setCreatedAt(LocalDateTime.now());
    testCustomer.setUpdatedAt(LocalDateTime.now());
    testCustomer.setCreatedBy("testuser");
  }

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll();
  }

  // ========== Basic CRUD Tests ==========

  @Test
  void testSaveCustomer() {
    Customer saved = customerRepository.save(testCustomer);
    assertNotNull(saved.getCustomerId());
    assertEquals("John", saved.getFirstName());
    assertEquals("john@example.com", saved.getEmail());
  }

  @Test
  void testFindCustomerById() {
    Customer saved = customerRepository.save(testCustomer);
    Optional<Customer> found = customerRepository.findById(saved.getCustomerId());

    assertTrue(found.isPresent());
    assertEquals(saved.getCustomerId(), found.get().getCustomerId());
    assertEquals("John", found.get().getFirstName());
  }

  @Test
  void testUpdateCustomer() {
    Customer saved = customerRepository.save(testCustomer);
    saved.setFirstName("Jane");
    saved.setUpdatedAt(LocalDateTime.now());
    Customer updated = customerRepository.save(saved);

    assertEquals("Jane", updated.getFirstName());
    assertEquals(saved.getCustomerId(), updated.getCustomerId());
  }

  @Test
  void testDeleteCustomer() {
    Customer saved = customerRepository.save(testCustomer);
    customerRepository.delete(saved);

    Optional<Customer> found = customerRepository.findById(saved.getCustomerId());
    assertFalse(found.isPresent());
  }

  // ========== findByEmail Tests ==========

  @Test
  void testFindByEmail() {
    customerRepository.save(testCustomer);

    Optional<Customer> found = customerRepository.findByEmail("john@example.com");
    assertTrue(found.isPresent());
    assertEquals("John", found.get().getFirstName());
  }

  @Test
  void testFindByEmailNotFound() {
    Optional<Customer> found = customerRepository.findByEmail("nonexistent@example.com");
    assertFalse(found.isPresent());
  }

  @Test
  void testFindByEmailWithMultipleCustomers() {
    customerRepository.save(testCustomer);

    Customer customer2 = new Customer("Jane", "Smith", "jane@example.com");
    customer2.setCreatedAt(LocalDateTime.now());
    customer2.setUpdatedAt(LocalDateTime.now());
    customer2.setCreatedBy("testuser");
    customerRepository.save(customer2);

    Optional<Customer> foundJane = customerRepository.findByEmail("jane@example.com");
    Optional<Customer> foundJohn = customerRepository.findByEmail("john@example.com");

    assertTrue(foundJane.isPresent());
    assertTrue(foundJohn.isPresent());
    assertEquals("Jane", foundJane.get().getFirstName());
    assertEquals("John", foundJohn.get().getFirstName());
  }

  // ========== findByPhone Tests ==========

  @Test
  void testFindByPhone() {
    customerRepository.save(testCustomer);

    Optional<Customer> found = customerRepository.findByPhone("+1-555-0001");
    assertTrue(found.isPresent());
    assertEquals("John", found.get().getFirstName());
  }

  @Test
  void testFindByPhoneNotFound() {
    Optional<Customer> found = customerRepository.findByPhone("+1-555-9999");
    assertFalse(found.isPresent());
  }

  // ========== findByLastNameContainingIgnoreCase Tests ==========

  @Test
  void testFindByLastNameContainingIgnoreCase() {
    customerRepository.save(testCustomer);

    Customer customer2 = new Customer("Jane", "Doe", "jane@example.com");
    customer2.setCreatedAt(LocalDateTime.now());
    customer2.setUpdatedAt(LocalDateTime.now());
    customer2.setCreatedBy("testuser");
    customerRepository.save(customer2);

    List<Customer> results = customerRepository.findByLastNameContainingIgnoreCase("do");
    assertEquals(2, results.size());
  }

  @Test
  void testFindByLastNameContainingIgnoreCasePartialMatch() {
    customerRepository.save(testCustomer);

    Customer customer2 = new Customer("Bob", "Smith", "bob@example.com");
    customer2.setCreatedAt(LocalDateTime.now());
    customer2.setUpdatedAt(LocalDateTime.now());
    customer2.setCreatedBy("testuser");
    customerRepository.save(customer2);

    List<Customer> results = customerRepository.findByLastNameContainingIgnoreCase("smith");
    assertEquals(1, results.size());
    assertEquals("Bob", results.get(0).getFirstName());
  }

  @Test
  void testFindByLastNameContainingIgnoreCaseNotFound() {
    customerRepository.save(testCustomer);

    List<Customer> results = customerRepository.findByLastNameContainingIgnoreCase("NonExistent");
    assertTrue(results.isEmpty());
  }

  // ========== findByStatusAndCreatedAtAfter Tests ==========

  @Test
  void testFindByStatusAndCreatedAtAfter() {
    LocalDateTime before = LocalDateTime.now().minusHours(1);
    testCustomer.setCreatedAt(before.plusMinutes(30));
    testCustomer.setUpdatedAt(before.plusMinutes(30));
    customerRepository.save(testCustomer);

    List<Customer> results = customerRepository.findByStatusAndCreatedAtAfter(Status.ACTIVE,
        before);
    assertEquals(1, results.size());
    assertEquals("John", results.get(0).getFirstName());
  }

  @Test
  void testFindByStatusAndCreatedAtAfterExcludesOldRecords() {
    LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
    testCustomer.setCreatedAt(twoHoursAgo);
    testCustomer.setUpdatedAt(twoHoursAgo);
    customerRepository.save(testCustomer);

    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
    List<Customer> results = customerRepository.findByStatusAndCreatedAtAfter(Status.ACTIVE,
        oneHourAgo);
    assertTrue(results.isEmpty());
  }

  @Test
  void testFindByStatusAndCreatedAtAfterWithStatusFilter() {
    testCustomer.setStatus(Status.INACTIVE);
    testCustomer.setCreatedAt(LocalDateTime.now());
    testCustomer.setUpdatedAt(LocalDateTime.now());
    customerRepository.save(testCustomer);

    LocalDateTime before = LocalDateTime.now().minusHours(1);
    List<Customer> activeResults = customerRepository.findByStatusAndCreatedAtAfter(Status.ACTIVE,
        before);
    List<Customer> inactiveResults = customerRepository.findByStatusAndCreatedAtAfter(
        Status.INACTIVE, before);

    assertTrue(activeResults.isEmpty());
    assertEquals(1, inactiveResults.size());
  }

  // ========== findByStatus Tests ==========

  @Test
  void testFindByStatus() {
    customerRepository.save(testCustomer);

    Customer inactiveCustomer = new Customer("Jane", "Smith", "jane@example.com");
    inactiveCustomer.setStatus(Status.INACTIVE);
    inactiveCustomer.setCreatedAt(LocalDateTime.now());
    inactiveCustomer.setUpdatedAt(LocalDateTime.now());
    inactiveCustomer.setCreatedBy("testuser");
    customerRepository.save(inactiveCustomer);

    List<Customer> activeCustomers = customerRepository.findByStatus(Status.ACTIVE);
    assertEquals(1, activeCustomers.size());
    assertEquals("John", activeCustomers.get(0).getFirstName());
  }

  // ========== countByStatus Tests ==========

  @Test
  void testCountByStatus() {
    customerRepository.save(testCustomer);

    Customer customer2 = new Customer("Jane", "Smith", "jane@example.com");
    customer2.setStatus(Status.ACTIVE);
    customer2.setCreatedAt(LocalDateTime.now());
    customer2.setUpdatedAt(LocalDateTime.now());
    customer2.setCreatedBy("testuser");
    customerRepository.save(customer2);

    long count = customerRepository.countByStatus(Status.ACTIVE);
    assertEquals(2, count);
  }

  // ========== existsByEmail Tests ==========

  @Test
  void testExistsByEmail() {
    customerRepository.save(testCustomer);
    assertTrue(customerRepository.existsByEmail("john@example.com"));
    assertFalse(customerRepository.existsByEmail("nonexistent@example.com"));
  }

  // ========== existsByPhone Tests ==========

  @Test
  void testExistsByPhone() {
    customerRepository.save(testCustomer);
    assertTrue(customerRepository.existsByPhone("+1-555-0001"));
    assertFalse(customerRepository.existsByPhone("+1-555-9999"));
  }
}
