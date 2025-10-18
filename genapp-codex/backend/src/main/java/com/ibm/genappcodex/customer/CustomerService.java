package com.ibm.genappcodex.customer;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerService(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CustomerDto getCustomer(String id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer %s not found".formatted(id)));
    }

    public List<CustomerDto> searchCustomers(String query) {
        Sort sortOrder = Sort.by(Sort.Order.asc("lastName"), Sort.Order.asc("firstName"), Sort.Order.asc("id"));
        Pageable topTwenty = PageRequest.of(0, 20, sortOrder);

        if (StringUtils.hasText(query)) {
            var trimmed = query.trim();
            return repository.searchCustomers(trimmed, topTwenty).stream()
                    .map(mapper::toDto)
                    .toList();
        }

        return repository.findAll(topTwenty)
                .map(mapper::toDto)
                .getContent();
    }
}
