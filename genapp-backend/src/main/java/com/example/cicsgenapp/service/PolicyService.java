package com.example.cicsgenapp.service;

import com.example.cicsgenapp.dto.PagedResponse;
import com.example.cicsgenapp.dto.PolicyResponse;
import com.example.cicsgenapp.entity.Policy;
import com.example.cicsgenapp.entity.PolicyStatus;
import com.example.cicsgenapp.entity.PolicyType;
import com.example.cicsgenapp.exception.ResourceNotFoundException;
import com.example.cicsgenapp.repository.PolicyRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for policy-related business logic.
 * Provides search, filtering, and CRUD operations for policies.
 * Implements Story 3.7 - Policy List Page with Vaadin Grid
 */
@Service
@Transactional
public class PolicyService {

  private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);
  private static final int MAX_LIMIT = 100;
  private static final int DEFAULT_LIMIT = 50;

  private final PolicyRepository policyRepository;

  public PolicyService(PolicyRepository policyRepository) {
    this.policyRepository = policyRepository;
  }

  /**
   * Search for policies with filtering and pagination.
   *
   * @param query search query (policy number or customer name)
   * @param policyType policy type filter (nullable)
   * @param status policy status filter (nullable)
   * @param limit number of results per page (1-100)
   * @param offset page offset (0-indexed)
   * @param sortBy field to sort by (default: policyNumber)
   * @param sortOrder sort order (ASC or DESC, default: ASC)
   * @return PagedResponse containing matching policies
   */
  @Transactional(readOnly = true)
  public PagedResponse<PolicyResponse> searchPolicies(
      String query,
      PolicyType policyType,
      PolicyStatus status,
      int limit,
      int offset,
      String sortBy,
      String sortOrder) {

    // Enforce limit constraints
    limit = Math.min(Math.max(1, limit), MAX_LIMIT);
    offset = Math.max(0, offset);

    // Convert offset to page number
    int page = offset;

    // Validate sort field
    String validatedSortBy = validateSortField(sortBy);
    Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(direction, validatedSortBy);

    Pageable pageable = PageRequest.of(page, limit, sort);

    // Execute search
    Page<Policy> policyPage = policyRepository.searchPolicies(
        query,
        policyType,
        status,
        pageable);

    // Convert to response DTOs
    var policies = policyPage.map(PolicyResponse::from).getContent();

    // Build response with pagination metadata
    PagedResponse.PaginationInfo paginationInfo = new PagedResponse.PaginationInfo(
        limit,
        offset,
        policyPage.getTotalElements(),
        policyPage.hasNext());
    return new PagedResponse<>(policies, paginationInfo);
  }

  /**
   * Get a policy by ID.
   *
   * @param policyId the policy ID
   * @return the policy details
   * @throws ResourceNotFoundException if policy not found
   */
  @Transactional(readOnly = true)
  public PolicyResponse getPolicyById(UUID policyId) {
    Policy policy = policyRepository.findById(policyId)
        .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + policyId));
    return PolicyResponse.from(policy);
  }

  /**
   * Validate sort field to prevent injection attacks.
   *
   * @param sortBy requested sort field
   * @return validated sort field or default
   */
  private String validateSortField(String sortBy) {
    if (sortBy == null || sortBy.isBlank()) {
      return "policyNumber";
    }

    // Whitelist allowed sort fields
    return switch (sortBy.toLowerCase()) {
      case "policynumber" -> "policyNumber";
      case "customername" -> "customer.firstName";
      case "policytype" -> "policyType";
      case "status" -> "status";
      case "premium" -> "premiumAmount";
      case "effectivedate" -> "effectiveDate";
      case "createdAt" -> "createdAt";
      default -> "policyNumber"; // Default sort field
    };
  }
}
