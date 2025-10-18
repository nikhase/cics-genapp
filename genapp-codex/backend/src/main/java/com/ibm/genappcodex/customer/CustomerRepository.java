package com.ibm.genappcodex.customer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    @Query("""
            select c from CustomerEntity c
            where lower(c.id) like lower(concat('%', :term, '%'))
               or lower(c.firstName) like lower(concat('%', :term, '%'))
               or lower(c.lastName) like lower(concat('%', :term, '%'))
            order by c.lastName asc, c.firstName asc, c.id asc
            """)
    List<CustomerEntity> searchCustomers(@Param("term") String term, Pageable pageable);
}
