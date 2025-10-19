package com.genapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genapp.dto.*;
import com.genapp.model.*;
import com.genapp.repository.CustomerRepository;
import com.genapp.repository.PolicyRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PolicyControllerIntegrationTest - Integration tests for Policy REST API endpoints
 *
 * Uses TestContainers with PostgreSQL for real database integration testing.
 * Tests the full stack: HTTP Request → Controller → Service → Repository → Database
 *
 * Old COBOL equivalent: Manual testing with actual CICS/VSAM files
 * New approach: Automated integration tests with TestContainers (real PostgreSQL)
 *
 * Benefits:
 *   ✅ Tests real database interactions
 *   ✅ Tests HTTP request/response serialization
 *   ✅ Tests Spring Boot integration
 *   ✅ Tests transaction boundaries
 *   ✅ No mocks - real system testing
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("PolicyController Integration Tests")
class PolicyControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("genapp_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
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

    @Autowired
    private PolicyRepository policyRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Clear repositories
        policyRepository.deleteAll();
        customerRepository.deleteAll();

        // Create test customer
        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john@example.com");
        testCustomer.setPhone("(555) 123-4567");
        testCustomer.setAddress("123 Main St");
        testCustomer.setCity("New York");
        testCustomer.setState("NY");
        testCustomer.setZipCode("10001");
        testCustomer = customerRepository.save(testCustomer);
    }

    // ===== CREATE MOTOR POLICY TESTS =====

    @Test
    @DisplayName("Should create motor policy and return 201 Created (AC7)")
    void testCreateMotorPolicy_Success() throws Exception {
        // Given: valid motor policy DTO
        MotorPolicyDTO motorPolicyDTO = new MotorPolicyDTO(
                null, // policyId
                testCustomer.getCustomerId(),
                "MOT-2025-001",
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"),
                "ACTIVE",
                "MOTOR",
                "Toyota",
                "Camry",
                2022,
                "WVWZZZ3CZ5E123456",
                "PERSONAL",
                15000,
                "COMPREHENSIVE"
        );

        // When: POST /api/policies/motor
        mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motorPolicyDTO)))
        // Then: returns 201 Created
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId", notNullValue()))
                .andExpect(jsonPath("$.policyNumber").value("MOT-2025-001"))
                .andExpect(jsonPath("$.policyType").value("MOTOR"))
                .andExpect(jsonPath("$.vehicleMake").value("Toyota"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid motor policy data")
    void testCreateMotorPolicy_InvalidData() throws Exception {
        // Given: invalid motor policy DTO (missing required fields)
        String invalidJSON = "{\"policyNumber\": \"MOT-2025-001\"}"; // missing customerId, dates, premium, etc.

        // When: POST /api/policies/motor with invalid data
        mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJSON))
        // Then: returns 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 409 Conflict for duplicate policy number")
    void testCreateMotorPolicy_DuplicatePolicyNumber() throws Exception {
        // Given: motor policy already exists
        MotorPolicyDTO firstPolicy = new MotorPolicyDTO(
                null, testCustomer.getCustomerId(), "MOT-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"), "ACTIVE", "MOTOR",
                "Toyota", "Camry", 2022, "WVWZZZ3CZ5E123456",
                "PERSONAL", 15000, "COMPREHENSIVE"
        );

        // Create first policy
        mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstPolicy)))
                .andExpect(status().isCreated());

        // When: trying to create second policy with same number
        mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstPolicy)))
        // Then: returns 409 Conflict
                .andExpect(status().isConflict());
    }

    // ===== CREATE HOUSE POLICY TESTS =====

    @Test
    @DisplayName("Should create house policy and return 201 Created (AC8)")
    void testCreateHousePolicy_Success() throws Exception {
        // Given: valid house policy DTO
        HousePolicyDTO housePolicyDTO = new HousePolicyDTO(
                null, // policyId
                testCustomer.getCustomerId(),
                "HSE-2025-001",
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2026, 10, 19),
                new BigDecimal("800.00"),
                "ACTIVE",
                "HOUSE",
                "456 Oak Ave",
                "SINGLE_FAMILY",
                1995,
                2000,
                new BigDecimal("400000.00"),
                new BigDecimal("1000.00"),
                3, 2, false, true
        );

        // When: POST /api/policies/house
        mockMvc.perform(post("/api/policies/house")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(housePolicyDTO)))
        // Then: returns 201 Created
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId", notNullValue()))
                .andExpect(jsonPath("$.policyNumber").value("HSE-2025-001"))
                .andExpect(jsonPath("$.policyType").value("HOUSE"))
                .andExpect(jsonPath("$.propertyAddress").value("456 Oak Ave"));
    }

    // ===== CREATE ENDOWMENT POLICY TESTS =====

    @Test
    @DisplayName("Should create endowment policy and return 201 Created (AC9)")
    void testCreateEndowmentPolicy_Success() throws Exception {
        // Given: valid endowment policy DTO
        EndowmentPolicyDTO endowmentPolicyDTO = new EndowmentPolicyDTO(
                null, // policyId
                testCustomer.getCustomerId(),
                "END-2025-001",
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2030, 10, 19),
                new BigDecimal("5000.00"),
                "ACTIVE",
                "ENDOWMENT",
                LocalDate.of(2050, 10, 19),
                new BigDecimal("100000.00"),
                new BigDecimal("3.50"),
                "BALANCED",
                new BigDecimal("2.50"),
                35,
                new BigDecimal("15000.00")
        );

        // When: POST /api/policies/endowment
        mockMvc.perform(post("/api/policies/endowment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endowmentPolicyDTO)))
        // Then: returns 201 Created
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId", notNullValue()))
                .andExpect(jsonPath("$.policyNumber").value("END-2025-001"))
                .andExpect(jsonPath("$.policyType").value("ENDOWMENT"))
                .andExpect(jsonPath("$.policyholderAge").value(35));
    }

    // ===== CREATE COMMERCIAL POLICY TESTS =====

    @Test
    @DisplayName("Should create commercial policy and return 201 Created (AC10)")
    void testCreateCommercialPolicy_Success() throws Exception {
        // Given: valid commercial policy DTO
        CommercialPolicyDTO commercialPolicyDTO = new CommercialPolicyDTO(
                null, // policyId
                testCustomer.getCustomerId(),
                "COM-2025-001",
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2026, 10, 19),
                new BigDecimal("15000.00"),
                "ACTIVE",
                "COMMERCIAL",
                "ABC Corp",
                "MANUFACTURING",
                "789 Industrial Dr",
                new BigDecimal("5000000.00"),
                50,
                new BigDecimal("1000000.00"),
                new BigDecimal("5000.00"),
                "MEDIUM_RISK"
        );

        // When: POST /api/policies/commercial
        mockMvc.perform(post("/api/policies/commercial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commercialPolicyDTO)))
        // Then: returns 201 Created
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId", notNullValue()))
                .andExpect(jsonPath("$.policyNumber").value("COM-2025-001"))
                .andExpect(jsonPath("$.policyType").value("COMMERCIAL"))
                .andExpect(jsonPath("$.businessName").value("ABC Corp"));
    }

    // ===== READ POLICY TESTS =====

    @Test
    @DisplayName("Should retrieve policy by ID and return 200 OK (AC5 modified)")
    void testGetPolicy_Success() throws Exception {
        // Given: policy exists
        MotorPolicyDTO motorPolicyDTO = new MotorPolicyDTO(
                null, testCustomer.getCustomerId(), "MOT-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"), "ACTIVE", "MOTOR",
                "Toyota", "Camry", 2022, "WVWZZZ3CZ5E123456",
                "PERSONAL", 15000, "COMPREHENSIVE"
        );

        // Create policy first
        String response = mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motorPolicyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MotorPolicyDTO created = objectMapper.readValue(response, MotorPolicyDTO.class);

        // When: GET /api/policies/{policyId}
        mockMvc.perform(get("/api/policies/" + created.policyId()))
        // Then: returns 200 OK with policy details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(created.policyId()))
                .andExpect(jsonPath("$.policyNumber").value("MOT-2025-001"));
    }

    @Test
    @DisplayName("Should return 404 Not Found for non-existent policy")
    void testGetPolicy_NotFound() throws Exception {
        // When: GET /api/policies/{nonexistentId}
        mockMvc.perform(get("/api/policies/999"))
        // Then: returns 404 Not Found
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should retrieve all policies for customer and return 200 OK (AC11)")
    void testGetPoliciesByCustomer_Success() throws Exception {
        // Given: customer has 2 policies
        MotorPolicyDTO motorPolicy = new MotorPolicyDTO(
                null, testCustomer.getCustomerId(), "MOT-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"), "ACTIVE", "MOTOR",
                "Toyota", "Camry", 2022, "WVWZZZ3CZ5E123456",
                "PERSONAL", 15000, "COMPREHENSIVE"
        );

        HousePolicyDTO housePolicy = new HousePolicyDTO(
                null, testCustomer.getCustomerId(), "HSE-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("800.00"), "ACTIVE", "HOUSE",
                "456 Oak Ave", "SINGLE_FAMILY", 1995, 2000,
                new BigDecimal("400000.00"), new BigDecimal("1000.00"),
                3, 2, false, true
        );

        // Create both policies
        mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motorPolicy)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/policies/house")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(housePolicy)))
                .andExpect(status().isCreated());

        // When: GET /api/policies/customer/{customerId}
        mockMvc.perform(get("/api/policies/customer/" + testCustomer.getCustomerId()))
        // Then: returns 200 OK with array of 2 policies
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].policyNumber", either(is("MOT-2025-001")).or(is("HSE-2025-001"))));
    }

    // ===== DELETE/CANCEL POLICY TESTS =====

    @Test
    @DisplayName("Should cancel policy and return 204 No Content (AC13)")
    void testCancelPolicy_Success() throws Exception {
        // Given: active policy exists
        MotorPolicyDTO motorPolicyDTO = new MotorPolicyDTO(
                null, testCustomer.getCustomerId(), "MOT-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"), "ACTIVE", "MOTOR",
                "Toyota", "Camry", 2022, "WVWZZZ3CZ5E123456",
                "PERSONAL", 15000, "COMPREHENSIVE"
        );

        // Create policy
        String response = mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motorPolicyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MotorPolicyDTO created = objectMapper.readValue(response, MotorPolicyDTO.class);

        // When: DELETE /api/policies/{policyId}
        mockMvc.perform(delete("/api/policies/" + created.policyId()))
        // Then: returns 204 No Content
                .andExpect(status().isNoContent());

        // Verify policy status is now TERMINATED
        mockMvc.perform(get("/api/policies/" + created.policyId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TERMINATED"));
    }

    @Test
    @DisplayName("Should return 400 when canceling already terminated policy")
    void testCancelPolicy_AlreadyTerminated() throws Exception {
        // Given: active policy exists
        MotorPolicyDTO motorPolicyDTO = new MotorPolicyDTO(
                null, testCustomer.getCustomerId(), "MOT-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"), "ACTIVE", "MOTOR",
                "Toyota", "Camry", 2022, "WVWZZZ3CZ5E123456",
                "PERSONAL", 15000, "COMPREHENSIVE"
        );

        String response = mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motorPolicyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MotorPolicyDTO created = objectMapper.readValue(response, MotorPolicyDTO.class);

        // Cancel it first
        mockMvc.perform(delete("/api/policies/" + created.policyId()))
                .andExpect(status().isNoContent());

        // When: trying to cancel it again
        mockMvc.perform(delete("/api/policies/" + created.policyId()))
        // Then: returns 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    // ===== UTILITY ENDPOINT TESTS =====

    @Test
    @DisplayName("Should count policies for customer")
    void testCountPolicies_Success() throws Exception {
        // Given: customer has 1 policy
        MotorPolicyDTO motorPolicyDTO = new MotorPolicyDTO(
                null, testCustomer.getCustomerId(), "MOT-2025-001",
                LocalDate.of(2025, 10, 19), LocalDate.of(2026, 10, 19),
                new BigDecimal("1200.00"), "ACTIVE", "MOTOR",
                "Toyota", "Camry", 2022, "WVWZZZ3CZ5E123456",
                "PERSONAL", 15000, "COMPREHENSIVE"
        );

        mockMvc.perform(post("/api/policies/motor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(motorPolicyDTO)))
                .andExpect(status().isCreated());

        // When: GET /api/policies/customer/{customerId}/count
        mockMvc.perform(get("/api/policies/customer/" + testCustomer.getCustomerId() + "/count"))
        // Then: returns 200 OK with count = 1
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
