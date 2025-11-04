package com.example.cicsgenapp.repository;

import com.example.cicsgenapp.entity.Policy;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Policy entity.
 * Provides database access methods for policy search, filtering, and CRUD operations.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, UUID> {

  /**
   * Search for policies by query, type, and status with pagination.
   *
   * @param query search term (searches policy number or customer name)
   * @param policyType policy type filter (nullable)
   * @param status policy status filter (nullable)
   * @param pageable pagination and sorting parameters
   * @return page of matching policies
   */
  @Query("""
      SELECT p FROM Policy p
      WHERE p.deletedAt IS NULL
      AND (
        COALESCE(:query, '') = ''
        OR LOWER(p.policyNumber) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.customer.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.customer.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
      )
      AND (COALESCE(:policyType, '') = '' OR p.policyType = :policyType)
      AND (COALESCE(:status, '') = '' OR p.status = :status)
      ORDER BY p.policyNumber ASC
      """)
  Page<Policy> searchPolicies(
      @Param("query") String query,
      @Param("policyType") PolicyType policyType,
      @Param("status") PolicyStatus status,
      Pageable pageable);

  /**
   * Find policies by type.
   *
   * @param policyType the policy type to search for
   * @param pageable pagination parameters
   * @return page of policies of the specified type
   */
  Page<Policy> findByPolicyTypeAndDeletedAtIsNull(PolicyType policyType, Pageable pageable);

  /**
   * Find policies by status.
   *
   * @param status the policy status to search for
   * @param pageable pagination parameters
   * @return page of policies with the specified status
   */
  Page<Policy> findByStatusAndDeletedAtIsNull(PolicyStatus status, Pageable pageable);

  /**
   * Find a policy by policy number (case-insensitive).
   *
   * @param policyNumber the policy number to search for
   * @return the matching policy or empty if not found
   */
  java.util.Optional<Policy> findByPolicyNumberIgnoreCaseAndDeletedAtIsNull(String policyNumber);
}
