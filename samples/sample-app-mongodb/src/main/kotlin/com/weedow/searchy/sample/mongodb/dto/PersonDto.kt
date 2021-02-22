package com.weedow.searchy.sample.mongodb.dto

data class PersonDto(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val nickNames: Set<String>?,
    val phoneNumbers: Set<String>?,
    val addresses: Set<AddressDto>?,
    val vehicles: Set<VehicleDto>?
)