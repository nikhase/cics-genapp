package com.genapp.repository;

import com.genapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CustomerRepository - Spring Data JPA Repository
 *
 * Replaces the COBOL VSAM file operations (READ, WRITE, UPDATE) from:
 *   - lgacdb01.cbl: INSERT INTO Db2 customer table
 *   - lgicdb01.cbl: SELECT FROM Db2 customer table
 *   - lgucdb01.cbl: UPDATE Db2 customer table
 *   - lgacvs01.cbl: WRITE to VSAM KSDSCUST
 *   - lgicvs01.cbl: READ from VSAM KSDSCUST
 *   - lgucvs01.cbl: UPDATE VSAM KSDSCUST
 *
 * Modern equivalent: No more EXEC CICS, CALL LINKAGE SECTION, or dual-write patterns.
 * Just simple JPA Repository with automatic CRUD operations.
 *
 * Benefits over original COBOL:
 *   ✅ No manual SQL writing
 *   ✅ Type-safe queries
 *   ✅ Automatic transaction management
 *   ✅ Connection pooling built-in
 *   ✅ Query optimization by Hibernate
 *   ✅ No VSAM dual-write problems
 *
 * @author Claude AI
 * @version 0.1.0
 * @since Java 21, Spring Boot 3.2
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by email (unique lookup)
     *
     * Old COBOL equivalent:
     *   EXEC CICS READ FILE('KSDSCUST')
     *       INTO WS-CUST-REC
     *       KEYLENGTH(10)
     *       NOTFND CONTINUE
     *   END-EXEC.
     *
     * @param email customer email
     * @return Optional containing customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find all customers by first name (search operation)
     *
     * Old COBOL equivalent: Manual sequential read loop with matching
     *
     * @param firstName first name to search
     * @return List of matching customers
     */
    List<Customer> findByFirstNameIgnoreCase(String firstName);

    /**
     * Find all customers by last name (search operation)
     *
     * Old COBOL equivalent: Manual sequential read loop with matching
     *
     * @param lastName last name to search
     * @return List of matching customers
     */
    List<Customer> findByLastNameIgnoreCase(String lastName);

    /**
     * Find all customers by city
     *
     * Old COBOL equivalent: Manual sequential read with city matching
     *
     * @param city city to search
     * @return List of customers in that city
     */
    List<Customer> findByCityIgnoreCase(String city);

    /**
     * Find all customers by state
     *
     * Old COBOL equivalent: Manual sequential read with state matching
     *
     * @param state state abbreviation (e.g., "CA", "NY")
     * @return List of customers in that state
     */
    List<Customer> findByStateIgnoreCase(String state);

    /**
     * Custom query: Search customers by full name pattern
     *
     * Old COBOL equivalent: Multiple sequential file reads with pattern matching
     *
     * @param pattern search pattern (using SQL LIKE)
     * @return List of matching customers
     */
    @Query("""
        SELECT c FROM Customer c
        WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :pattern, '%'))
        ORDER BY c.lastName, c.firstName
    """)
    List<Customer> searchByFullName(@Param("pattern") String pattern);

    /**
     * Check if customer exists by email (before insert validation)
     *
     * Old COBOL equivalent:
     *   EXEC CICS READ FILE('KSDSCUST')
     *       INTO WS-CUST-REC
     *       RESP WS-RESP
     *   END-EXEC.
     *   IF WS-RESP = DFHRESP(NORMAL) THEN
     *       SET ERROR-FLAG TO TRUE
     *   END-IF.
     *
     * @param email email to check
     * @return true if customer with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Count customers by state (analytics)
     *
     * Old COBOL equivalent: Manual counting loop through VSAM file
     *
     * @param state state abbreviation
     * @return count of customers in that state
     */
    long countByStateIgnoreCase(String state);
}
