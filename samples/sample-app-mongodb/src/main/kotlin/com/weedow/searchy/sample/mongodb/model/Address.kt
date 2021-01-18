package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.neovisionaries.i18n.CountryCode
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Address(
    val street: String,

    val city: String,

    val zipCode: String,

    val country: CountryCode,

    @DBRef(lazy = true)
    @JsonIgnoreProperties("addressEntities")
    val persons: Set<Person>

) : MongoPersistable<String>()