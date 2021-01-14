package com.weedow.searchy.sample.java.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Vehicle extends JpaPersistable<Long> {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties({"vehicles"})
    private Person person;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "feature_mapping",
            joinColumns = {@JoinColumn(name = "vehicle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")})
    @MapKey(name = "name") // Feature name
    private Map<String, Feature> features;

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Vehicle setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public Vehicle setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public String getModel() {
        return model;
    }

    public Vehicle setModel(String model) {
        this.model = model;
        return this;
    }

    public Person getPerson() {
        return person;
    }

    public Vehicle setPerson(Person person) {
        this.person = person;
        return this;
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public Vehicle setFeatures(Map<String, Feature> features) {
        this.features = features;
        return this;
    }
}
