package com.weedow.spring.data.search.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoins
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.domain.Specification

@ExtendWith(MockitoExtension::class)
internal class JpaSpecificationServiceImplTest {

    @InjectMocks
    private lateinit var jpaSpecificationService: JpaSpecificationServiceImpl

    @Test
    fun create_specification() {
        val rootExpression = mock<RootExpression<Person>>()
        val entityJoins = mock<EntityJoins>()

        val expectedSpecification = mock<Specification<Person>>()
        whenever(rootExpression.toSpecification<Person>(entityJoins)).thenReturn(expectedSpecification)

        val specification = jpaSpecificationService.createSpecification(rootExpression, entityJoins)

        assertThat(specification).isEqualTo(expectedSpecification)
    }

}