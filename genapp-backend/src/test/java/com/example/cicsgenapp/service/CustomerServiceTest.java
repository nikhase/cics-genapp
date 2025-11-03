package com.example.cicsgenapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Operation;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.exception.CustomerAlreadyExistsException;
import com.example.cicsgenapp.repository.CustomerRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for CustomerService.
 *
 * <p>Tests business logic for customer creation including validation, persistence,
 * and audit trail generation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Unit Tests")
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private AuditService auditService;

  private CustomerService customerService;

  @BeforeEach
  void setUp() {
    customerService = new CustomerService(customerRepository, auditService);
  }

  @Test
  @DisplayName("createCustomer - Successfully creates customer with valid request")
  void testCreateCustomerSuccess() {
    // Given: valid customer request
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );
    request.setPhone("+12025551234");
    request.setDateOfBirth(LocalDate.of(1990, 1, 15));

    // Mock: email doesn't exist and save succeeds
    UUID customerId = UUID.randomUUID();
    when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

    Customer savedCustomer = new Customer("John", "Doe", "john@example.com");
    savedCustomer.setCustomerId(customerId);
    savedCustomer.setPhone("+12025551234");
    savedCustomer.setDateOfBirth(LocalDate.of(1990, 1, 15));
    savedCustomer.setStatus(Status.ACTIVE);
    savedCustomer.setCreatedAt(LocalDateTime.now());
    savedCustomer.setUpdatedAt(LocalDateTime.now());

    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    // When: create customer
    CustomerResponse response = customerService.createCustomer(request);

    // Then: verify response and interactions
    assertNotNull(response);
    assertEquals("John", response.getFirstName());
    assertEquals("Doe", response.getLastName());
    assertEquals("john@example.com", response.getEmail());
    assertEquals(Status.ACTIVE, response.getStatus());

    // Verify repository was called
    verify(customerRepository, times(1)).findByEmail("john@example.com");
    verify(customerRepository, times(1)).save(any(Customer.class));

    // Verify audit entry was created
    verify(auditService, times(1)).createAuditEntry(
        any(Operation.class),
        any(String.class),
        any(UUID.class),
        any(Customer.class),
        any(String.class)
    );
  }

  @Test
  @DisplayName("createCustomer - Throws CustomerAlreadyExistsException for duplicate email")
  void testCreateCustomerDuplicateEmail() {
    // Given: request with email that already exists
    CreateCustomerRequest request = new CreateCustomerRequest(
        "Jane",
        "Smith",
        "existing@example.com"
    );

    // Mock: email already exists
    Customer existingCustomer = new Customer("John", "Doe", "existing@example.com");
    when(customerRepository.findByEmail("existing@example.com"))
        .thenReturn(Optional.of(existingCustomer));

    // When/Then: exception is thrown
    assertThrows(
        CustomerAlreadyExistsException.class,
        () -> customerService.createCustomer(request),
        "Should throw CustomerAlreadyExistsException for duplicate email"
    );

    // Verify repository was called for email check only
    verify(customerRepository, times(1)).findByEmail("existing@example.com");
    verify(customerRepository, times(0)).save(any(Customer.class));
    verify(auditService, times(0)).createAuditEntry(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("createCustomer - Sets default status to ACTIVE")
  void testCreateCustomerDefaultStatusActive() {
    // Given: request without explicit status
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );

    // Mock setup
    when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

    Customer savedCustomer = new Customer("John", "Doe", "john@example.com");
    savedCustomer.setCustomerId(UUID.randomUUID());
    savedCustomer.setStatus(Status.ACTIVE);
    savedCustomer.setCreatedAt(LocalDateTime.now());
    savedCustomer.setUpdatedAt(LocalDateTime.now());

    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    // When: create customer
    CustomerResponse response = customerService.createCustomer(request);

    // Then: verify status is ACTIVE
    assertEquals(Status.ACTIVE, response.getStatus());

    // Verify saved customer had status ACTIVE set
    ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
    verify(customerRepository).save(customerCaptor.capture());
    assertEquals(Status.ACTIVE, customerCaptor.getValue().getStatus());
  }

  @Test
  @DisplayName("createCustomer - Maps all request fields to entity")
  void testCreateCustomerFieldMapping() {
    // Given: request with all fields
    CreateCustomerRequest request = new CreateCustomerRequest(
        "Jane",
        "Smith",
        "jane@example.com"
    );
    request.setDateOfBirth(LocalDate.of(1985, 5, 20));
    request.setPhone("+447911123456");
    request.setAddress("123 Main St");
    request.setCity("New York");
    request.setState("NY");
    request.setZipCode("10001");

    // Mock setup
    when(customerRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

    Customer savedCustomer = new Customer("Jane", "Smith", "jane@example.com");
    savedCustomer.setCustomerId(UUID.randomUUID());
    savedCustomer.setDateOfBirth(LocalDate.of(1985, 5, 20));
    savedCustomer.setPhone("+447911123456");
    savedCustomer.setAddress("123 Main St");
    savedCustomer.setCity("New York");
    savedCustomer.setState("NY");
    savedCustomer.setZipCode("10001");
    savedCustomer.setStatus(Status.ACTIVE);
    savedCustomer.setCreatedAt(LocalDateTime.now());
    savedCustomer.setUpdatedAt(LocalDateTime.now());

    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    // When: create customer
    CustomerResponse response = customerService.createCustomer(request);

    // Then: verify all fields are mapped
    ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
    verify(customerRepository).save(customerCaptor.capture());

    Customer savedEntity = customerCaptor.getValue();
    assertEquals("Jane", savedEntity.getFirstName());
    assertEquals("Smith", savedEntity.getLastName());
    assertEquals("jane@example.com", savedEntity.getEmail());
    assertEquals(LocalDate.of(1985, 5, 20), savedEntity.getDateOfBirth());
    assertEquals("+447911123456", savedEntity.getPhone());
    assertEquals("123 Main St", savedEntity.getAddress());
    assertEquals("New York", savedEntity.getCity());
    assertEquals("NY", savedEntity.getState());
    assertEquals("10001", savedEntity.getZipCode());
  }

  @Test
  @DisplayName("createCustomer - Audit entry is created with CREATE operation")
  void testCreateCustomerAuditEntry() {
    // Given: valid request
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );

    // Mock setup
    when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

    UUID customerId = UUID.randomUUID();
    Customer savedCustomer = new Customer("John", "Doe", "john@example.com");
    savedCustomer.setCustomerId(customerId);
    savedCustomer.setStatus(Status.ACTIVE);
    savedCustomer.setCreatedAt(LocalDateTime.now());
    savedCustomer.setUpdatedAt(LocalDateTime.now());

    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    // When: create customer
    customerService.createCustomer(request);

    // Then: verify audit entry was created
    ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
    ArgumentCaptor<String> entityTypeCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<UUID> entityIdCaptor = ArgumentCaptor.forClass(UUID.class);

    verify(auditService).createAuditEntry(
        operationCaptor.capture(),
        entityTypeCaptor.capture(),
        entityIdCaptor.capture(),
        any(Customer.class),
        any(String.class)
    );

    assertEquals(Operation.CREATE, operationCaptor.getValue());
    assertEquals("CUSTOMER", entityTypeCaptor.getValue());
    assertEquals(customerId, entityIdCaptor.getValue());
  }

  @Test
  @DisplayName("getCustomerById - Returns customer response when found")
  void testGetCustomerByIdFound() {
    // Given: customer ID
    UUID customerId = UUID.randomUUID();
    Customer customer = new Customer("John", "Doe", "john@example.com");
    customer.setCustomerId(customerId);
    customer.setStatus(Status.ACTIVE);

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

    // When: get customer
    CustomerResponse response = customerService.getCustomerById(customerId);

    // Then: verify response
    assertNotNull(response);
    assertEquals("John", response.getFirstName());
    assertEquals(customerId, response.getCustomerId());
  }

  @Test
  @DisplayName("getCustomerByEmail - Returns customer response when found")
  void testGetCustomerByEmailFound() {
    // Given: email address
    Customer customer = new Customer("John", "Doe", "john@example.com");
    customer.setCustomerId(UUID.randomUUID());

    when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customer));

    // When: get customer
    CustomerResponse response = customerService.getCustomerByEmail("john@example.com");

    // Then: verify response
    assertNotNull(response);
    assertEquals("john@example.com", response.getEmail());
  }

  @Test
  @DisplayName("customerExistsByEmail - Returns true when customer exists")
  void testCustomerExistsByEmailTrue() {
    // Given: email that exists
    when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);

    // When/Then: check exists
    assertEquals(true, customerService.customerExistsByEmail("john@example.com"));
  }

  @Test
  @DisplayName("customerExistsByEmail - Returns false when customer doesn't exist")
  void testCustomerExistsByEmailFalse() {
    // Given: email that doesn't exist
    when(customerRepository.existsByEmail("notfound@example.com")).thenReturn(false);

    // When/Then: check exists
    assertEquals(false, customerService.customerExistsByEmail("notfound@example.com"));
  }
}
