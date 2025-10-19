package com.genapp.service;

import com.genapp.dto.*;
import com.genapp.exception.PolicyNotFoundException;
import com.genapp.model.*;
import com.genapp.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * PolicyServiceTest - Unit tests for PolicyService business logic
 *
 * Tests all policy types (Motor, House, Endowment, Commercial) and their operations.
 * Uses Mockito for dependency injection and mocking the repository.
 *
 * Old COBOL equivalent: Manual testing of lgXXpol01.cbl programs
 * New approach: Automated unit tests with assertions and mocks
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyService Unit Tests")
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private PolicyService policyService;

    private MotorPolicyDTO motorPolicyDTO;
    private HousePolicyDTO housePolicyDTO;
    private EndowmentPolicyDTO endowmentPolicyDTO;
    private CommercialPolicyDTO commercialPolicyDTO;

    @BeforeEach
    void setUp() {
        // Setup Motor Policy DTO
        motorPolicyDTO = new MotorPolicyDTO(
                null, // policyId (null for create)
                1L,   // customerId
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

        // Setup House Policy DTO
        housePolicyDTO = new HousePolicyDTO(
                null, // policyId
                1L,   // customerId
                "HSE-2025-001",
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2026, 10, 19),
                new BigDecimal("800.00"),
                "ACTIVE",
                "HOUSE",
                "456 Oak Ave",
                "SINGLE_FAMILY",
                1995,
                new BigDecimal("2000.00"),
                new BigDecimal("400000.00"),
                new BigDecimal("1000.00"),
                3,
                2,
                false,
                true
        );

        // Setup Endowment Policy DTO
        endowmentPolicyDTO = new EndowmentPolicyDTO(
                null, // policyId
                1L,   // customerId
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

        // Setup Commercial Policy DTO
        commercialPolicyDTO = new CommercialPolicyDTO(
                null, // policyId
                1L,   // customerId
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
    }

    // ===== MOTOR POLICY TESTS =====

    @Test
    @DisplayName("Should create motor policy successfully (AC7)")
    void testCreateMotorPolicy_Success() {
        // Given: customer exists, policy number is unique
        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "MOT-2025-001")).thenReturn(false);
        when(policyRepository.save(any(MotorPolicy.class))).thenAnswer(invocation -> {
            MotorPolicy policy = invocation.getArgument(0);
            policy.setPolicyId(1L);
            return policy;
        });

        // When: creating motor policy
        MotorPolicyDTO result = policyService.createMotorPolicy(motorPolicyDTO);

        // Then: policy is created successfully
        assertNotNull(result);
        assertEquals("MOT-2025-001", result.policyNumber());
        assertEquals("MOTOR", result.policyType());
        assertEquals("Toyota", result.vehicleMake());
        verify(policyRepository).save(any(MotorPolicy.class));
    }

    @Test
    @DisplayName("Should throw exception for duplicate policy number (motor)")
    void testCreateMotorPolicy_DuplicatePolicyNumber() {
        // Given: policy number already exists
        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "MOT-2025-001")).thenReturn(true);

        // When/Then: should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            policyService.createMotorPolicy(motorPolicyDTO);
        });
        verify(policyRepository, never()).save(any());
    }

    // ===== HOUSE POLICY TESTS =====

    @Test
    @DisplayName("Should create house policy successfully (AC8)")
    void testCreateHousePolicy_Success() {
        // Given: customer exists, policy number is unique
        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "HSE-2025-001")).thenReturn(false);
        when(policyRepository.save(any(HousePolicy.class))).thenAnswer(invocation -> {
            HousePolicy policy = invocation.getArgument(0);
            policy.setPolicyId(2L);
            return policy;
        });

        // When: creating house policy
        HousePolicyDTO result = policyService.createHousePolicy(housePolicyDTO);

        // Then: policy is created successfully
        assertNotNull(result);
        assertEquals("HSE-2025-001", result.policyNumber());
        assertEquals("HOUSE", result.policyType());
        assertEquals("456 Oak Ave", result.propertyAddress());
        verify(policyRepository).save(any(HousePolicy.class));
    }

    @Test
    @DisplayName("Should validate deductible < replacement cost (house)")
    void testCreateHousePolicy_InvalidDeductible() {
        // Given: deductible >= replacement cost
        HousePolicyDTO invalidDTO = new HousePolicyDTO(
                null, 1L, "HSE-2025-001",
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
                new BigDecimal("400000.00"), // deductible = replacement cost (invalid)
                3, 2, false, true
        );

        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "HSE-2025-001")).thenReturn(false);

        // When/Then: should throw exception during validation
        assertThrows(IllegalArgumentException.class, () -> {
            policyService.createHousePolicy(invalidDTO);
        });
    }

    // ===== ENDOWMENT POLICY TESTS =====

    @Test
    @DisplayName("Should create endowment policy successfully (AC9)")
    void testCreateEndowmentPolicy_Success() {
        // Given: customer exists, policy number is unique
        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "END-2025-001")).thenReturn(false);
        when(policyRepository.save(any(EndowmentPolicy.class))).thenAnswer(invocation -> {
            EndowmentPolicy policy = invocation.getArgument(0);
            policy.setPolicyId(3L);
            return policy;
        });

        // When: creating endowment policy
        EndowmentPolicyDTO result = policyService.createEndowmentPolicy(endowmentPolicyDTO);

        // Then: policy is created successfully
        assertNotNull(result);
        assertEquals("END-2025-001", result.policyNumber());
        assertEquals("ENDOWMENT", result.policyType());
        assertEquals(35, result.policyholderAge());
        verify(policyRepository).save(any(EndowmentPolicy.class));
    }

    @Test
    @DisplayName("Should validate guaranteed return <= bonus rate (endowment)")
    void testCreateEndowmentPolicy_InvalidGuaranteedReturn() {
        // Given: guaranteed return > bonus rate (invalid)
        EndowmentPolicyDTO invalidDTO = new EndowmentPolicyDTO(
                null, 1L, "END-2025-001",
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2030, 10, 19),
                new BigDecimal("5000.00"),
                "ACTIVE",
                "ENDOWMENT",
                LocalDate.of(2050, 10, 19),
                new BigDecimal("100000.00"),
                new BigDecimal("2.50"), // bonusRate
                "BALANCED",
                new BigDecimal("3.50"), // guaranteedReturn > bonusRate (invalid)
                35,
                new BigDecimal("15000.00")
        );

        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "END-2025-001")).thenReturn(false);

        // When/Then: should throw exception during validation
        assertThrows(IllegalArgumentException.class, () -> {
            policyService.createEndowmentPolicy(invalidDTO);
        });
    }

    // ===== COMMERCIAL POLICY TESTS =====

    @Test
    @DisplayName("Should create commercial policy successfully (AC10)")
    void testCreateCommercialPolicy_Success() {
        // Given: customer exists, policy number is unique
        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "COM-2025-001")).thenReturn(false);
        when(policyRepository.save(any(CommercialPolicy.class))).thenAnswer(invocation -> {
            CommercialPolicy policy = invocation.getArgument(0);
            policy.setPolicyId(4L);
            return policy;
        });

        // When: creating commercial policy
        CommercialPolicyDTO result = policyService.createCommercialPolicy(commercialPolicyDTO);

        // Then: policy is created successfully
        assertNotNull(result);
        assertEquals("COM-2025-001", result.policyNumber());
        assertEquals("COMMERCIAL", result.policyType());
        assertEquals("ABC Corp", result.businessName());
        verify(policyRepository).save(any(CommercialPolicy.class));
    }

    @Test
    @DisplayName("Should validate deductible < coverage limit (commercial)")
    void testCreateCommercialPolicy_InvalidDeductible() {
        // Given: deductible >= coverage limit
        CommercialPolicyDTO invalidDTO = new CommercialPolicyDTO(
                null, 1L, "COM-2025-001",
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
                new BigDecimal("1000000.00"), // deductible = coverage limit (invalid)
                "MEDIUM_RISK"
        );

        doNothing().when(customerService).getCustomerById(1L);
        when(policyRepository.existsByCustomerIdAndPolicyNumber(1L, "COM-2025-001")).thenReturn(false);

        // When/Then: should throw exception during validation
        assertThrows(IllegalArgumentException.class, () -> {
            policyService.createCommercialPolicy(invalidDTO);
        });
    }

    // ===== READ TESTS =====

    @Test
    @DisplayName("Should retrieve policy by ID (AC5 modified)")
    void testGetPolicyById_Success() {
        // Given: policy exists
        MotorPolicy motorPolicy = new MotorPolicy();
        motorPolicy.setPolicyId(1L);
        motorPolicy.setCustomerId(1L);
        motorPolicy.setPolicyNumber("MOT-2025-001");
        motorPolicy.setVehicleMake("Toyota");

        when(policyRepository.findById(1L)).thenReturn(Optional.of(motorPolicy));

        // When: getting policy by ID
        PolicyDTO result = policyService.getPolicyById(1L);

        // Then: policy is retrieved successfully
        assertNotNull(result);
        assertEquals("MOT-2025-001", result.policyNumber());
        verify(policyRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw PolicyNotFoundException when policy not found")
    void testGetPolicyById_NotFound() {
        // Given: policy doesn't exist
        when(policyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then: should throw PolicyNotFoundException
        assertThrows(PolicyNotFoundException.class, () -> {
            policyService.getPolicyById(999L);
        });
    }

    @Test
    @DisplayName("Should retrieve all policies for customer (AC11)")
    void testGetPoliciesByCustomerId_Success() {
        // Given: customer has multiple policies
        List<Policy> policies = List.of(
                createMotorPolicy(1L, 1L, "MOT-2025-001"),
                createHousePolicy(2L, 1L, "HSE-2025-001"),
                createEndowmentPolicy(3L, 1L, "END-2025-001")
        );

        when(policyRepository.findByCustomerId(1L)).thenReturn(policies);
        doNothing().when(customerService).getCustomerById(1L);

        // When: getting all policies for customer
        List<PolicyDTO> result = policyService.getPoliciesByCustomerId(1L);

        // Then: all policies are retrieved
        assertEquals(3, result.size());
        verify(policyRepository).findByCustomerId(1L);
    }

    // ===== CANCEL/DELETE TESTS =====

    @Test
    @DisplayName("Should cancel policy successfully (AC13)")
    void testCancelPolicy_Success() {
        // Given: active policy exists
        MotorPolicy policy = new MotorPolicy();
        policy.setPolicyId(1L);
        policy.setStatus("ACTIVE");

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        // When: canceling policy
        policyService.cancelPolicy(1L);

        // Then: policy status is changed to TERMINATED
        assertEquals("TERMINATED", policy.getStatus());
        verify(policyRepository).save(policy);
    }

    @Test
    @DisplayName("Should throw exception when canceling already terminated policy")
    void testCancelPolicy_AlreadyTerminated() {
        // Given: already terminated policy
        MotorPolicy policy = new MotorPolicy();
        policy.setPolicyId(1L);
        policy.setStatus("TERMINATED");

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // When/Then: should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            policyService.cancelPolicy(1L);
        });
        verify(policyRepository, never()).save(any());
    }

    // ===== HELPER METHODS =====

    private MotorPolicy createMotorPolicy(Long policyId, Long customerId, String policyNumber) {
        MotorPolicy policy = new MotorPolicy();
        policy.setPolicyId(policyId);
        policy.setCustomerId(customerId);
        policy.setPolicyNumber(policyNumber);
        policy.setVehicleMake("Toyota");
        return policy;
    }

    private HousePolicy createHousePolicy(Long policyId, Long customerId, String policyNumber) {
        HousePolicy policy = new HousePolicy();
        policy.setPolicyId(policyId);
        policy.setCustomerId(customerId);
        policy.setPolicyNumber(policyNumber);
        policy.setPropertyAddress("456 Oak Ave");
        return policy;
    }

    private EndowmentPolicy createEndowmentPolicy(Long policyId, Long customerId, String policyNumber) {
        EndowmentPolicy policy = new EndowmentPolicy();
        policy.setPolicyId(policyId);
        policy.setCustomerId(customerId);
        policy.setPolicyNumber(policyNumber);
        policy.setMaturityDate(LocalDate.of(2050, 10, 19));
        return policy;
    }
}
