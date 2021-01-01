package com.weedow.spring.data.search.jpa.query.specification

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.types.EntityPath
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.jpa.query.JpaQueryBuilder
import com.weedow.spring.data.search.query.querytype.QEntity
import com.weedow.spring.data.search.query.specification.Specification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.repository.support.CrudMethodMetadata
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.querydsl.EntityPathResolver
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.LockModeType
import javax.persistence.Query

@ExtendWith(MockitoExtension::class)
internal class JpaSpecificationExecutorTest {

    @Mock
    private lateinit var dataSearchContext: DataSearchContext

    @Mock
    private lateinit var entityInformation: JpaEntityInformation<Any, Long>

    @Mock
    private lateinit var entityManager: EntityManager

    @Mock
    private lateinit var resolver: EntityPathResolver

    private lateinit var resultList: List<Any?>
    private lateinit var query: Query

    private val entityPathType = QEntity::class.java

    @BeforeEach
    fun setUp() {
        val entityType = Any::class.java
        whenever(entityInformation.javaType).thenReturn(entityType)

        val entityPath = mock<EntityPath<Any>> {
            on { this.type }.thenReturn(entityPathType)
            on { this.metadata }.thenReturn(mock())
        }
        whenever(entityPath.accept<Any?, Any?>(anyOrNull(), anyOrNull())).thenReturn(entityPath)

        whenever(resolver.createPath(entityType)).thenReturn(entityPath)

        val delegate = mock<EntityManager>()
        whenever(entityManager.delegate).thenReturn(delegate)

        val entityManagerFactory = mock<EntityManagerFactory> { on { this.properties }.thenReturn(emptyMap<String, Any>()) }
        whenever(entityManager.entityManagerFactory).thenReturn(entityManagerFactory)

        resultList = mock()

        query = mock {
            on { this.parameters }.thenReturn(emptySet())
            on { this.resultList }.thenReturn(resultList)
        }

        whenever(entityManager.createQuery(anyString())).thenReturn(query)
    }

    @Test
    fun find_all() {
        val qEntity = mock<QEntity<QEntity<*>>>()
        whenever(dataSearchContext.get(entityPathType)).thenReturn(qEntity)

        val specification = mock<Specification<Any>> {
            on { this.toPredicate(any<JpaQueryBuilder<Any>>()) }.thenReturn(mock())
        }
        val metadata = null

        val jpaQueryDslSpecificationExecutor =
            JpaSpecificationExecutor(dataSearchContext, entityInformation, entityManager, resolver, metadata)
        val result = jpaQueryDslSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)
        verifyNoMoreInteractions(query)
    }

    @Test
    fun find_all_with_no_predicate() {
        val qEntity = mock<QEntity<QEntity<*>>>()
        whenever(dataSearchContext.get(entityPathType)).thenReturn(qEntity)

        val specification = mock<Specification<Any>> {
            on { this.toPredicate(any<JpaQueryBuilder<Any>>()) }.thenReturn(Specification.NO_PREDICATE)
        }
        val metadata = mock<CrudMethodMetadata>()

        val jpaQueryDslSpecificationExecutor =
            JpaSpecificationExecutor(dataSearchContext, entityInformation, entityManager, resolver, metadata)
        val result = jpaQueryDslSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)
        verifyNoMoreInteractions(query)
    }

    @Test
    fun find_all_with_null_specification() {
        val specification = null
        val metadata = null

        val jpaQueryDslSpecificationExecutor =
            JpaSpecificationExecutor(dataSearchContext, entityInformation, entityManager, resolver, metadata)
        val result = jpaQueryDslSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)

        verifyZeroInteractions(dataSearchContext)
        verifyNoMoreInteractions(query)
    }

    @Test
    fun find_all_with_metadata() {
        val qEntity = mock<QEntity<QEntity<*>>>()
        whenever(dataSearchContext.get(entityPathType)).thenReturn(qEntity)

        val specification = mock<Specification<Any>> {
            on { this.toPredicate(any<JpaQueryBuilder<Any>>()) }.thenReturn(mock())
        }
        val metadata = mock<CrudMethodMetadata>()

        val jpaQueryDslSpecificationExecutor =
            JpaSpecificationExecutor(dataSearchContext, entityInformation, entityManager, resolver, metadata)
        val result = jpaQueryDslSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)
        verifyNoMoreInteractions(query)
    }

    @Test
    fun find_all_with_metadata_and_lock_mode_type() {
        val qEntity = mock<QEntity<QEntity<*>>>()
        whenever(dataSearchContext.get(entityPathType)).thenReturn(qEntity)

        val specification = mock<Specification<Any>> {
            on { this.toPredicate(any<JpaQueryBuilder<Any>>()) }.thenReturn(mock())
        }

        val lockModeType = LockModeType.READ
        val metadata = mock<CrudMethodMetadata> {
            on { this.lockModeType }.thenReturn(lockModeType)
        }

        val jpaQueryDslSpecificationExecutor =
            JpaSpecificationExecutor(dataSearchContext, entityInformation, entityManager, resolver, metadata)
        val result = jpaQueryDslSpecificationExecutor.findAll(specification)

        assertThat(result).isSameAs(resultList)

        verify(query).lockMode = lockModeType
        verifyNoMoreInteractions(query)
    }

}