package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime

@Document
class Job(
    val active: Boolean,

    val title: String,

    val company: String,

    val salary: Int,

    val hireDate: OffsetDateTime,

    @JsonIgnoreProperties("jobEntity")
    val person: Person

) : MongoPersistable<Long>()