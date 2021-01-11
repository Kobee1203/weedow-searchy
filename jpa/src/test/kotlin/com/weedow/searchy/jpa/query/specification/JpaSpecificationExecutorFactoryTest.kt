package com.weedow.searchy.jpa.query.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.dsl.PathBuilder
import com.weedow.searchy.context.SearchyContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.EntityManager
import javax.persistence.metamodel.IdentifiableType
import javax.persistence.metamodel.Metamodel

@ExtendWith(MockitoExtension::class)
internal class JpaSpecificationExecutorFactoryTest {

    @Mock
    private lateinit var entityManager: EntityManager

    @Mock
    private lateinit var searchyContext: SearchyContext

    @InjectMocks
    private lateinit var jpaSpecificationExecutorFactory: JpaSpecificationExecutorFactory

    @ParameterizedTest
    @ValueSource(classes = [EntityWithIdClass::class, EntityWithEmbeddedId::class, EntityWithId::class, EntityWithSpringId::class, EntityWithIdField::class, EntityWithUnknownId::class])
    fun get_specification_executor(entityClass: Class<*>) {
        verify(entityClass)
    }

    private fun <T> verify(entityClass: Class<T>) {
        val delegate = mock<EntityManager>()
        whenever(entityManager.delegate).thenReturn(delegate)

        val identifiableType = mock<IdentifiableType<T>>()
        val metamodel = mock<Metamodel> {
            on { this.managedType(entityClass) }.thenReturn(identifiableType)
        }
        whenever(entityManager.metamodel).thenReturn(metamodel)

        val specificationExecutor = jpaSpecificationExecutorFactory.getSpecificationExecutor(entityClass)

        assertThat(specificationExecutor)
            .isInstanceOf(JpaSpecificationExecutor::class.java)
            .extracting("searchyContext", "entityManager", "metadata")
            .contains(searchyContext, entityManager, null)
        assertThat(specificationExecutor).extracting("path").isInstanceOf(PathBuilder::class.java)
        assertThat(specificationExecutor).extracting("path.type").isEqualTo(entityClass)
    }

    @javax.persistence.IdClass(EntityWithIdClass.MyPK::class)
    class EntityWithIdClass(
        @javax.persistence.Id
        private val id1: Long,

        @javax.persistence.Id
        private val id2: String
    ) {
        class MyPK(
            private val id1: Long,
            private val id2: String
        )
    }

    class EntityWithEmbeddedId(
        @javax.persistence.EmbeddedId
        private val id1: MyPK
    ) {
        class MyPK(
            private val id1: Long,
            private val id2: String
        )
    }

    class EntityWithId(
        @javax.persistence.Id
        private val id: Double
    )

    class EntityWithSpringId(
        @org.springframework.data.annotation.Id
        private val myId: String
    )

    class EntityWithIdField(
        private val id: Double
    )

    class EntityWithUnknownId(
        private val unknownId: Long
    )

}