package com.ibm.genappcodex.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/customers")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a single customer", description = "Returns the customer that matches the provided identifier.")
    public CustomerDto getCustomer(@PathVariable String id) {
        return service.getCustomer(id);
    }

    @GetMapping
    @Operation(summary = "Search customers", description = "Returns up to 20 customers that match the provided query (ID, first name, or last name).")
    public List<CustomerDto> searchCustomers(@RequestParam(name = "q", required = false) String query) {
        return service.searchCustomers(query);
    }
}
