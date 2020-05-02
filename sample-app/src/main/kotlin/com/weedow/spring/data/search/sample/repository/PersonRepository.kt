package com.weedow.spring.data.search.sample.repository

import com.weedow.spring.data.search.sample.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long>, JpaSpecificationExecutor<Person>