package com.weedow.spring.data.search.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
class Vehicle(
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        val vehicleType: VehicleType,

        @Column(nullable = false)
        val brand: String,

        @Column(nullable = false)
        val model: String,

        @ManyToOne(optional = false)
        @JsonIgnoreProperties("vehicles")
        val person: Person

) : JpaPersistable<Long>() {

}

enum class VehicleType {
    CAR, MOTORBIKE, SCOOTER, VAN, TRUCK
}
