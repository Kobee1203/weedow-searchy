package com.weedow.spring.data.search.example

import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.common.repository.PersonRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager

class PersonRepositoryImpl(
    domainClass: Class<Person>,
    em: EntityManager
) : SimpleJpaRepository<Person, Long>(domainClass, em), PersonRepository