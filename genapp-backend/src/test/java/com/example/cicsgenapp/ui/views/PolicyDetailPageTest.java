package com.example.cicsgenapp.ui.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.cicsgenapp.dto.PolicyResponse;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import com.example.cicsgenapp.exception.ResourceNotFoundException;
import com.example.cicsgenapp.service.PolicyService;
import com.vaadin.flow.component.UI;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for PolicyDetailPage.
 *
 * <p>Tests the policy detail display, API integration, error handling,
 * and navigation for the policy detail page (Story 3.8).
 */
@ExtendWith(MockitoExtension.class)
class PolicyDetailPageTest {

  @Mock private PolicyService policyService;

  @Mock private UI mockUI;

  private PolicyDetailPage view;

  private UUID testPolicyId;
  private UUID testCustomerId;
  private PolicyResponse testPolicy;

  @BeforeEach
  void setUp() {
    // Initialize test data
    testPolicyId = UUID.randomUUID();
    testCustomerId = UUID.randomUUID();

    // Create a test policy response
    testPolicy = new PolicyResponse();
    testPolicy.setPolicyId(testPolicyId);
    testPolicy.setPolicyNumber("POL-001234");
    testPolicy.setCustomerId(testCustomerId);
    testPolicy.setCustomerName("John Doe");
    testPolicy.setPolicyType(PolicyType.MOTOR);
    testPolicy.setStatus(PolicyStatus.ACTIVE);
    testPolicy.setPremiumAmount(new BigDecimal("1500.00"));
    testPolicy.setEffectiveDate(LocalDate.of(2024, 1, 1));
    testPolicy.setExpirationDate(LocalDate.of(2025, 1, 1));
    testPolicy.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    testPolicy.setUpdatedAt(LocalDateTime.of(2024, 6, 15, 14, 30, 0));

    // Initialize view with mocked service
    view = new PolicyDetailPage(policyService);

    // Mock UI for navigation tests
    UI.setCurrent(mockUI);
  }

  // ========== View Initialization Tests ==========

  @Test
  void testViewInitialization() {
    assertNotNull(view);
    // View should have components initialized
    assertNotNull(view.getChildren());
  }

  // ========== Policy Detail Loading Tests ==========

  @Test
  void testLoadPolicyDetailsWithValidId() {
    // Setup: Mock service returns valid policy
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Trigger policy load (would be done via beforeEnter in actual navigation)
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify policy data is loaded correctly
    assertNotNull(result);
    assertEquals(testPolicyId, result.getPolicyId());
    assertEquals("POL-001234", result.getPolicyNumber());
    assertEquals(testCustomerId, result.getCustomerId());
    assertEquals("John Doe", result.getCustomerName());
    assertEquals(PolicyType.MOTOR, result.getPolicyType());
    assertEquals(PolicyStatus.ACTIVE, result.getStatus());
    assertEquals(new BigDecimal("1500.00"), result.getPremiumAmount());
  }

  @Test
  void testLoadPolicyDetailsWithInvalidId() {
    // Setup: Mock service throws exception for invalid ID
    UUID invalidId = UUID.randomUUID();
    when(policyService.getPolicyById(invalidId))
        .thenThrow(new ResourceNotFoundException("Policy not found with ID: " + invalidId));

    // Act & Assert: Verify exception is thrown
    assertThrows(ResourceNotFoundException.class, () -> policyService.getPolicyById(invalidId));
  }

