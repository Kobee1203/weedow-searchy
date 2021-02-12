package com.weedow.searchy.mongodb.domain

import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.LocalDateTime

/**
 * Abstract class that declares common fields (@[Id], @[CreatedDate], @[LastModifiedDate]), and simplify the creation of entities for MongoDB.
 */
abstract class MongoPersistable<ID : Serializable> : Persistable<ID> {

    @Transient
    @kotlin.jvm.Transient
    val idClass: Class<ID>

    @Id
    private var id: ID? = null

    @CreatedDate
    private var createdOn: LocalDateTime? = null

    @LastModifiedDate
    private var updatedOn: LocalDateTime? = null

    init {
        val t: Type = javaClass.genericSuperclass
        val pt: ParameterizedType = t as ParameterizedType
        idClass = pt.actualTypeArguments[0] as Class<ID>
    }

    override fun getId(): ID? {
        return id
    }

    open fun setId(id: ID) {
        this.id = id
    }

    fun getCreatedOn(): LocalDateTime? {
        return createdOn
    }

    fun getUpdatedOn(): LocalDateTime? {
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