package com.weedow.searchy.mongodb.query.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.dsl.PathBuilder
import com.weedow.searchy.context.SearchyContext
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty

@ExtendWith(MockitoExtension::class)
internal class MongoSpecificationExecutorFactoryTest {

    @Mock
    private lateinit var mongoOperations: MongoOperations

    @Mock
    private lateinit var searchyContext: SearchyContext

    @InjectMocks
    private lateinit var mongoSpecificationExecutorFactory: MongoSpecificationExecutorFactory

    @ParameterizedTest
    @ValueSource(classes = [EntityWithSpringId::class, EntityWithIdField::class, EntityWithUnknownId::class])
    fun get_specification_executor(entityClass: Class<*>) {
        verify(entityClass)
    }

    private fun <T> verify(entityClass: Class<T>) {
        val persistentEntity = mock<MongoPersistentEntity<*>> {
            on { this.type }.thenReturn(entityClass)
        }
        val mappingContext = mock<MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty>> {
            on { this.getRequiredPersistentEntity(entityClass) }.thenReturn(persistentEntity)
        }
        val converter = mock<MongoConverter> {
            on { this.mappingContext }.thenReturn(mappingContext)
        }
        whenever(mongoOperations.converter).thenReturn(converter)

        val specificationExecutor = mongoSpecificationExecutorFactory.getSpecificationExecutor(entityClass)

        Assertions.assertThat(specificationExecutor)
            .isInstanceOf(MongoSpecificationExecutor::class.java)
            .extracting("searchyContext", "mongoOperations")
            .contains(searchyContext, mongoOperations)
        Assertions.assertThat(specificationExecutor).extracting("path").isInstanceOf(PathBuilder::class.java)
        Assertions.assertThat(specificationExecutor).extracting("path.type").isEqualTo(entityClass)
    }

    class EntityWithSpringId(
        @Id
        private val myId: String
    )

    class EntityWithIdField(
        private val id: Double
    )

    class EntityWithUnknownId(
        private val unknownId: Long
    )

}