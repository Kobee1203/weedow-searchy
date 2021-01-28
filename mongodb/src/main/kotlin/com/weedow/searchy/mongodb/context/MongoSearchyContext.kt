package com.weedow.searchy.mongodb.context

import com.weedow.searchy.context.AbstractConfigurableSearchyContext
import org.springframework.data.annotation.Persistent
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
            Document::class.java,
            Persistent::class.java
        )

        /**
         * Join Annotations supported.
         */
        private val JOIN_ANNOTATIONS = listOf(
            DBRef::class.java
        )
    }

    override val entityAnnotations: List<Class<out Annotation>> = ENTITY_ANNOTATIONS

    override val joinAnnotations: List<Class<out Annotation>> = JOIN_ANNOTATIONS

    override val isUnknownAsEmbedded: Boolean = true

}