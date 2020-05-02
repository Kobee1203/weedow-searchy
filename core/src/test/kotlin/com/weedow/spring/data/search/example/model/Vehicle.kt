package com.weedow.spring.data.search.example.model

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

        @ManyToOne
        val person: Person

) : JpaPersistable<Long>() {


}

enum class VehicleType {
    CAR, MOTORBIKE, SCOOTER, VAN, TRUCK
}
