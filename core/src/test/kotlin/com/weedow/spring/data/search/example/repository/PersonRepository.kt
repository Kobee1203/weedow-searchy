package com.weedow.spring.data.search.example.repository

import com.weedow.spring.data.search.example.model.Person
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface PersonRepository : JpaSpecificationExecutor<Person>