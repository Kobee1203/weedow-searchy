package com.weedow.spring.data.search.example.model

import org.springframework.data.domain.Persistable
import java.io.Serializable
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

@MappedSuperclass
abstract class JpaPersistable<T : Serializable> : Persistable<T>
{

    companion object {
        private val serialVersionUID = -1L
    }

    @Id
    @GeneratedValue
    private var id: T? = null

    override fun getId(): T? {
        return id
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
        return "Identifier(id=$id)"
    }

}