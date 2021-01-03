package com.weedow.spring.data.search.jpa.context

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.*

internal class JpaDataSearchContextTest {

    @Test
    fun getEntityAnnotations() {
        val entityAnnotations = JpaDataSearchContext().entityAnnotations

        assertThat(entityAnnotations).hasSize(1)
        assertThat(entityAnnotations).containsExactly(Entity::class.java)
    }

    @Test
    fun getJoinAnnotations() {
        val joinAnnotations = JpaDataSearchContext().joinAnnotations

        assertThat(joinAnnotations).hasSize(5)
        assertThat(joinAnnotations).containsExactly(
            OneToOne::class.java,
            OneToMany::class.java,
            ManyToMany::class.java,
            ElementCollection::class.java,
            ManyToOne::class.java
        )
    }
}