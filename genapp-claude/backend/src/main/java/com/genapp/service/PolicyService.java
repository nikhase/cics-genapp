package com.genapp.service;

import com.genapp.dto.*;
import com.genapp.exception.PolicyNotFoundException;
import com.genapp.model.*;
import com.genapp.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PolicyService - Business logic layer for Policy management
 *
 * Replaces the COBOL business logic from:
 *   - lgacpol01.cbl: Add/Create Policy business rules
 *   - lgicpol01.cbl: Inquire/Read Policy business rules
 *   - lgucpol01.cbl: Update Policy business rules
 *   - lgdcpol01.cbl: Delete/Cancel Policy business rules
 *
 * Old COBOL pattern:
 *   1. ACCEPT input into COMMAREA
 *   2. Validate data
 *   3. CALL data layer (lgacdb01, lgacvs01)
 *   4. Return result in COMMAREA
 *   5. Handle CICS response codes
 *
 * New Spring pattern:
 *   1. Service receives DTO from Controller
 *   2. Convert DTO to Entity
 *   3. Validate entity business rules
 *   4. Save to repository
 *   5. Convert entity back to DTO for response
 *   6. Throw exceptions for errors (handled by GlobalExceptionHandler)
 *
 * Benefits over COBOL:
 *   ✅ Automatic transaction management (@Transactional)
 *   ✅ Exception-based error handling (no CICS response codes)
 *   ✅ Type-safe sealed class operations
 *   ✅ Dependency injection (@RequiredArgsConstructor)
 *   ✅ Logging with Slf4j (no manual GENACNTL queue writes)
 *   ✅ Testable with mocks and stubs
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Service
@Transactional
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private final PolicyRepository policyRepository;
    private final CustomerService customerService;

    /**
     * Constructor for dependency injection
     */
    public PolicyService(PolicyRepository policyRepository, CustomerService customerService) {
        this.policyRepository = policyRepository;
        this.customerService = customerService;
    }

    /**
     * Create a new motor policy
     *
     * Old COBOL equivalent:
     *   EXEC CICS LINK PROGRAM('LGACPOL01')
     *       COMMAREA(WS-POLICY-COMMAREA)
     *       LENGTH(WS-COMMAREA-LEN)
     *   END-EXEC.
     *
     * @param motorPolicyDTO DTO with motor policy details
     * @return created motor policy DTO
     * @throws IllegalArgumentException if customer not found or invalid data
     */
    public MotorPolicyDTO createMotorPolicy(MotorPolicyDTO motorPolicyDTO) {
        log.info("Creating motor policy for customer ID: {}", motorPolicyDTO.customerId());

        // Verify customer exists
        customerService.getCustomerById(motorPolicyDTO.customerId());

        // Check for duplicate policy number
        if (policyRepository.existsByCustomerIdAndPolicyNumber(
                motorPolicyDTO.customerId(),
                motorPolicyDTO.policyNumber())) {
            throw new IllegalArgumentException(
                    String.format("Policy number '%s' already exists for this customer",
                            motorPolicyDTO.policyNumber()));
        }

        // Validate policy-specific rules before creation
        motorPolicyDTO.validateMotorSpecificFields();

        // Create entity using constructor - all fields from parent Policy class plus MotorPolicy-specific fields
        MotorPolicy motorPolicy = new MotorPolicy(
                null, // policyId (auto-generated)
                motorPolicyDTO.customerId(),
                motorPolicyDTO.policyNumber(),
                motorPolicyDTO.startDate(),
                motorPolicyDTO.endDate(),
                motorPolicyDTO.premium(),
                motorPolicyDTO.status() != null ? motorPolicyDTO.status() : "ACTIVE",
                null, // createdAt (set by @PrePersist)
                null, // updatedAt (set by @PrePersist)
                motorPolicyDTO.vehicleMake(),
                motorPolicyDTO.vehicleModel(),
                motorPolicyDTO.vehicleYear(),
                motorPolicyDTO.vehicleVin(),
                motorPolicyDTO.usageType(),
                motorPolicyDTO.annualMileage(),
                motorPolicyDTO.coverageType()
        );

        // Validate policy-specific rules
        motorPolicy.validatePolicySpecificFields();

        // Save to database
        MotorPolicy savedPolicy = policyRepository.save(motorPolicy);
        log.info("Motor policy created with ID: {}", savedPolicy.getPolicyId());

        return convertToMotorPolicyDTO(savedPolicy);
    }

    /**
     * Create a new house policy
     */
    public HousePolicyDTO createHousePolicy(HousePolicyDTO housePolicyDTO) {
        log.info("Creating house policy for customer ID: {}", housePolicyDTO.customerId());

        // Verify customer exists
        customerService.getCustomerById(housePolicyDTO.customerId());

        // Check for duplicate policy number
        if (policyRepository.existsByCustomerIdAndPolicyNumber(
                housePolicyDTO.customerId(),
                housePolicyDTO.policyNumber())) {
            throw new IllegalArgumentException(
                    String.format("Policy number '%s' already exists for this customer",
                            housePolicyDTO.policyNumber()));
        }

        // Validate policy-specific rules before creation
        housePolicyDTO.validateHouseSpecificFields();

        // Create entity using constructor - all fields from parent Policy class plus HousePolicy-specific fields
        HousePolicy housePolicy = new HousePolicy(
                null, // policyId (auto-generated)
                housePolicyDTO.customerId(),
                housePolicyDTO.policyNumber(),
                housePolicyDTO.startDate(),
                housePolicyDTO.endDate(),
                housePolicyDTO.premium(),
                housePolicyDTO.status() != null ? housePolicyDTO.status() : "ACTIVE",
                null, // createdAt (set by @PrePersist)
                null, // updatedAt (set by @PrePersist)
                housePolicyDTO.propertyAddress(),
                housePolicyDTO.propertyType(),
                housePolicyDTO.constructionYear(),
                housePolicyDTO.squareFootage(),
                housePolicyDTO.replacementCost(),
                housePolicyDTO.deductible(),
                housePolicyDTO.numBedrooms(),
                housePolicyDTO.numBathrooms(),
                housePolicyDTO.hasPool(),
                housePolicyDTO.hasAlarmSystem()
        );

        // Validate policy-specific rules
        housePolicy.validatePolicySpecificFields();

        // Save to database
        HousePolicy savedPolicy = policyRepository.save(housePolicy);
        log.info("House policy created with ID: {}", savedPolicy.getPolicyId());

        return convertToHousePolicyDTO(savedPolicy);
    }

    /**
     * Create a new endowment policy
     */
    public EndowmentPolicyDTO createEndowmentPolicy(EndowmentPolicyDTO endowmentPolicyDTO) {
        log.info("Creating endowment policy for customer ID: {}", endowmentPolicyDTO.customerId());

        // Verify customer exists
        customerService.getCustomerById(endowmentPolicyDTO.customerId());

        // Check for duplicate policy number
        if (policyRepository.existsByCustomerIdAndPolicyNumber(
                endowmentPolicyDTO.customerId(),
                endowmentPolicyDTO.policyNumber())) {
            throw new IllegalArgumentException(
                    String.format("Policy number '%s' already exists for this customer",
                            endowmentPolicyDTO.policyNumber()));
        }

        // Validate policy-specific rules before creation
        endowmentPolicyDTO.validateEndowmentSpecificFields();

        // Create entity using constructor - all fields from parent Policy class plus EndowmentPolicy-specific fields
        EndowmentPolicy endowmentPolicy = new EndowmentPolicy(
                null, // policyId (auto-generated)
                endowmentPolicyDTO.customerId(),
                endowmentPolicyDTO.policyNumber(),
                endowmentPolicyDTO.startDate(),
                endowmentPolicyDTO.endDate(),
                endowmentPolicyDTO.premium(),
                endowmentPolicyDTO.status() != null ? endowmentPolicyDTO.status() : "ACTIVE",
                null, // createdAt (set by @PrePersist)
                null, // updatedAt (set by @PrePersist)
                endowmentPolicyDTO.maturityDate(),
                endowmentPolicyDTO.insuredAmount(),
                endowmentPolicyDTO.bonusRate(),
                endowmentPolicyDTO.investmentType(),
                endowmentPolicyDTO.guaranteedReturn(),
                endowmentPolicyDTO.policyholderAge(),
                endowmentPolicyDTO.surrenderValue()
        );

        // Validate policy-specific rules
        endowmentPolicy.validatePolicySpecificFields();

        // Save to database
        EndowmentPolicy savedPolicy = policyRepository.save(endowmentPolicy);
        log.info("Endowment policy created with ID: {}", savedPolicy.getPolicyId());

        return convertToEndowmentPolicyDTO(savedPolicy);
    }

    /**
     * Create a new commercial policy
     */
    public CommercialPolicyDTO createCommercialPolicy(CommercialPolicyDTO commercialPolicyDTO) {
        log.info("Creating commercial policy for customer ID: {}", commercialPolicyDTO.customerId());

        // Verify customer exists
        customerService.getCustomerById(commercialPolicyDTO.customerId());

        // Check for duplicate policy number
        if (policyRepository.existsByCustomerIdAndPolicyNumber(
                commercialPolicyDTO.customerId(),
                commercialPolicyDTO.policyNumber())) {
            throw new IllegalArgumentException(
                    String.format("Policy number '%s' already exists for this customer",
                            commercialPolicyDTO.policyNumber()));
        }

        // Validate policy-specific rules before creation
        commercialPolicyDTO.validateCommercialSpecificFields();

        // Create entity using constructor - all fields from parent Policy class plus CommercialPolicy-specific fields
        CommercialPolicy commercialPolicy = new CommercialPolicy(
                null, // policyId (auto-generated)
                commercialPolicyDTO.customerId(),
                commercialPolicyDTO.policyNumber(),
                commercialPolicyDTO.startDate(),
                commercialPolicyDTO.endDate(),
                commercialPolicyDTO.premium(),
                commercialPolicyDTO.status() != null ? commercialPolicyDTO.status() : "ACTIVE",
                null, // createdAt (set by @PrePersist)
                null, // updatedAt (set by @PrePersist)
                commercialPolicyDTO.businessName(),
                commercialPolicyDTO.businessType(),
                commercialPolicyDTO.propertyAddress(),
                commercialPolicyDTO.annualRevenue(),
                commercialPolicyDTO.numEmployees(),
                commercialPolicyDTO.coverageLimit(),
                commercialPolicyDTO.deductible(),
                commercialPolicyDTO.businessClassification()
        );

        // Validate policy-specific rules
        commercialPolicy.validatePolicySpecificFields();

        // Save to database
        CommercialPolicy savedPolicy = policyRepository.save(commercialPolicy);
        log.info("Commercial policy created with ID: {}", savedPolicy.getPolicyId());

        return convertToCommercialPolicyDTO(savedPolicy);
    }

    /**
     * Get policy by ID (polymorphic - returns specific DTO type)
     *
     * Old COBOL equivalent:
     *   EXEC CICS READ FILE('KSDSPOLY')
     *       INTO WS-POLICY-REC
     *       KEYLENGTH(21)
     *   END-EXEC.
     */
    @Transactional(readOnly = true)
    public Object getPolicyById(Long policyId) {
        log.info("Retrieving policy ID: {}", policyId);

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException(
                        String.format("Policy not found with ID: %d", policyId)));

        return convertToDTO(policy);
    }

    /**
     * Get all policies for a customer (returns polymorphic DTOs)
     */
    @Transactional(readOnly = true)
    public List<?> getPoliciesByCustomerId(Long customerId) {
        log.info("Retrieving policies for customer ID: {}", customerId);

        // Verify customer exists
        customerService.getCustomerById(customerId);

        return policyRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get motor policies for a customer
     */
    @Transactional(readOnly = true)
    public List<MotorPolicyDTO> getMotorPoliciesByCustomerId(Long customerId) {
        log.info("Retrieving motor policies for customer ID: {}", customerId);

        return policyRepository.findMotorPoliciesByCustomerId(customerId).stream()
                .map(this::convertToMotorPolicyDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get house policies for a customer
     */
    @Transactional(readOnly = true)
    public List<HousePolicyDTO> getHousePoliciesByCustomerId(Long customerId) {
        log.info("Retrieving house policies for customer ID: {}", customerId);

        return policyRepository.findHousePoliciesByCustomerId(customerId).stream()
                .map(this::convertToHousePolicyDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get endowment policies for a customer
     */
    @Transactional(readOnly = true)
    public List<EndowmentPolicyDTO> getEndowmentPoliciesByCustomerId(Long customerId) {
        log.info("Retrieving endowment policies for customer ID: {}", customerId);

        return policyRepository.findEndowmentPoliciesByCustomerId(customerId).stream()
                .map(this::convertToEndowmentPolicyDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get commercial policies for a customer
     */
    @Transactional(readOnly = true)
    public List<CommercialPolicyDTO> getCommercialPoliciesByCustomerId(Long customerId) {
        log.info("Retrieving commercial policies for customer ID: {}", customerId);

        return policyRepository.findCommercialPoliciesByCustomerId(customerId).stream()
                .map(this::convertToCommercialPolicyDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cancel/terminate a policy
     * Changes status from ACTIVE to TERMINATED
     *
     * Note: Since Policy entities use @Setter from Lombok, we can still use setStatus()
     * for updates to existing entities. The constructor-based approach is primarily
     * needed for object creation with sealed classes.
     *
     * Old COBOL equivalent:
     *   EXEC CICS UPDATE FILE('KSDSPOLY')
     *       FROM WS-POLICY-REC
     *       KEYLENGTH(21)
     *   END-EXEC.
     */
    public void cancelPolicy(Long policyId) {
        log.info("Canceling policy ID: {}", policyId);

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException(
                        String.format("Policy not found with ID: %d", policyId)));

        if ("TERMINATED".equals(policy.getStatus())) {
            throw new IllegalArgumentException("Policy is already terminated");
        }

        // For updates to existing entities, setters work fine even with sealed classes
        // The issue is only with object construction
        policy.setStatus("TERMINATED");
        policyRepository.save(policy);

        log.info("Policy ID {} terminated successfully", policyId);
    }

    /**
     * Get active policies for a customer (returns polymorphic DTOs)
     */
    @Transactional(readOnly = true)
    public List<?> getActivePoliciesByCustomerId(Long customerId) {
        log.info("Retrieving active policies for customer ID: {}", customerId);

        return policyRepository.findByCustomerIdAndStatus(customerId, "ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expired policies (returns polymorphic DTOs)
     */
    @Transactional(readOnly = true)
    public List<?> getExpiredPolicies() {
        log.info("Retrieving expired policies");

        return policyRepository.findByEndDateBefore(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Count policies for a customer
     */
    @Transactional(readOnly = true)
    public long countPoliciesByCustomerId(Long customerId) {
        return policyRepository.countByCustomerId(customerId);
    }

    /**
     * Count active policies for a customer
     */
    @Transactional(readOnly = true)
    public long countActivePoliciesByCustomerId(Long customerId) {
        return policyRepository.countByCustomerIdAndStatus(customerId, "ACTIVE");
    }

    // ===== Conversion Methods (DTO <-> Entity) =====

    /**
     * Convert any Policy entity to appropriate DTO type
     * Uses sealed class pattern matching to determine type.
     * Returns Object to support polymorphic DTO types (MotorPolicyDTO, HousePolicyDTO, etc.)
     * REST automatically serializes each DTO type to JSON correctly.
     */
    private Object convertToDTO(Policy policy) {
        return switch (policy) {
            case MotorPolicy mp -> convertToMotorPolicyDTO(mp);
            case HousePolicy hp -> convertToHousePolicyDTO(hp);
            case EndowmentPolicy ep -> convertToEndowmentPolicyDTO(ep);
            case CommercialPolicy cp -> convertToCommercialPolicyDTO(cp);
            default -> throw new IllegalArgumentException("Unknown policy type: " + policy.getClass().getName());
        };
    }

    /**
     * Convert MotorPolicy entity to DTO
     */
    private MotorPolicyDTO convertToMotorPolicyDTO(MotorPolicy policy) {
        return new MotorPolicyDTO(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPolicyNumber(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getPremium(),
                policy.getStatus(),
                policy.getPolicyType(),
                policy.getVehicleMake(),
                policy.getVehicleModel(),
                policy.getVehicleYear(),
                policy.getVehicleVin(),
                policy.getUsageType(),
                policy.getAnnualMileage(),
                policy.getCoverageType()
        );
    }

    /**
     * Convert HousePolicy entity to DTO
     */
    private HousePolicyDTO convertToHousePolicyDTO(HousePolicy policy) {
        return new HousePolicyDTO(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPolicyNumber(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getPremium(),
                policy.getStatus(),
                policy.getPolicyType(),
                policy.getPropertyAddress(),
                policy.getPropertyType(),
                policy.getConstructionYear(),
                policy.getSquareFootage(),
                policy.getReplacementCost(),
                policy.getDeductible(),
                policy.getNumBedrooms(),
                policy.getNumBathrooms(),
                policy.getHasPool(),
                policy.getHasAlarmSystem()
        );
    }

    /**
     * Convert EndowmentPolicy entity to DTO
     */
    private EndowmentPolicyDTO convertToEndowmentPolicyDTO(EndowmentPolicy policy) {
        return new EndowmentPolicyDTO(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPolicyNumber(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getPremium(),
                policy.getStatus(),
                policy.getPolicyType(),
                policy.getMaturityDate(),
                policy.getInsuredAmount(),
                policy.getBonusRate(),
                policy.getInvestmentType(),
                policy.getGuaranteedReturn(),
                policy.getPolicyholderAge(),
                policy.getSurrenderValue()
        );
    }

    /**
     * Convert CommercialPolicy entity to DTO
     */
    private CommercialPolicyDTO convertToCommercialPolicyDTO(CommercialPolicy policy) {
        return new CommercialPolicyDTO(
                policy.getPolicyId(),
                policy.getCustomerId(),
                policy.getPolicyNumber(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getPremium(),
                policy.getStatus(),
                policy.getPolicyType(),
                policy.getBusinessName(),
                policy.getBusinessType(),
                policy.getPropertyAddress(),
                policy.getAnnualRevenue(),
                policy.getNumEmployees(),
                policy.getCoverageLimit(),
                policy.getDeductible(),
                policy.getBusinessClassification()
        );
    }
}
