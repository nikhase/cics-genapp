package com.example.cicsgenapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cicsgenapp.api.PolicyController;
import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.PolicyResponse;
import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import com.example.cicsgenapp.service.PolicyService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
 * Integration tests for PolicyController.
 *
 * <p>Tests REST API endpoints for policy search and retrieval.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
@WebMvcTest(PolicyController.class)
@DisplayName("Policy Controller Integration Tests")
class PolicyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PolicyService policyService;

  private PolicyResponse testPolicy1;
  private PolicyResponse testPolicy2;

  @Configuration
  static class TestConfig {
    @Bean
    public PolicyController policyController(PolicyService policyService) {
      return new PolicyController(policyService);
    }
  }

  @BeforeEach
  void setUp() {
    // Create test policy data
    UUID customerId = UUID.randomUUID();
    UUID policyId1 = UUID.randomUUID();
    UUID policyId2 = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    testPolicy1 = new PolicyResponse(
        policyId1,
        "POL-001",
        customerId,
        "John Doe",
        PolicyType.MOTOR,
        LocalDate.of(2024, 1, 1),
        LocalDate.of(2025, 1, 1),
        new BigDecimal("1500.00"),
        PolicyStatus.ACTIVE,
        now,
        now
    );

    testPolicy2 = new PolicyResponse(
        policyId2,
        "POL-002",
        customerId,
        "John Doe",
        PolicyType.HOUSE,
        LocalDate.of(2024, 3, 1),
        LocalDate.of(2026, 3, 1),
        new BigDecimal("2500.00"),
        PolicyStatus.ACTIVE,
        now,
        now
    );
  }

  @Test
  @DisplayName("GET /api/v1/policies - Search policies successfully")
  @WithMockUser
  void testSearchPoliciesSuccess() throws Exception {
    // Given: mock response with two policies
    List<PolicyResponse> policies = List.of(testPolicy1, testPolicy2);
    PagedResponse.PaginationInfo pagination = new PagedResponse.PaginationInfo(20, 0, 2, false);
    PagedResponse<PolicyResponse> pagedResponse = new PagedResponse<>(policies, pagination);

    when(policyService.searchPolicies(
        any(), any(), any(), any(Integer.class), any(Integer.class), any(), any()))
        .thenReturn(pagedResponse);

    // When: search policies
    mockMvc.perform(get("/api/v1/policies")
            .param("query", "POL")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.data[0].policyNumber").value("POL-001"))
        .andExpect(jsonPath("$.data.data[1].policyNumber").value("POL-002"));
  }

  @Test
  @DisplayName("GET /api/v1/policies - Filter by policy type")
  @WithMockUser
  void testSearchPoliciesByType() throws Exception {
    // Given: mock response with one motor policy
    List<PolicyResponse> policies = List.of(testPolicy1);
    PagedResponse.PaginationInfo pagination = new PagedResponse.PaginationInfo(20, 0, 1, false);
    PagedResponse<PolicyResponse> pagedResponse = new PagedResponse<>(policies, pagination);

    when(policyService.searchPolicies(
        any(), eq(PolicyType.MOTOR), any(), any(Integer.class), any(Integer.class), any(), any()))
        .thenReturn(pagedResponse);

    // When: search policies with type filter
    mockMvc.perform(get("/api/v1/policies")
            .param("type", "MOTOR")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].policyType").value("MOTOR"));
  }

  @Test
  @DisplayName("GET /api/v1/policies - Filter by status")
  @WithMockUser
  void testSearchPoliciesByStatus() throws Exception {
    // Given: mock response with active policies
    List<PolicyResponse> policies = List.of(testPolicy1, testPolicy2);
    PagedResponse.PaginationInfo pagination = new PagedResponse.PaginationInfo(20, 0, 2, false);
    PagedResponse<PolicyResponse> pagedResponse = new PagedResponse<>(policies, pagination);

    when(policyService.searchPolicies(
        any(), any(), eq(PolicyStatus.ACTIVE), any(Integer.class), any(Integer.class), any(), any()))
        .thenReturn(pagedResponse);

    // When: search with status filter
    mockMvc.perform(get("/api/v1/policies")
            .param("status", "ACTIVE")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].status").value("ACTIVE"));
  }

  @Test
  @DisplayName("GET /api/v1/policies/{policyId} - Get policy by ID")
  @WithMockUser
  void testGetPolicyById() throws Exception {
    // Given: mock response with one policy
    when(policyService.getPolicyById(testPolicy1.getPolicyId()))
        .thenReturn(testPolicy1);

    // When: get specific policy
    mockMvc.perform(get("/api/v1/policies/" + testPolicy1.getPolicyId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.policyNumber").value("POL-001"))
        .andExpect(jsonPath("$.data.customerName").value("John Doe"));
  }

  @Test
  @DisplayName("GET /api/v1/policies - Pagination controls")
  @WithMockUser
  void testSearchPoliciesPagination() throws Exception {
    // Given: mock response with pagination metadata
    List<PolicyResponse> policies = List.of(testPolicy1);
    PagedResponse.PaginationInfo pagination = new PagedResponse.PaginationInfo(1, 0, 100, true);
    PagedResponse<PolicyResponse> pagedResponse = new PagedResponse<>(policies, pagination);

    when(policyService.searchPolicies(
        any(), any(), any(), any(Integer.class), any(Integer.class), any(), any()))
        .thenReturn(pagedResponse);

    // When: search with pagination
    mockMvc.perform(get("/api/v1/policies")
            .param("limit", "1")
            .param("offset", "0")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.pagination.limit").value(1))
        .andExpect(jsonPath("$.data.pagination.offset").value(0));
  }

  @Test
  @DisplayName("GET /api/v1/policies - Unauthenticated access denied")
  void testSearchPoliciesUnauthorized() throws Exception {
    // When: call without authentication
    mockMvc.perform(get("/api/v1/policies")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /api/v1/policies - Empty results")
  @WithMockUser
  void testSearchPoliciesNoResults() throws Exception {
    // Given: mock empty response
    PagedResponse.PaginationInfo pagination = new PagedResponse.PaginationInfo(20, 0, 0, false);
    PagedResponse<PolicyResponse> pagedResponse = new PagedResponse<>(List.of(), pagination);

    when(policyService.searchPolicies(
        any(), any(), any(), any(Integer.class), any(Integer.class), any(), any()))
        .thenReturn(pagedResponse);

    // When: search with no results
    mockMvc.perform(get("/api/v1/policies")
            .param("query", "NONEXISTENT")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.data.length()").value(0));
  }
}
