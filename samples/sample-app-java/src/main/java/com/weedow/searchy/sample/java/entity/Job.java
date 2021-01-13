package com.weedow.searchy.sample.java.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.OffsetDateTime;

@Entity
public class Job extends JpaPersistable<Long> {

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private int salary;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime hireDate;

    @OneToOne(optional = false)
    @JsonIgnoreProperties({"jobEntity"})
    private Person person;

    public boolean isActive() {
        return active;
    }

    public Job setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Job setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCompany() {
        return company;
    }

    public Job setCompany(String company) {
        this.company = company;
        return this;
    }

    public int getSalary() {
        return salary;
    }

    public Job setSalary(int salary) {
        this.salary = salary;
        return this;
    }

    public OffsetDateTime getHireDate() {
        return hireDate;
    }

    public Job setHireDate(OffsetDateTime hireDate) {
        this.hireDate = hireDate;
        return this;
    }

    public Person getPerson() {
        return person;
    }

    public Job setPerson(Person person) {
        this.person = person;
        return this;
    }
}
