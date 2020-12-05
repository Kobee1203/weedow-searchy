package com.weedow.spring.data.search.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationService
import com.weedow.spring.data.search.specification.JpaSpecificationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class EntitySearchServiceImplTest {

    @Mock
    lateinit var jpaSpecificationService: JpaSpecificationService

    @Mock
    lateinit var entityJoinManager: EntityJoinManager

    @Mock
    lateinit var queryDslSpecificationService: QueryDslSpecificationService

    @Mock
    lateinit var queryDslSpecificationExecutorFactory: QueryDslSpecificationExecutorFactory

    @InjectMocks
    lateinit var entitySearchService: EntitySearchServiceImpl

    @Test
    fun find_all() {
        val rootExpression = mock<RootExpression<Person>>()

        val queryDslSpecificationExecutor = mock<QueryDslSpecificationExecutor<Person>>()

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.queryDslSpecificationExecutor }.doReturn(queryDslSpecificationExecutor)
        }

        val specification = mock<QueryDslSpecification<Person>>()
        whenever(queryDslSpecificationService.createSpecification(rootExpression, searchDescriptor)).thenReturn(specification)

        val person = Person("John", "Doe")
        whenever(queryDslSpecificationExecutor.findAll(specification)).thenReturn(listOf(person))

        val result = entitySearchService.findAll(rootExpression, searchDescriptor)

        assertThat(result).containsExactly(person)

        verifyZeroInteractions(queryDslSpecificationExecutorFactory)

        verifyZeroInteractions(jpaSpecificationService)
        verifyZeroInteractions(entityJoinManager)
    }

    @Test
    fun find_all_when_queryDslSpecificationExecutor_is_null() {
        val entityClass = Person::class.java

        val rootExpression = mock<RootExpression<Person>>()

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.thenReturn(entityClass)
        }

        val specification = mock<QueryDslSpecification<Person>>()
        whenever(queryDslSpecificationService.createSpecification(rootExpression, searchDescriptor)).thenReturn(specification)

        val queryDslSpecificationExecutor = mock<QueryDslSpecificationExecutor<Person>>()

        whenever(queryDslSpecificationExecutorFactory.getQueryDslSpecificationExecutor(entityClass)).thenReturn(queryDslSpecificationExecutor)

        val person = Person("John", "Doe")
        whenever(queryDslSpecificationExecutor.findAll(specification)).thenReturn(listOf(person))

        val result = entitySearchService.findAll(rootExpression, searchDescriptor)

        assertThat(result).containsExactly(person)

        verifyZeroInteractions(jpaSpecificationService)
        verifyZeroInteractions(entityJoinManager)
    }
}