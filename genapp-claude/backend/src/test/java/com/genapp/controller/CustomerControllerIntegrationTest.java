package com.genapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genapp.dto.CustomerDTO;
import com.genapp.model.Customer;
import com.genapp.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CustomerControllerIntegrationTest - Integration tests with real PostgreSQL
 *
 * Uses TestContainers to spin up a real PostgreSQL instance for testing
 * Tests full Spring Boot context and REST API endpoints
 *
 * Old COBOL equivalent: Manual testing via 3270 terminal
 * Modern approach: Automated integration tests with real database
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("CustomerController Integration Tests with PostgreSQL")
class CustomerControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("genapp_test")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerDTO testCustomerDTO;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

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
    }

    @Test
    @DisplayName("POST /api/customers - Create customer successfully")
    void testCreateCustomer() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.customerId").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/customers - Reject duplicate email")
    void testCreateCustomerDuplicateEmail() throws Exception {
        // Arrange - Create first customer
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated());

        // Act & Assert - Try to create with same email
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    @DisplayName("POST /api/customers - Reject invalid email format")
    void testCreateCustomerInvalidEmail() throws Exception {
        // Arrange
        CustomerDTO invalidDTO = new CustomerDTO(
                null,
                "John",
                "Doe",
                null,
                null,
                null,
                null,
                null,
                "invalid-email"
        );

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Get customer successfully")
    void testGetCustomer() throws Exception {
        // Arrange - Create a customer
        String createResponse = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerDTO createdCustomer = objectMapper.readValue(createResponse, CustomerDTO.class);
        Long customerId = createdCustomer.customerId();

        // Act & Assert
        mockMvc.perform(get("/api/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Return 404 for non-existent customer")
    void testGetCustomerNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers/{customerId}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - Update customer successfully")
    void testUpdateCustomer() throws Exception {
        // Arrange - Create a customer
        String createResponse = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerDTO createdCustomer = objectMapper.readValue(createResponse, CustomerDTO.class);
        Long customerId = createdCustomer.customerId();

        // Create updated DTO
        CustomerDTO updateDTO = new CustomerDTO(
                null,
                "Jonathan",
                "Doe",
                "456 Oak Ave",
                "Boston",
                "MA",
                "02101",
                null,
                null
        );

        // Act & Assert
        mockMvc.perform(put("/api/customers/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.firstName").value("Jonathan"))
                .andExpect(jsonPath("$.city").value("Boston"));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - Delete customer successfully")
    void testDeleteCustomer() throws Exception {
        // Arrange - Create a customer
        String createResponse = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerDTO createdCustomer = objectMapper.readValue(createResponse, CustomerDTO.class);
        Long customerId = createdCustomer.customerId();

        // Act & Assert - Delete
        mockMvc.perform(delete("/api/customers/{customerId}", customerId))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/api/customers/{customerId}", customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/customers - Get all customers")
    void testGetAllCustomers() throws Exception {
        // Arrange - Create two customers
        CustomerDTO customer2DTO = new CustomerDTO(
                null,
                "Jane",
                "Smith",
                "789 Elm St",
                "Los Angeles",
                "CA",
                "90001",
                "(555) 987-6543",
                "jane.smith@example.com"
        );

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer2DTO)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    @DisplayName("GET /api/customers/search?pattern=Doe - Search customers by name")
    void testSearchCustomers() throws Exception {
        // Arrange - Create two customers with same last name
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomerDTO)))
                .andExpect(status().isCreated());

        CustomerDTO customer2DTO = new CustomerDTO(
                null,
                "Jane",
                "Doe",
                null,
                null,
                null,
                null,
                null,
                "jane.doe@example.com"
        );

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer2DTO)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/customers/search")
                .param("pattern", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"));
    }
}
