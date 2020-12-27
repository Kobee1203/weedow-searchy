package com.weedow.spring.data.search.sample.repository

import com.weedow.spring.data.search.common.model.Person
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : DataSearchBaseRepository<Person, Long>