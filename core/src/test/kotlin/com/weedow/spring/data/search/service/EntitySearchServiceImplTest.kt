package com.weedow.spring.data.search.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.query.specification.Specification
import com.weedow.spring.data.search.query.specification.SpecificationExecutor
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.SpecificationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class EntitySearchServiceImplTest {

    @Mock
    private lateinit var specificationService: SpecificationService

    @Mock
    private lateinit var specificationExecutorFactory: SpecificationExecutorFactory

    @InjectMocks
    lateinit var entitySearchService: EntitySearchServiceImpl

    @Test
    fun find_all() {
        val rootExpression = mock<RootExpression<Person>>()

        val specificationExecutor = mock<SpecificationExecutor<Person>>()

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.specificationExecutor }.doReturn(specificationExecutor)
        }

        val specification = mock<Specification<Person>>()
        whenever(specificationService.createSpecification(rootExpression, searchDescriptor)).thenReturn(specification)

        val person = Person("John", "Doe")
        whenever(specificationExecutor.findAll(specification)).thenReturn(listOf(person))

        val result = entitySearchService.findAll(rootExpression, searchDescriptor)

        assertThat(result).containsExactly(person)

        verifyZeroInteractions(specificationExecutorFactory)
    }

    @Test
    fun find_all_when_specificationExecutor_is_null() {
        val entityClass = Person::class.java

        val rootExpression = mock<RootExpression<Person>>()

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.entityClass }.thenReturn(entityClass)
        }

        val specification = mock<Specification<Person>>()
        whenever(specificationService.createSpecification(rootExpression, searchDescriptor)).thenReturn(specification)

        val specificationExecutor = mock<SpecificationExecutor<Person>>()

        whenever(specificationExecutorFactory.getSpecificationExecutor(entityClass)).thenReturn(specificationExecutor)

        val person = Person("John", "Doe")
        whenever(specificationExecutor.findAll(specification)).thenReturn(listOf(person))

        val result = entitySearchService.findAll(rootExpression, searchDescriptor)

        assertThat(result).containsExactly(person)
    }
}