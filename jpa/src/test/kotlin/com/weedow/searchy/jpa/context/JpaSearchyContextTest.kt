package com.weedow.searchy.jpa.context

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.*

internal class JpaSearchyContextTest {

    @Test
    fun getEntityAnnotations() {
        val entityAnnotations = JpaSearchyContext().entityAnnotations

        assertThat(entityAnnotations).hasSize(1)
        assertThat(entityAnnotations).containsExactly(Entity::class.java)
    }

    @Test
    fun getJoinAnnotations() {
        val joinAnnotations = JpaSearchyContext().joinAnnotations

        assertThat(joinAnnotations).hasSize(5)
        assertThat(joinAnnotations).containsExactly(
            OneToOne::class.java,
            OneToMany::class.java,
            ManyToMany::class.java,
            ElementCollection::class.java,
            ManyToOne::class.java
        )
    }

    @Test
    fun isUnknownAsEmbedded() {
        assertThat(JpaSearchyContext().isUnknownAsEmbedded).isFalse
    }
}