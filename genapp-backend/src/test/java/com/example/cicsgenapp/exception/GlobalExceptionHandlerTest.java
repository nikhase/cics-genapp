package com.example.cicsgenapp.exception;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cicsgenapp.api.CustomerController;
import com.example.cicsgenapp.dto.CreateCustomerRequest;
import com.example.cicsgenapp.dto.CustomerResponse;
import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Status;
import com.example.cicsgenapp.repository.CustomerRepository;
import com.example.cicsgenapp.service.AuditService;
import com.example.cicsgenapp.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for GlobalExceptionHandler.
 *
 * <p>Tests error handling for all exception types, validation errors, HTTP status codes,
 * error response structure, and traceId inclusion.
 */
@WebMvcTest(CustomerController.class)
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CustomerRepository customerRepository;

  @MockBean
  private AuditService auditService;

  private CustomerService customerService;

  @Configuration
  static class TestConfig {
    @Bean
    public CustomerService customerService(
        CustomerRepository customerRepository,
        AuditService auditService) {
      return new CustomerService(customerRepository, auditService);
    }

    @Bean
    public AuditService auditService() {
      return new AuditService(null, null, new ObjectMapper());
    }
  }

  @BeforeEach
  void setUp() {
    customerService = new CustomerService(customerRepository, auditService);
  }

  // ========== VALIDATION ERROR TESTS ==========

  @Test
  @DisplayName("POST /api/v1/customers - Validation: missing firstName returns 400 with error details")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testMissingFirstNameReturns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName(null); // Missing
    request.setLastName("Doe");
    request.setEmail("john@example.com");

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.error.details", hasSize(1)))
        .andExpect(jsonPath("$.error.details[0].field", is("firstName")))
        .andExpect(jsonPath("$.metadata.timestamp", notNullValue()))
        .andExpect(jsonPath("$.metadata.traceId", notNullValue()));
  }

  @Test
  @DisplayName("POST /api/v1/customers - Validation: firstName too long returns 400")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testFirstNameTooLongReturns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName("a".repeat(101)); // > 100 chars
    request.setLastName("Doe");
    request.setEmail("john@example.com");

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.error.details", hasSize(1)));
  }

  @Test
  @DisplayName("POST /api/v1/customers - Validation: invalid email format returns 400")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testInvalidEmailFormatReturns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "invalid-email"
    );

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.error.details[*].field", hasSize(1)));
  }

  @Test
  @DisplayName("POST /api/v1/customers - Validation: invalid phone format returns 400")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testInvalidPhoneFormatReturns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );
    request.setPhone("invalid-phone");

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")));
  }

  @Test
  @DisplayName("POST /api/v1/customers - Validation: future date of birth returns 400")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testFutureDateOfBirthReturns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );
    request.setDateOfBirth(LocalDate.now().plusDays(1)); // Future date

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")));
  }

  @Test
  @DisplayName("POST /api/v1/customers - Validation: age < 18 returns 400")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testAgeUnder18Returns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );
    request.setDateOfBirth(LocalDate.now().minusYears(17)); // 17 years old

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")));
  }

  @Test
  @DisplayName("POST /api/v1/customers - Validation: multiple field errors returns 400 with all details")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testMultipleValidationErrorsReturns400() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName(""); // Empty
    request.setLastName(""); // Empty
    request.setEmail("invalid");  // Invalid
    request.setPhone("bad"); // Invalid

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code", is("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.error.details", hasSize(4)))
        .andExpect(jsonPath("$.metadata.traceId", notNullValue()));
  }

  // ========== DUPLICATE KEY ERROR TESTS ==========

  @Test
  @DisplayName("POST /api/v1/customers - Duplicate email returns 409 Conflict")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testDuplicateEmailReturns409() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );

    // Mock repository to return existing customer
    Customer existing = new Customer("Jane", "Doe", "john@example.com");
    when(customerRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.of(existing));

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code", is("DUPLICATE_KEY")))
        .andExpect(jsonPath("$.error.message", notNullValue()))
        .andExpect(jsonPath("$.metadata.traceId", notNullValue()));
  }

  // ========== NOT FOUND ERROR TESTS ==========

  @Test
  @DisplayName("GET /api/v1/customers/{id} - Non-existent customer returns 404 Not Found")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testGetNonExistentCustomerReturns404() throws Exception {
    UUID customerId = UUID.randomUUID();

    when(customerRepository.findById(customerId))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/customers/{id}", customerId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code", is("RESOURCE_NOT_FOUND")))
        .andExpect(jsonPath("$.error.message", notNullValue()))
        .andExpect(jsonPath("$.metadata.timestamp", notNullValue()))
        .andExpect(jsonPath("$.metadata.traceId", notNullValue()));
  }

  // ========== ERROR RESPONSE STRUCTURE TESTS ==========

  @Test
  @DisplayName("Error response includes required metadata fields")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testErrorResponseIncludesMetadata() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName(null);
    request.setLastName("Doe");
    request.setEmail("john@example.com");

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()))
        .andExpect(jsonPath("$.error.code", notNullValue()))
        .andExpect(jsonPath("$.error.message", notNullValue()))
        .andExpect(jsonPath("$.metadata", notNullValue()))
        .andExpect(jsonPath("$.metadata.timestamp", notNullValue()))
        .andExpect(jsonPath("$.metadata.traceId", notNullValue()));
  }

  @Test
  @DisplayName("Validation error response includes field details")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testValidationErrorIncludesFieldDetails() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName(null);
    request.setLastName("Doe");
    request.setEmail("john@example.com");

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.details", hasSize(1)))
        .andExpect(jsonPath("$.error.details[0].field", notNullValue()))
        .andExpect(jsonPath("$.error.details[0].message", notNullValue()));
  }

  // ========== HTTP STATUS CODE TESTS ==========

  @Test
  @DisplayName("HTTP status code: 400 Bad Request for validation errors")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testStatus400ForValidationError() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "invalid"
    );

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("HTTP status code: 404 Not Found for missing resource")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testStatus404ForNotFound() throws Exception {
    UUID customerId = UUID.randomUUID();
    when(customerRepository.findById(customerId))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/customers/{id}", customerId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("HTTP status code: 409 Conflict for duplicate key")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testStatus409ForConflict() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "John",
        "Doe",
        "john@example.com"
    );

    Customer existing = new Customer("Jane", "Doe", "john@example.com");
    when(customerRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.of(existing));

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  // ========== TRACE ID TESTS ==========

  @Test
  @DisplayName("All error responses include traceId in metadata")
  @WithMockUser(roles = "CUSTOMER_SERVICE_AGENT")
  void testAllErrorsIncludeTraceId() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName(null);
    request.setLastName("Doe");
    request.setEmail("john@example.com");

    mockMvc.perform(post("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.metadata.traceId", notNullValue()))
        .andExpect(jsonPath("$.metadata.traceId", is(notNullValue())));
  }
}
