package com.weedow.searchy

import com.weedow.searchy.context.AbstractConfigurableSearchyContext
import javax.persistence.*

class TestSearchyContext(override val isUnknownAsEmbedded: Boolean = false) : AbstractConfigurableSearchyContext() {

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

    override val entityAnnotations: List<Class<out Annotation>> = ENTITY_ANNOTATIONS

    override val joinAnnotations: List<Class<out Annotation>> = JOIN_ANNOTATIONS

}