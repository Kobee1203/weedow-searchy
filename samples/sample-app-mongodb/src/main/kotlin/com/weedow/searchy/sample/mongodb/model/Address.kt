package com.weedow.searchy.sample.mongodb.model

import com.neovisionaries.i18n.CountryCode

data class Address(
    val street: String,
    val zipCode: String,
    val country: CountryCode,
    val city: City
)