package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class NotExpressionTest {

    @Test
    fun to_specification() {
        val entityJoins = mock<EntityJoins>()

        val queryDslBuilder = mock<QueryDslBuilder<Any>>()

        val mockExpression = mock<Expression>()
        val mockSpecification = mock<QueryDslSpecification<Any>>()
        whenever(mockExpression.toQueryDslSpecification<Any>(entityJoins)).thenReturn(mockSpecification)

        val mockPredicate = mock<Predicate>()
        whenever(mockSpecification.toPredicate(queryDslBuilder)).thenReturn(mockPredicate)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.not(mockPredicate)).thenReturn(predicate)

        val expression = NotExpression(mockExpression)
        val specification = expression.toQueryDslSpecification<Any>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

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