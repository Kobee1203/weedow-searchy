package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigInteger
import java.time.OffsetDateTime

@Document
class Job(
    val active: Boolean,

    val title: String,

    val company: String,

    val salary: Int,

    val hireDate: OffsetDateTime,

    @DBRef(lazy = true)
    @JsonIgnoreProperties("jobEntity")
    val person: Person

) : MongoPersistable<BigInteger>()