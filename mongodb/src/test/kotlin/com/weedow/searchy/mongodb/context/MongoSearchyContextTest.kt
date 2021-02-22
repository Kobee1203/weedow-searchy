package com.weedow.searchy.mongodb.context

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Persistent
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

internal class MongoSearchyContextTest {

    @Test
    fun getEntityAnnotations() {
        val entityAnnotations = MongoSearchyContext().entityAnnotations

        assertThat(entityAnnotations).hasSize(2)
        assertThat(entityAnnotations).containsExactly(Document::class.java, Persistent::class.java)
    }

    @Test
    fun getJoinAnnotations() {
        val joinAnnotations = MongoSearchyContext().joinAnnotations

        assertThat(joinAnnotations).hasSize(1)
        assertThat(joinAnnotations).containsExactly(DBRef::class.java)
    }

    @Test
    fun isUnknownAsEmbedded() {
        assertThat(MongoSearchyContext().isUnknownAsEmbedded).isTrue
    }

}