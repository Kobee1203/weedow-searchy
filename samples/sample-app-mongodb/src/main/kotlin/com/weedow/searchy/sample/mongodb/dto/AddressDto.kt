package com.weedow.searchy.sample.mongodb.dto

import com.neovisionaries.i18n.CountryCode

data class AddressDto(
    val street: String?,
    val city: String?,
    val zipCode: String?,
    val country: String?
)