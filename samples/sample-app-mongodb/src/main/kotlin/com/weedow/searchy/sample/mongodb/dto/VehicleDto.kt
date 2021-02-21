package com.weedow.searchy.sample.mongodb.dto

import com.weedow.searchy.sample.mongodb.model.VehicleType

data class VehicleDto(
    val vehicleType: VehicleType?,
    val brand: String?,
    val model: String?
)