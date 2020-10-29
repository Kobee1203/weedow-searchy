package com.weedow.spring.data.search.common.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.time.OffsetDateTime
import javax.persistence.*


@MappedSuperclass
abstract class JpaPersistable<ID : Serializable> : Persistable<ID> {

    companion object {
        private const val serialVersionUID = -1L
    }

    @Id
    @GeneratedValue
    private var id: ID? = null

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private var createdOn: OffsetDateTime? = null

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private var updatedOn: OffsetDateTime? = null

    @PrePersist
    fun prePersist() {
        createdOn = OffsetDateTime.now()
        updatedOn = OffsetDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        updatedOn = OffsetDateTime.now()
    }

    override fun getId(): ID? {
        return id
    }

    fun getCreatedOn(): OffsetDateTime? {
        return createdOn
    }

    fun getUpdatedOn(): OffsetDateTime? {
        return updatedOn
    }

    /**
     * Must be [Transient] in order to ensure that no JPA provider complains because of a missing setter.
     *
     * @see org.springframework.data.domain.Persistable.isNew
     */
    @Transient
    override fun isNew() = null == getId()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JpaPersistable<*>

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