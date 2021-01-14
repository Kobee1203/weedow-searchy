package com.weedow.searchy.sample.java.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.neovisionaries.i18n.CountryCode;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Address extends JpaPersistable<Long> {

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    private CountryCode country;

    @ManyToMany(mappedBy = "addressEntities")
    @JsonIgnoreProperties({"addressEntities"})
    private Set<Person> persons;

    public String getStreet() {
        return street;
    }

    public Address setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address setCity(String city) {
        this.city = city;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Address setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public CountryCode getCountry() {
        return country;
    }

    public Address setCountry(CountryCode country) {
        this.country = country;
        return this;
    }

    public Set<Person> getPersons() {
        return persons;
    }

    public Address setPersons(Set<Person> persons) {
        this.persons = persons;
        return this;
    }
}