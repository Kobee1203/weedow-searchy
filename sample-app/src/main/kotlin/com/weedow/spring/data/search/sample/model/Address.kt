package com.weedow.spring.data.search.sample.model

import com.neovisionaries.i18n.CountryCode
import javax.persistence.*

@Entity
class Address(
        @Column(nullable = false)
        val street: String,

        @Column(nullable = false)
        val city: String,

        @Column(nullable = false)
        val zipCode: String,

        @Enumerated(EnumType.STRING)
        val country: CountryCode,

        @ManyToMany(mappedBy = "addressEntities")
        val persons: Set<Person>

) : JpaPersistable<Long>() {
}