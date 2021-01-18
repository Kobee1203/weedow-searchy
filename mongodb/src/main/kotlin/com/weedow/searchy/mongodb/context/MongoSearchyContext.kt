package com.weedow.searchy.mongodb.context

import com.weedow.searchy.context.AbstractConfigurableSearchyContext
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 * JPA [com.weedow.searchy.context.SearchyContext] implementation.
 */
class MongoSearchyContext : AbstractConfigurableSearchyContext() {

    companion object {
        /**
         * Entity Annotations supported
         */
        private val ENTITY_ANNOTATIONS = listOf(
            Document::class.java
        )

        /**
         * Join Annotations supported.
         */
        private val JOIN_ANNOTATIONS = listOf(
            DBRef::class.java
        )
    }

    override val entityAnnotations: List<Class<out Annotation>>
        get() = ENTITY_ANNOTATIONS

    override val joinAnnotations: List<Class<out Annotation>>
        get() = JOIN_ANNOTATIONS

}