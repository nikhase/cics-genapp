package com.ibm.genappcodex.customer;

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
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomer(@PathVariable String id) {
        return service.getCustomer(id);
    }

    @GetMapping
    public List<CustomerDto> searchCustomers(@RequestParam(name = "q", required = false) String query) {
        return service.searchCustomers(query);
    }
}
