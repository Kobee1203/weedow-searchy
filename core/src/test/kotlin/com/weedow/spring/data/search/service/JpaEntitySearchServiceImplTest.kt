package com.weedow.spring.data.search.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.specification.JpaSpecificationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@ExtendWith(MockitoExtension::class)
internal class JpaEntitySearchServiceImplTest {

    @Mock
    private lateinit var jpaSpecificationService: JpaSpecificationService

    @Mock
    private lateinit var entityJoinManager: EntityJoinManager

    @InjectMocks
    private lateinit var jpaEntitySearchService: JpaEntitySearchServiceImpl


    @Test
    fun find_all() {
        val rootExpression = mock<RootExpression<Person>>()

        val jpaSpecificationExecutor = mock<JpaSpecificationExecutor<Person>>()

        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.jpaSpecificationExecutor }.thenReturn(jpaSpecificationExecutor)
        }

        val entityJoins = mock<EntityJoins>()
        whenever(entityJoinManager.computeEntityJoins(searchDescriptor)).thenReturn(entityJoins)

        val specification = mock<Specification<Person>>()
        whenever(jpaSpecificationService.createSpecification(rootExpression, entityJoins)).thenReturn(specification)

        val resultList = mock<List<Person>>()
        whenever(jpaSpecificationExecutor.findAll(specification)).thenReturn(resultList)

        val result = jpaEntitySearchService.findAll(rootExpression, searchDescriptor)

        assertThat(result).isSameAs(resultList)
    }
}