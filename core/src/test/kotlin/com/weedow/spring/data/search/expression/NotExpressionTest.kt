package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.query.QueryBuilder
import com.weedow.spring.data.search.query.specification.Specification
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class NotExpressionTest {

    @Test
    fun to_specification() {
        val entityJoins = mock<EntityJoins>()

        val queryBuilder = mock<QueryBuilder<Any>>()

        val mockExpression = mock<Expression>()
        val mockSpecification = mock<Specification<Any>>()
        whenever(mockExpression.toSpecification<Any>(entityJoins)).thenReturn(mockSpecification)

        val mockPredicate = mock<Predicate>()
        whenever(mockSpecification.toPredicate(queryBuilder)).thenReturn(mockPredicate)

        val predicate = mock<Predicate>()
        whenever(queryBuilder.not(mockPredicate)).thenReturn(predicate)

        val expression = NotExpression(mockExpression)
        val specification = expression.toSpecification<Any>(entityJoins)

        val result = specification.toPredicate(queryBuilder)

        Assertions.assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_field_expressions() {
        assertToFieldExpressions(false)
        assertToFieldExpressions(true)
    }

    private fun assertToFieldExpressions(negated: Boolean) {
        val fieldExpression = mock<FieldExpression>()
        val mockExpression = mock<Expression> {
            on { this.toFieldExpressions(!negated) }.doReturn(listOf(fieldExpression))
        }

        val expression = NotExpression(mockExpression)

        val fieldExpressions = expression.toFieldExpressions(negated)

        Assertions.assertThat(fieldExpressions).containsExactly(fieldExpression)
    }
}