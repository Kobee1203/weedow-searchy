package com.weedow.searchy.sample.mongodb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.weedow.searchy.mongodb.domain.MongoPersistable
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
    var email: String? = null,

    var birthday: LocalDateTime? = null,

    var age: Int? = null,

    var height: Double? = null,

    var weight: Double? = null,

    var sex: Sex? = null,

    var nickNames: Set<String>? = null,

    var phoneNumbers: Set<String>? = null,

    var mainAddress: Address? = null,

    var otherAddresses: Set<Address>? = null,

    @DBRef
    @Field("job")
    @JsonIgnoreProperties("person")
    var jobEntity: Job? = null,

    @DBRef(lazy = true)
    @JsonIgnoreProperties("person")
    var vehicles: Set<Vehicle>? = null,

    var characteristics: Map<String, String>? = null,

    @DBRef(lazy = true)
    var tasks: Map<Task, TaskTime>? = null,

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    var location: Point? = null

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