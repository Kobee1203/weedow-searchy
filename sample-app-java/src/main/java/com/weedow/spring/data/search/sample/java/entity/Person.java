package com.weedow.spring.data.search.sample.java.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
public class Person extends JpaPersistable<Long> {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, length = 100)
    private String email;

    @Column
    private LocalDateTime birthday;

    @Column
    private Double height;

    @Column
    private Double weight;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> nickNames;

    @ElementCollection
    @CollectionTable(name = "person_phone_numbers", joinColumns = {@JoinColumn(name = "person_id")})
    @Column(name = "phone_number")
    private Set<String> phoneNumbers;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "person_address",
            joinColumns = {@JoinColumn(name = "personId")},
            inverseJoinColumns = {@JoinColumn(name = "addressId")})
    @JsonIgnoreProperties("persons")
    private Set<Address> addressEntities;

    @OneToOne(mappedBy = "person", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonIgnoreProperties("person")
    private Job jobEntity;

    @OneToMany(mappedBy = "person", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonIgnoreProperties("person")
    private Set<Vehicle> vehicles;

    @ElementCollection
    @CollectionTable(
            name = "characteristic_mapping",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "characteristic_name")
    @Column(name = "value")
    private Map<String, String> characteristics;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public Double getHeight() {
        return height;
    }

    public Double getWeight() {
        return weight;
    }

    public Set<String> getNickNames() {
        return nickNames;
    }

    public Person setNickNames(Set<String> nickNames) {
        this.nickNames = nickNames;
        return this;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public Person setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }

    public Set<Address> getAddressEntities() {
        return addressEntities;
    }

    public Person setAddressEntities(Set<Address> addressEntities) {
        this.addressEntities = addressEntities;
        return this;
    }

    public Job getJobEntity() {
        return jobEntity;
    }

    public Person setJobEntity(Job jobEntity) {
        this.jobEntity = jobEntity;
        return this;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public Person setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
        return this;
    }

    public Map<String, String> getCharacteristics() {
        return characteristics;
    }

    public Person setCharacteristics(Map<String, String> characteristics) {
        this.characteristics = characteristics;
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        Person person = (Person) object;

        if (!firstName.equals(person.firstName)) {
            return false;
        }
        if (!lastName.equals(person.lastName)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}