package com.weedow.spring.data.search.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Person(
        @Column(nullable = false)
        val firstName: String,

        @Column(nullable = false)
        val lastName: String,

        @Column(unique = true, length = 100)
        val email: String? = null,

        @Column
        val birthday: LocalDateTime? = null,

        @Column
        val height: Double? = null,

        @Column
        val weight: Double? = null,

        @ElementCollection(fetch = FetchType.EAGER)
        var nickNames: Set<String>? = null,

        @ElementCollection
        @CollectionTable(name = "person_phone_numbers", joinColumns = [JoinColumn(name = "person_id")])
        @Column(name = "phone_number")
        var phoneNumbers: Set<String>? = null,

        @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinTable(
                name = "person_address",
                joinColumns = [JoinColumn(name = "person_id")],
                inverseJoinColumns = [JoinColumn(name = "address_id")])
        @JsonIgnoreProperties("persons")
        val addressEntities: Set<Address>? = null,

        @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        @JsonIgnoreProperties("person")
        val jobEntity: Job? = null,

        @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
        @JsonIgnoreProperties("person")
        val vehicles: Set<Vehicle>? = null,

        @ElementCollection
        @CollectionTable(
                name = "characteristic_mapping",
                joinColumns = [JoinColumn(name = "person_id", referencedColumnName = "id")])
        @MapKeyColumn(name = "characteristic_name")
        @Column(name = "value")
        val characteristics: Map<String, String>? = null,

        @ElementCollection
        @CollectionTable(
                name = "person_tasks",
                joinColumns = [JoinColumn(name = "person_id", referencedColumnName = "id")])
        @MapKeyJoinColumn(name = "task_id")
        @Column(name = "task_date")
        val tasks: Map<Task, LocalDateTime>? = null,

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