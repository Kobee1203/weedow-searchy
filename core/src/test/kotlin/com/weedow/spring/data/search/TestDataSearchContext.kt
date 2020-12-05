package com.weedow.spring.data.search

import com.weedow.spring.data.search.context.AbstractConfigurableDataSearchContext
import javax.persistence.*

class TestDataSearchContext : AbstractConfigurableDataSearchContext() {

    companion object {
        /**
         * Entity Annotations supported
         */
        val ENTITY_ANNOTATIONS = listOf(
                Entity::class.java
        )

        /**
         * Join Annotations supported.
         */
        val JOIN_ANNOTATIONS = listOf(
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