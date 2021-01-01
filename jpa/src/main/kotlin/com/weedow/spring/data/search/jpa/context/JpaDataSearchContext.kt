package com.weedow.spring.data.search.jpa.context

import com.weedow.spring.data.search.context.AbstractConfigurableDataSearchContext
import javax.persistence.*

/**
 * JPA [com.weedow.spring.data.search.context.DataSearchContext] implementation.
 */
class JpaDataSearchContext : AbstractConfigurableDataSearchContext() {

    companion object {
        /**
         * Entity Annotations supported
         */
        private val ENTITY_ANNOTATIONS = listOf(
            Entity::class.java
        )

        /**
         * Join Annotations supported.
         */
        private val JOIN_ANNOTATIONS = listOf(
            OneToOne::class.java,
            OneToMany::class.java,
            ManyToMany::class.java,
            ElementCollection::class.java,
            ManyToOne::class.java
        )
    }

    override val entityAnnotations: List<Class<out Annotation>>
        get() = ENTITY_ANNOTATIONS

    override val joinAnnotations: List<Class<out Annotation>>
        get() = JOIN_ANNOTATIONS

}