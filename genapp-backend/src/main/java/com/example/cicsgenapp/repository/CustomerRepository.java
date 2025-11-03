package com.example.cicsgenapp.repository;

import com.example.cicsgenapp.entity.Customer;
import com.example.cicsgenapp.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
