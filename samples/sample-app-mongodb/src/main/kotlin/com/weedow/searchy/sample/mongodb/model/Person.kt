package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.bson.types.ObjectId
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document
class Person(
    @Indexed
    val firstName: String,

    @Indexed
    val lastName: String,

    @Indexed(unique = true)
    val email: String? = null,

    val birthday: LocalDateTime? = null,

    val age: Int? = null,

    val height: Double? = null,

    val weight: Double? = null,

    val sex: Sex? = null,

    var nickNames: Set<String>? = null,

    var phoneNumbers: Set<String>? = null,

    @DBRef(lazy = true)
    @JsonIgnoreProperties("persons")
    val addressEntities: Set<Address>? = null,

    @DBRef
    @Field("job")
    @JsonIgnoreProperties("person")
    val jobEntity: Job? = null,

    @DBRef(lazy = true)
    @JsonIgnoreProperties("person")
    val vehicles: Set<Vehicle>? = null,

    val characteristics: Map<String, String>? = null,

    //@JsonIgnore
    @DBRef(lazy = true)
    val tasks: Map<Task, LocalDateTime>? = null,

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val location: Point? = null

) : MongoPersistable<ObjectId>() {

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

enum class Sex {
    MALE, FEMALE
}