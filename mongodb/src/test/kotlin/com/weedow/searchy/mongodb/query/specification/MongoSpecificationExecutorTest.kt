package com.weedow.searchy.mongodb.query.specification

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.EntityPath
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.query.MongoQueryBuilder
import com.weedow.searchy.query.querytype.QEntity
import com.weedow.searchy.query.specification.Specification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.core.ExecutableFindOperation
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.querydsl.EntityPathResolver

@ExtendWith(MockitoExtension::class)
internal class MongoSpecificationExecutorTest {

    @Mock
    private lateinit var searchyContext: SearchyContext

    @Mock
    private lateinit var entityInformation: MongoEntityInformation<Any, *>

    @Mock
    private lateinit var mongoOperations: MongoOperations

    @Mock
    private lateinit var resolver: EntityPathResolver

    private lateinit var mongoSpecificationExecutor: MongoSpecificationExecutor<Any>

    private val entityPath = mock<EntityPath<Any>>()
    private val entityPathType = QEntity::class.java

    private lateinit var resultList: List<Any?>

    @BeforeEach
    fun setUp() {
        val entityType = Any::class.java
        whenever(entityInformation.javaType).thenReturn(entityType)

        whenever(resolver.createPath(entityType)).thenReturn(entityPath)

        val converter = mock<MongoConverter> {
            val mappingContext = mock<MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty>>()
            on { this.mappingContext }.thenReturn(mappingContext)
        }
        whenever(mongoOperations.converter).thenReturn(converter)
        whenever(mongoOperations.getCollectionName(entityType)).thenReturn("collection_name")

        resultList = mock()

        val query = mock<ExecutableFindOperation.ExecutableFind<Any>> {
            val terminatingFind = mock<ExecutableFindOperation.TerminatingFind<Any>> {
                on { this.all() }.thenReturn(resultList)
            }
            val findWithProjection = mock<ExecutableFindOperation.FindWithProjection<Any>> {
                on { this.matching(any<BasicQuery>()) }.thenReturn(terminatingFind)
            }
            on { this.inCollection("collection_name") }.thenReturn(findWithProjection)
        }
        whenever(mongoOperations.query(entityType)).thenReturn(query)

        mongoSpecificationExecutor = MongoSpecificationExecutor(searchyContext, entityInformation, mongoOperations, resolver)
    }

    @Test
    fun find_all() {
        whenever(entityPath.type).thenReturn(entityPathType)

        val qEntity = mock<QEntity<QEntity<*>>>()
        whenever(searchyContext.get(entityPathType)).thenReturn(qEntity)

        val specification = mock<Specification<Any>> {
            on { this.toPredicate(any<MongoQueryBuilder<Any>>()) }.thenReturn(mock())
        }

        val result = mongoSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)
    }

    @Test
    fun find_all_with_no_predicate() {
        whenever(entityPath.type).thenReturn(entityPathType)

        val qEntity = mock<QEntity<QEntity<*>>>()
        whenever(searchyContext.get(entityPathType)).thenReturn(qEntity)

        val specification = mock<Specification<Any>> {
            on { this.toPredicate(any<MongoQueryBuilder<Any>>()) }.thenReturn(Specification.NO_PREDICATE)
        }

        val result = mongoSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)
    }

    @Test
    fun find_all_with_null_specification() {
        val specification = null

        val result = mongoSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)

        verifyZeroInteractions(searchyContext)
    }

}