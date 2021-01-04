package com.weedow.spring.data.search.query.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.join.EntityJoins
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class SpecificationServiceImplTest {

    @Mock
    private lateinit var entityJoinManager: EntityJoinManager

    @InjectMocks
    private lateinit var specificationService: SpecificationServiceImpl

    @Test
    fun create_specification() {
        val rootExpression = mock<RootExpression<Any>>()
        val searchDescriptor = mock<SearchDescriptor<Any>>()

        val entityJoins = mock<EntityJoins>()
        whenever(entityJoinManager.computeEntityJoins(searchDescriptor)).thenReturn(entityJoins)

        val expectedSpecification = mock<Specification<Any>>()
        whenever(rootExpression.toSpecification<Any>(entityJoins)).thenReturn(expectedSpecification)

        val specification = specificationService.createSpecification(rootExpression, searchDescriptor)

        Assertions.assertThat(specification).isSameAs(expectedSpecification)
    }
}