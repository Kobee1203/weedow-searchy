package com.weedow.searchy.common.dto

import com.weedow.searchy.common.model.VehicleType

data class VehicleDto internal constructor(
    val vehicleType: VehicleType?,
    val brand: String?,
    val model: String?
) {

    data class Builder(
        private var vehicleType: VehicleType? = null,
        private var brand: String? = null,
        private var model: String? = null
    ) {
        fun vehicleType(vehicleType: VehicleType) = apply { this.vehicleType = vehicleType }
        fun brand(brand: String) = apply { this.brand = brand }
        fun model(model: String) = apply { this.model = model }
        fun build() = VehicleDto(vehicleType, brand, model)
    }
}