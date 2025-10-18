package com.ibm.genappcodex.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(length = 10)
    private String id;

    @Column(name = "first_name", length = 10, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 20, nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "house_name", length = 20)
    private String houseName;

    @Column(name = "house_number", length = 4)
    private String houseNumber;

    @Column(name = "postal_code", length = 8)
    private String postalCode;

    @Column(name = "num_policies")
    private Integer numPolicies;

    @Column(name = "phone_mobile", length = 20)
    private String phoneMobile;

    @Column(name = "phone_home", length = 20)
    private String phoneHome;

    @Column(length = 100)
    private String email;

    protected CustomerEntity() {
        // JPA
    }

    public CustomerEntity(String id, String firstName, String lastName, LocalDate dateOfBirth, String houseName,
                          String houseNumber, String postalCode, Integer numPolicies, String phoneMobile,
                          String phoneHome, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.houseName = houseName;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.numPolicies = numPolicies;
        this.phoneMobile = phoneMobile;
        this.phoneHome = phoneHome;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getNumPolicies() {
        return numPolicies;
    }

    public void setNumPolicies(Integer numPolicies) {
        this.numPolicies = numPolicies;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
