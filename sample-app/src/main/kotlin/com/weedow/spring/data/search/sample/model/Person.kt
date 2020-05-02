package com.weedow.spring.data.search.sample.model

import javax.persistence.*

@Entity
class Person(
        @Column(nullable = false)
        val firstName: String,

        @Column(nullable = false)
        val lastName: String,

        @Column(unique = true, length = 100)
        val email: String? = null,

        @ElementCollection
        var nickNames: Set<String>? = null,

        @ElementCollection
        @CollectionTable(name = "person_phone_numbers", joinColumns = [JoinColumn(name = "person_id")])
        @Column(name = "phone_number")
        var phoneNumbers: Set<String>? = null,

        @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinTable(
                name = "person_address",
                joinColumns = [JoinColumn(name = "personId")],
                inverseJoinColumns = [JoinColumn(name = "addressId")])
        val addressEntities: Set<Address>? = null,

        @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        val jobEntity: Job? = null,

        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        val vehicles: Set<Vehicle>? = null

) : JpaPersistable<Long>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        return result
    }
}