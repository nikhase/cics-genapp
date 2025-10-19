package com.genapp.repository;

import com.genapp.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * PolicyRepository - Spring Data JPA Repository for Policy management
 *
 * Replaces the COBOL policy file operations (READ, WRITE, UPDATE) from:
 *   - lgapol01.cbl: INSERT INTO Db2 policy table
 *   - lgipol01.cbl: SELECT FROM Db2 policy table
 *   - lgupol01.cbl: UPDATE Db2 policy table
 *   - lgdpol01.cbl: DELETE/UPDATE Db2 policy table (cancel policy)
 *   - VSAM KSDSPOLY file access (READ, WRITE, UPDATE, DELETE operations)
 *
 * Modern equivalent: No more EXEC CICS, CALL LINKAGE SECTION, or dual-write patterns.
 * Just simple JPA Repository with automatic CRUD operations.
 *
 * Benefits over original COBOL:
 *   ✅ No manual SQL writing
 *   ✅ Type-safe queries with sealed class hierarchy
 *   ✅ Polymorphic queries (all policy types or specific types)
 *   ✅ Automatic transaction management
 *   ✅ Connection pooling built-in
 *   ✅ Query optimization by Hibernate
 *   ✅ No VSAM dual-write problems
 *   ✅ Sealed class inheritance ensures type safety at compile time
 *
 * Usage Examples:
 *   - Find all policies for a customer: findByCustomerId(1L)
 *   - Find specific policy type: findMotorPoliciesByCustomerId(1L)
 *   - Search by policy number: findByPolicyNumber("MOT-2025-001")
 *   - Find active policies: findByStatus("ACTIVE")
 *   - Complex queries: searchPoliciesByCustomerAndType(1L, "MOTOR")
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /**
     * Find all policies for a specific customer
     *
     * Old COBOL equivalent:
     *   EXEC CICS STARTBR FILE('KSDSPOLY')
     *       INTO WS-POLICY-REC
     *       KEYLENGTH(10)
     *   END-EXEC.
     *   Performs READ NEXT until all customer's policies found
     *
     * @param customerId customer ID
     * @return List of all policies for customer
     */
    List<Policy> findByCustomerId(Long customerId);

    /**
     * Find a specific policy by ID
     *
     * Old COBOL equivalent:
     *   EXEC CICS READ FILE('KSDSPOLY')
     *       INTO WS-POLICY-REC
     *       KEYLENGTH(21)
     *   END-EXEC.
     *
     * @param policyId policy ID
     * @return Optional containing policy if found
     */
    Optional<Policy> findById(Long policyId);

    /**
     * Find policy by unique policy number within a customer's policies
     *
     * Old COBOL equivalent: Custom sequential search in VSAM KSDSPOLY
     *
     * @param customerId customer ID
     * @param policyNumber policy number
     * @return Optional containing policy if found
     */
    Optional<Policy> findByCustomerIdAndPolicyNumber(Long customerId, String policyNumber);

    /**
     * Find all policies with a specific status
     *
     * Old COBOL equivalent: Manual sequential read with status matching
     *
     * @param status policy status (ACTIVE, TERMINATED, EXPIRED, SUSPENDED)
     * @return List of policies with specified status
     */
    List<Policy> findByStatus(String status);

    /**
     * Find active policies for a customer
     *
     * Old COBOL equivalent: Custom search logic
     *
     * @param customerId customer ID
     * @param status status to match
     * @return List of active policies for customer
     */
    List<Policy> findByCustomerIdAndStatus(Long customerId, String status);

    /**
     * Find Motor policies for a specific customer
     * Uses runtime type checking (instanceof MotorPolicy)
     *
     * Old COBOL equivalent: Read KSDSPOLY and check policy type indicator = 'M'
     *
     * @param customerId customer ID
     * @return List of Motor policies for customer
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.customerId = :customerId AND TYPE(p) = MotorPolicy
        ORDER BY p.startDate DESC
    """)
    List<MotorPolicy> findMotorPoliciesByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find House policies for a specific customer
     *
     * Old COBOL equivalent: Read KSDSPOLY and check policy type indicator = 'H'
     *
     * @param customerId customer ID
     * @return List of House policies for customer
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.customerId = :customerId AND TYPE(p) = HousePolicy
        ORDER BY p.startDate DESC
    """)
    List<HousePolicy> findHousePoliciesByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find Endowment policies for a specific customer
     *
     * Old COBOL equivalent: Read KSDSPOLY and check policy type indicator = 'E'
     *
     * @param customerId customer ID
     * @return List of Endowment policies for customer
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.customerId = :customerId AND TYPE(p) = EndowmentPolicy
        ORDER BY p.startDate DESC
    """)
    List<EndowmentPolicy> findEndowmentPoliciesByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find Commercial policies for a specific customer
     *
     * Old COBOL equivalent: Read KSDSPOLY and check policy type indicator = 'C'
     *
     * @param customerId customer ID
     * @return List of Commercial policies for customer
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.customerId = :customerId AND TYPE(p) = CommercialPolicy
        ORDER BY p.startDate DESC
    """)
    List<CommercialPolicy> findCommercialPoliciesByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find all policies expiring after a certain date
     * Useful for renewal notifications
     *
     * Old COBOL equivalent: Custom date comparison logic
     *
     * @param expiryDate date to check against
     * @return List of policies expiring after date
     */
    List<Policy> findByEndDateAfter(LocalDate expiryDate);

    /**
     * Find all expired policies (ended before today)
     *
     * Old COBOL equivalent: Custom date comparison logic
     *
     * @param today current date
     * @return List of expired policies
     */
    List<Policy> findByEndDateBefore(LocalDate today);

    /**
     * Find policies by premium range
     * Useful for premium analysis and reporting
     *
     * Old COBOL equivalent: Manual numeric comparison loop
     *
     * @param minPremium minimum premium
     * @param maxPremium maximum premium
     * @return List of policies in premium range
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.premium >= :minPremium AND p.premium <= :maxPremium
        ORDER BY p.premium DESC
    """)
    List<Policy> findByPremiumRange(
        @Param("minPremium") BigDecimal minPremium,
        @Param("maxPremium") BigDecimal maxPremium
    );

    /**
     * Count policies by customer
     *
     * Old COBOL equivalent: Manual counting loop
     *
     * @param customerId customer ID
     * @return count of policies for customer
     */
    long countByCustomerId(Long customerId);

    /**
     * Count active policies for customer
     *
     * Old COBOL equivalent: Manual counting loop with status check
     *
     * @param customerId customer ID
     * @param status status to match
     * @return count of active policies
     */
    long countByCustomerIdAndStatus(Long customerId, String status);

    /**
     * Check if policy exists by customer and policy number
     *
     * Old COBOL equivalent: READ with NOTFND condition
     *
     * @param customerId customer ID
     * @param policyNumber policy number
     * @return true if policy exists
     */
    boolean existsByCustomerIdAndPolicyNumber(Long customerId, String policyNumber);

    /**
     * Find all policies for a customer of a specific type
     * Useful for getting all policies of one type
     *
     * Old COBOL equivalent: Read KSDSPOLY and check policy type
     *
     * @param customerId customer ID
     * @param policyType policy type (MOTOR, HOUSE, ENDOWMENT, COMMERCIAL)
     * @return List of policies of specified type
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.customerId = :customerId
        AND p.policyType = COALESCE(:policyType, p.policyType)
        ORDER BY p.startDate DESC
    """)
    List<Policy> findByCustomerIdAndPolicyType(
        @Param("customerId") Long customerId,
        @Param("policyType") String policyType
    );

    /**
     * Search policies by customer with flexible filtering
     *
     * Old COBOL equivalent: Complex sequential search with multiple conditions
     *
     * @param customerId customer ID
     * @param status policy status (nullable - omitted if null)
     * @return List of matching policies
     */
    @Query("""
        SELECT p FROM Policy p
        WHERE p.customerId = :customerId
        AND (:status IS NULL OR p.status = :status)
        ORDER BY p.startDate DESC
    """)
    List<Policy> searchPolicies(
        @Param("customerId") Long customerId,
        @Param("status") String status
    );
}
