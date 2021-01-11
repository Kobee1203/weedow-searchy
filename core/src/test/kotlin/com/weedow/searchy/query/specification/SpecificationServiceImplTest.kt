package com.weedow.searchy.query.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.expression.RootExpression
import com.weedow.searchy.join.EntityJoinManager
import com.weedow.searchy.join.EntityJoins
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
        val searchyDescriptor = mock<SearchyDescriptor<Any>>()

        val entityJoins = mock<EntityJoins>()
        whenever(entityJoinManager.computeEntityJoins(searchyDescriptor)).thenReturn(entityJoins)

        val expectedSpecification = mock<Specification<Any>>()
        whenever(rootExpression.toSpecification<Any>(entityJoins)).thenReturn(expectedSpecification)

        val specification = specificationService.createSpecification(rootExpression, searchyDescriptor)

        Assertions.assertThat(specification).isSameAs(expectedSpecification)
    }
}