package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Vehicle(
    val vehicleType: VehicleType,

    val brand: String,

    val model: String,

    @DBRef
    @JsonIgnoreProperties("vehicles")
    val person: Person,

    val features: Map<String, Feature>? = null

) : MongoPersistable<Long>()

enum class VehicleType {
    CAR, MOTORBIKE, SCOOTER, VAN, TRUCK
}
