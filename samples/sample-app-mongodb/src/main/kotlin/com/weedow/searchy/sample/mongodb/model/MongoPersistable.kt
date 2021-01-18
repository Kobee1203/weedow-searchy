package com.weedow.searchy.sample.mongodb.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.time.OffsetDateTime

abstract class MongoPersistable<ID : Serializable> : Persistable<ID> {

    @Id
    private var id: ID? = null

    @CreatedDate
    private var createdOn: OffsetDateTime? = null

    @LastModifiedDate
    private var updatedOn: OffsetDateTime? = null

    override fun getId(): ID? {
        return id
    }

    fun getCreatedOn(): OffsetDateTime? {
        return createdOn
    }

    fun getUpdatedOn(): OffsetDateTime? {
        return updatedOn
    }

    @Transient
    override fun isNew() = null == getId()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MongoPersistable<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

}