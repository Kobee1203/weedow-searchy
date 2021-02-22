package com.weedow.searchy.sample.mongodb.repository

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor


data class PersonAggregate @PersistenceConstructor constructor(
    @Id
    val lastname: String,
    val names: Collection<String>
) {

    constructor(lastname: String, name: String) : this(lastname, listOf(name))
}