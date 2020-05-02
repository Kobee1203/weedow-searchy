package com.weedow.spring.data.search.example.repository

import com.weedow.spring.data.search.example.model.Person
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager

class PersonRepositoryImpl : SimpleJpaRepository<Person, Long>, PersonRepository {
    constructor(entityInformation: JpaEntityInformation<Person, *>, entityManager: EntityManager) : super(entityInformation, entityManager)
    constructor(domainClass: Class<Person>, em: EntityManager) : super(domainClass, em)
}