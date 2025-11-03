package com.example.cicsgenapp.repository;

import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for Customer entity.
 * Provides data access operations and custom query methods for customer records.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  /**
   * Find a customer by email address.
   *
   * @param email the email address to search for
   * @return Optional containing the customer if found, empty otherwise
   */
  Optional<Customer> findByEmail(String email);

  /**
   * Find a customer by phone number.
   *
   * @param phone the phone number to search for
   * @return Optional containing the customer if found, empty otherwise
   */
  Optional<Customer> findByPhone(String phone);

  /**
   * Find all customers whose last name contains the given string (case-insensitive).
   *
   * @param lastName the partial last name to search for
   * @return list of customers matching the criteria
   */
  List<Customer> findByLastNameContainingIgnoreCase(String lastName);

  /**
   * Find all customers with a specific status created after a given date.
   * Used for reporting queries on recent registrations or active customer base.
   *
   * @param status the customer status to filter by
   * @param createdAfter the date threshold (customers created after this date)
   * @return list of customers matching the criteria
   */
  List<Customer> findByStatusAndCreatedAtAfter(Status status, LocalDateTime createdAfter);

  /**
   * Find all customers with a specific status.
   *
   * @param status the customer status to filter by
   * @return list of customers with the specified status
   */
  List<Customer> findByStatus(Status status);

  /**
   * Count customers with a specific status.
   *
   * @param status the customer status to count
   * @return number of customers with the specified status
   */
  long countByStatus(Status status);

  /**
   * Check if a customer exists by email.
   *
   * @param email the email address to check
   * @return true if customer exists with this email, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Check if a customer exists by phone.
   *
   * @param phone the phone number to check
   * @return true if customer exists with this phone, false otherwise
   */
  boolean existsByPhone(String phone);

  /**
   * Search customers by query string (matches firstName, lastName, email, phone) with optional status filter.
   *
   * <p>Searches are case-insensitive and use substring matching (LIKE %query%).
   * When query is null or empty, all customers are returned (subject to status filter).
   * INACTIVE (soft-deleted) customers are excluded from default search results.
   * To include soft-deleted customers, use searchCustomersIncludeDeleted() method.
   *
   * @param query the search query string (case-insensitive substring match)
   * @param status optional status filter (null includes all ACTIVE statuses)
   * @param pageable pagination and sorting information
   * @return Page of customers matching the search criteria (INACTIVE customers excluded)
   */
  @Query("SELECT c FROM Customer c WHERE "
      + "(:query IS NULL OR :query = '' OR "
      + "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%'))) AND "
      + "(:status IS NULL OR c.status = :status) AND "
      + "c.status = 'ACTIVE'")
  Page<Customer> searchCustomers(
      @Param("query") String query,
      @Param("status") Status status,
      Pageable pageable
  );

  /**
   * Count customers matching search criteria.
   *
   * <p>Excludes INACTIVE (soft-deleted) customers from count. Matches searchCustomers() behavior.
   *
   * @param query the search query string
   * @param status optional status filter
   * @return number of matching customers (INACTIVE customers excluded)
   */
  @Query("SELECT COUNT(c) FROM Customer c WHERE "
      + "(:query IS NULL OR :query = '' OR "
      + "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%'))) AND "
      + "(:status IS NULL OR c.status = :status) AND "
      + "c.status = 'ACTIVE'")
  long countSearchResults(
      @Param("query") String query,
      @Param("status") Status status
  );

  /**
   * Search customers including soft-deleted (INACTIVE) ones.
   *
   * <p>Same as searchCustomers() but includes INACTIVE customers.
   * Used by compliance officers and admins to view all customer records.
   *
   * @param query the search query string (case-insensitive substring match)
   * @param status optional status filter (null includes all statuses)
   * @param pageable pagination and sorting information
   * @return Page of customers matching the search criteria (includes INACTIVE)
   */
  @Query("SELECT c FROM Customer c WHERE "
      + "(:query IS NULL OR :query = '' OR "
      + "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%'))) AND "
      + "(:status IS NULL OR c.status = :status)")
  Page<Customer> searchCustomersIncludeDeleted(
      @Param("query") String query,
      @Param("status") Status status,
      Pageable pageable
  );
}
