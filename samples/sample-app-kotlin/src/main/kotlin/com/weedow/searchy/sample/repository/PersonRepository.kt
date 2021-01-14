package com.weedow.searchy.sample.repository

import com.weedow.searchy.common.model.Person
import com.weedow.searchy.repository.SearchyBaseRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : SearchyBaseRepository<Person, Long>