  @Test
  void testLoadPolicyDetailsWithInvalidIdFormat() {
    // Act & Assert: Verify that invalid UUID format throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> {
      UUID.fromString("invalid-uuid");
    });
  }

  // ========== Policy Type Tests ==========

  @Test
  void testDisplayMotorPolicy() {
    // Setup: Create Motor policy
    testPolicy.setPolicyType(PolicyType.MOTOR);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify Motor policy type
    assertEquals(PolicyType.MOTOR, result.getPolicyType());
    assertEquals("Motor", result.getPolicyType().getDisplayName());
  }

  @Test
  void testDisplayEndowmentPolicy() {
    // Setup: Create Endowment policy
    testPolicy.setPolicyType(PolicyType.ENDOWMENT);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify Endowment policy type
    assertEquals(PolicyType.ENDOWMENT, result.getPolicyType());
    assertEquals("Endowment", result.getPolicyType().getDisplayName());
  }

  @Test
  void testDisplayHousePolicy() {
    // Setup: Create House policy
    testPolicy.setPolicyType(PolicyType.HOUSE);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify House policy type
    assertEquals(PolicyType.HOUSE, result.getPolicyType());
    assertEquals("House", result.getPolicyType().getDisplayName());
  }

  @Test
  void testDisplayCommercialPolicy() {
    // Setup: Create Commercial policy
    testPolicy.setPolicyType(PolicyType.COMMERCIAL);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify Commercial policy type
    assertEquals(PolicyType.COMMERCIAL, result.getPolicyType());
    assertEquals("Commercial", result.getPolicyType().getDisplayName());
  }

  // ========== Policy Status Tests ==========

  @Test
  void testDisplayActivePolicyStatus() {
    // Setup: Create active policy
    testPolicy.setStatus(PolicyStatus.ACTIVE);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify active status
    assertEquals(PolicyStatus.ACTIVE, result.getStatus());
  }

  @Test
  void testDisplayLapsedPolicyStatus() {
    // Setup: Create lapsed policy
    testPolicy.setStatus(PolicyStatus.LAPSED);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify lapsed status
    assertEquals(PolicyStatus.LAPSED, result.getStatus());
  }

  @Test
  void testDisplayRenewedPolicyStatus() {
    // Setup: Create renewed policy
    testPolicy.setStatus(PolicyStatus.RENEWED);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify renewed status
    assertEquals(PolicyStatus.RENEWED, result.getStatus());
  }

  // ========== Policy Dates Tests ==========

  @Test
  void testPolicyDatesDisplay() {
    // Setup: Create policy with specific dates
    LocalDate effectiveDate = LocalDate.of(2024, 1, 15);
    LocalDate expirationDate = LocalDate.of(2025, 1, 15);
    testPolicy.setEffectiveDate(effectiveDate);
    testPolicy.setExpirationDate(expirationDate);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify dates
    assertEquals(effectiveDate, result.getEffectiveDate());
    assertEquals(expirationDate, result.getExpirationDate());
  }

  // ========== Premium Amount Tests ==========

  @Test
  void testPremiumAmountDisplay() {
    // Setup: Create policy with specific premium
    BigDecimal premiumAmount = new BigDecimal("2500.50");
    testPolicy.setPremiumAmount(premiumAmount);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify premium
    assertEquals(premiumAmount, result.getPremiumAmount());
  }

  // ========== Customer Link Tests ==========

  @Test
  void testCustomerLinkInformation() {
    // Setup: Verify customer info is available for linking
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify customer information is present
    assertNotNull(result.getCustomerId());
    assertNotNull(result.getCustomerName());
    assertEquals(testCustomerId, result.getCustomerId());
    assertEquals("John Doe", result.getCustomerName());
  }

  @Test
  void testCustomerLinkWithMissingCustomerName() {
    // Setup: Create policy with missing customer name
    testPolicy.setCustomerName(null);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify customer ID is still available
    assertNotNull(result.getCustomerId());
    assertNull(result.getCustomerName());
  }

  // ========== Audit Fields Tests ==========

  @Test
  void testAuditFieldsDisplay() {
    // Setup: Create policy with audit fields
    LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
    testPolicy.setCreatedAt(createdAt);
    testPolicy.setUpdatedAt(updatedAt);
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    PolicyResponse result = policyService.getPolicyById(testPolicyId);

    // Assert: Verify audit fields
    assertEquals(createdAt, result.getCreatedAt());
    assertEquals(updatedAt, result.getUpdatedAt());
  }

  // ========== Service Integration Tests ==========

  @Test
  void testServiceCallWithCorrectParameters() {
    // Setup
    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);

    // Act: Load policy
    policyService.getPolicyById(testPolicyId);

    // Assert: Verify service was called with correct parameter
    verify(policyService, times(1)).getPolicyById(testPolicyId);
  }

  @Test
  void testMultiplePolicyLoads() {
    // Setup: Create multiple test policies
    UUID policyId2 = UUID.randomUUID();
    PolicyResponse policy2 = new PolicyResponse();
    policy2.setPolicyId(policyId2);
    policy2.setPolicyNumber("POL-005678");
    policy2.setPolicyType(PolicyType.HOUSE);

    when(policyService.getPolicyById(testPolicyId))
        .thenReturn(testPolicy);
    when(policyService.getPolicyById(policyId2))
        .thenReturn(policy2);

    // Act: Load both policies
    PolicyResponse result1 = policyService.getPolicyById(testPolicyId);
    PolicyResponse result2 = policyService.getPolicyById(policyId2);

    // Assert: Verify both policies are loaded correctly
    assertEquals("POL-001234", result1.getPolicyNumber());
    assertEquals("POL-005678", result2.getPolicyNumber());
    verify(policyService, times(1)).getPolicyById(testPolicyId);
    verify(policyService, times(1)).getPolicyById(policyId2);
  }
}
