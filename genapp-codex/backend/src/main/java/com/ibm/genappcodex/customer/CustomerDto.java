package com.ibm.genappcodex.customer;

import java.time.LocalDate;

public record CustomerDto(
        String id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String houseName,
        String houseNumber,
        String postalCode,
        Integer numPolicies,
        String phoneMobile,
        String phoneHome,
        String email
) {
}
