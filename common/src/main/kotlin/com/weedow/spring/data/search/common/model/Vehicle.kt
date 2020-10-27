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
        val person: Person,

        @OneToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "feature_mapping",
                joinColumns = [JoinColumn(name = "vehicle_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "feature_id", referencedColumnName = "id")])
        @MapKey(name = "name") // Feature name
        val features: Map<String, Feature>? = null

) : JpaPersistable<Long>()

enum class VehicleType {
    CAR, MOTORBIKE, SCOOTER, VAN, TRUCK
}
