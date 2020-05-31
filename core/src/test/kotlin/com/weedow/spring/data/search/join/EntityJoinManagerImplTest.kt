package com.weedow.spring.data.search.join

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.*
import javax.persistence.criteria.JoinType

@ExtendWith(MockitoExtension::class)
internal class EntityJoinManagerImplTest {

    @Test
    fun compute_entity_without_joins() {
        val searchDescriptor = mock<SearchDescriptor<EntityWithNoJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(EntityWithNoJoins::class.java)
            on { this.entityJoinHandlers }.doReturn(emptyList())
        }

        val entityJoinManager = EntityJoinManagerImpl()
        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull()
        assertThat(entityJoins.getJoins()).isEmpty()
    }

    @Test
    fun compute_entity_with_one_join() {
        val searchDescriptor = mock<SearchDescriptor<EntityWithJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(EntityWithJoins::class.java)
            on { this.entityJoinHandlers }.doReturn(emptyList())
        }

        val entityJoinManager = EntityJoinManagerImpl()
        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull()
        assertThat(entityJoins.getJoins()).hasSize(1)

        val joinName = EntityWithJoins.OtherEntity::class.java.canonicalName
        assertThat(entityJoins.getJoins().keys)
                .containsExactly(joinName)

        assertThat(entityJoins.getJoins().values)
                .containsExactly(
                        EntityJoin("myJoin", joinName)
                )
    }

    @Test
    fun compute_entity_with_multiple_joins() {
        val searchDescriptor = mock<SearchDescriptor<EntityWithMultipleJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(EntityWithMultipleJoins::class.java)
            on { this.entityJoinHandlers }.doReturn(listOf(EntityWithMultipleJoins().MyEntityJoinHandler()))
        }

        val entityJoinManager = EntityJoinManagerImpl()
        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull()
        assertThat(entityJoins.getJoins()).hasSize(2)

        val joinName1 = EntityWithMultipleJoins.OtherEntity::class.java.canonicalName
        val joinName2 = EntityWithMultipleJoins.OtherEntity::class.java.canonicalName + "." + "myJoin2"

        assertThat(entityJoins.getJoins().keys)
                .containsExactly(joinName1, joinName2)

        assertThat(entityJoins.getJoins().values)
                .containsExactlyInAnyOrder(
                        EntityJoin("myJoin1", joinName1),
                        EntityJoin("myJoin1.myJoin2", joinName2, JoinType.LEFT, true)
                )
    }

    @Test
    fun compute_entity_by_skipping_entity_already_processed_or_root_entity() {
        val searchDescriptor = mock<SearchDescriptor<EntityWithBidirectionalJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(EntityWithBidirectionalJoins::class.java)
            on { this.entityJoinHandlers }.doReturn(emptyList())
        }

        val entityJoinManager = EntityJoinManagerImpl()
        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull()
        assertThat(entityJoins.getJoins()).hasSize(2)

        val joinName1 = EntityWithBidirectionalJoins.MyEntity1::class.java.canonicalName
        val joinName2 = EntityWithBidirectionalJoins.MyEntity2::class.java.canonicalName

        assertThat(entityJoins.getJoins().keys)
                .containsExactly(joinName1, joinName2)

        assertThat(entityJoins.getJoins().values)
                .containsExactlyInAnyOrder(
                        EntityJoin("myJoin1", joinName1),
                        EntityJoin("myJoin2", joinName2)
                )
    }

    internal data class EntityWithNoJoins(
            @Column(nullable = false)
            val firstName: String = "",

            @Column(nullable = false)
            val lastName: String = ""
    )

    internal data class EntityWithJoins(
            @Column(nullable = false)
            val firstName: String = "",

            @OneToMany
            val myJoin: OtherEntity? = null
    ) {
        inner class OtherEntity(
                @Column(nullable = false)
                val id: String = ""
        )
    }

    internal data class EntityWithMultipleJoins(
            @Column(nullable = false)
            val firstName: String = "",

            @OneToMany
            val myJoin1: OtherEntity? = null
    ) {
        inner class OtherEntity(
                @Column(nullable = false)
                val id: String = "",

                @ElementCollection
                val myJoin2: Set<String>
        )

        inner class MyEntityJoinHandler : EntityJoinHandler<EntityWithMultipleJoins> {
            override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
                return fieldName == "myJoin2"
            }

            override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
                return JoinInfo(JoinType.LEFT, true)
            }
        }
    }

    internal data class EntityWithBidirectionalJoins(
            @Column(nullable = false)
            val firstName: String = "",

            @OneToMany
            val myJoin1: MyEntity1? = null,

            @OneToMany
            val myJoin2: MyEntity2? = null
    ) {
        inner class MyEntity1(
                @Column(nullable = false)
                val id: String = "",

                @ManyToOne
                val parent: EntityWithBidirectionalJoins? = null
        )

        inner class MyEntity2(
                @Column(nullable = false)
                val id: String = "",

                @OneToOne
                val myEntity1: MyEntity1? = null
        )
    }

}