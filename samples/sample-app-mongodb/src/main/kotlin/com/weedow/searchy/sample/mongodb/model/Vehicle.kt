package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Vehicle(
    val vehicleType: VehicleType,

    val brand: String,

    val model: String,

    @DBRef(lazy = true)
    @JsonIgnoreProperties("vehicles")
    val person: Person,

    val features: Map<String, Feature>? = null

) : MongoPersistable<Long>()

enum class VehicleType {
    CAR, MOTORBIKE, SCOOTER, VAN, TRUCK
}
