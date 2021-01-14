package com.weedow.searchy.sample.java.repository;

import com.weedow.searchy.sample.java.entity.Person;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(excerptProjection = PersonDto.class)
public interface PersonRepository extends Repository<Person, Long> {

    List<Person> findAll();

    List<Person> findByLastName(@Param("name") String name);

    List<Person> findByFirstNameAndLastName(@Param("firstname") String firstName, @Param("lastname") String lastName);
}
