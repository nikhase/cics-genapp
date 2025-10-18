package com.genapp.service;

import com.genapp.dto.CustomerDTO;
import com.genapp.model.Customer;
import com.genapp.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * CustomerServiceTest - Unit tests for CustomerService
 *
 * Tests business logic without hitting the database
 * Uses Mockito for repository mocking
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerDTO testCustomerDTO;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Create test DTO
        testCustomerDTO = new CustomerDTO(
                null,
                "John",
                "Doe",
                "123 Main St",
                "New York",
                "NY",
                "10001",
                "(555) 123-4567",
                "john.doe@example.com"
        );

        // Create test entity
        testCustomer = new Customer();
        testCustomer.setCustomerId(1001L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setAddress("123 Main St");
        testCustomer.setCity("New York");
        testCustomer.setState("NY");
        testCustomer.setZipCode("10001");
        testCustomer.setPhone("(555) 123-4567");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create customer successfully")
    void testCreateCustomerSuccess() {
        // Arrange
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerDTO result = customerService.createCustomer(testCustomerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1001L, result.customerId());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("john.doe@example.com", result.email());
        verify(customerRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateCustomerEmailAlreadyExists() {
        // Arrange
        when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.createCustomer(testCustomerDTO)
        );
        assertEquals("Customer with email 'john.doe@example.com' already exists", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void testGetCustomerSuccess() {
        // Arrange
        when(customerRepository.findById(1001L)).thenReturn(Optional.of(testCustomer));

        // Act
        CustomerDTO result = customerService.getCustomer(1001L);

        // Assert
        assertNotNull(result);
        assertEquals(1001L, result.customerId());
        assertEquals("John", result.firstName());
        verify(customerRepository, times(1)).findById(1001L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void testGetCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.getCustomer(9999L)
        );
        assertEquals("Customer not found with ID: 9999", exception.getMessage());
        verify(customerRepository, times(1)).findById(9999L);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomerSuccess() {
        // Arrange
        CustomerDTO updateDTO = new CustomerDTO(
                null,
                "Jonathan",
                "Doe",
                "456 Oak Ave",
                "Boston",
                "MA",
                "02101",
                "(555) 987-6543",
                "jonathan.doe@example.com"
        );
        when(customerRepository.findById(1001L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("jonathan.doe@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerDTO result = customerService.updateCustomer(1001L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1001L, result.customerId());
        verify(customerRepository, times(1)).findById(1001L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void testUpdateCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.updateCustomer(9999L, testCustomerDTO)
        );
        assertEquals("Customer not found with ID: 9999", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomerSuccess() {
        // Arrange
        when(customerRepository.existsById(1001L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> customerService.deleteCustomer(1001L));

        // Assert
        verify(customerRepository, times(1)).deleteById(1001L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent customer")
    void testDeleteCustomerNotFound() {
        // Arrange
        when(customerRepository.existsById(9999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.deleteCustomer(9999L)
        );
        assertEquals("Customer not found with ID: 9999", exception.getMessage());
        verify(customerRepository, never()).deleteById(9999L);
    }

    @Test
    @DisplayName("Should search customers by full name pattern")
    void testSearchByFullName() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setCustomerId(1002L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Doe");
        customer2.setEmail("jane.doe@example.com");

        List<Customer> searchResults = Arrays.asList(testCustomer, customer2);
        when(customerRepository.searchByFullName("Doe")).thenReturn(searchResults);

        // Act
        List<CustomerDTO> results = customerService.searchByFullName("Doe");

        // Assert
        assertEquals(2, results.size());
        assertEquals("John", results.get(0).firstName());
        assertEquals("Jane", results.get(1).firstName());
        verify(customerRepository, times(1)).searchByFullName("Doe");
    }

    @Test
    @DisplayName("Should get all customers")
    void testGetAllCustomers() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setCustomerId(1002L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane.smith@example.com");

        List<Customer> allCustomers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(allCustomers);

        // Act
        List<CustomerDTO> results = customerService.getAllCustomers();

        // Assert
        assertEquals(2, results.size());
        verify(customerRepository, times(1)).findAll();
    }
}
