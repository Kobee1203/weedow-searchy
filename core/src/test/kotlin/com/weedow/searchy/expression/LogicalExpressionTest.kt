package com.weedow.searchy.expression

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.Predicate
import com.weedow.searchy.join.EntityJoins
import com.weedow.searchy.query.QueryBuilder
import com.weedow.searchy.query.specification.Specification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class LogicalExpressionTest {

    private lateinit var mockExpression1: Expression
    private lateinit var mockExpression2: Expression

    private lateinit var entityJoins: EntityJoins

    private lateinit var queryBuilder: QueryBuilder<Any>

    private lateinit var mockPredicate1: Predicate
    private lateinit var mockPredicate2: Predicate

    @BeforeEach
    fun setUp() {
        mockExpression1 = mock(name = "mockExpression1")
        mockExpression2 = mock(name = "mockExpression2")
    }

    private fun setUpSpecification() {
        entityJoins = mock()

        queryBuilder = mock()

        val mockSpecification1 = mock<Specification<Any>>()
        whenever(mockExpression1.toSpecification<Any>(entityJoins)).thenReturn(mockSpecification1)
        mockPredicate1 = mock(name = "mockPredicate1")
        whenever(mockSpecification1.toPredicate(queryBuilder)).thenReturn(mockPredicate1)

        val mockSpecification2 = mock<Specification<Any>>()
        whenever(mockExpression2.toSpecification<Any>(entityJoins)).thenReturn(mockSpecification2)
        mockPredicate2 = mock(name = "mockPredicate2")
        whenever(mockSpecification2.toPredicate(queryBuilder)).thenReturn(mockPredicate2)
    }

    @Test
    fun to_specification_with_OR_operator() {
        setUpSpecification()

        val predicate = mock<Predicate>()
        whenever(queryBuilder.or(mockPredicate1, mockPredicate2)).thenReturn(predicate)

        val expression = LogicalExpression(LogicalOperator.OR, listOf(mockExpression1, mockExpression2))
        val specification = expression.toSpecification<Any>(entityJoins)

        val result = specification.toPredicate(queryBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_AND_operator() {
        setUpSpecification()

        val predicate = mock<Predicate>()
        whenever(queryBuilder.and(mockPredicate1, mockPredicate2)).thenReturn(predicate)

        val expression = LogicalExpression(LogicalOperator.AND, listOf(mockExpression1, mockExpression2))
        val specification = expression.toSpecification<Any>(entityJoins)

        val result = specification.toPredicate(queryBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_field_expressions() {
        assertToFieldExpressions(LogicalOperator.AND, false)
        assertToFieldExpressions(LogicalOperator.OR, false)

        assertToFieldExpressions(LogicalOperator.AND, true)
        assertToFieldExpressions(LogicalOperator.OR, true)
    }

    private fun assertToFieldExpressions(logicalOperator: LogicalOperator, negated: Boolean) {
        val fieldExpression1 = mock<FieldExpression>()
        whenever(mockExpression1.toFieldExpressions(negated)).thenReturn(listOf(fieldExpression1))
        val fieldExpression2 = mock<FieldExpression>()
        whenever(mockExpression2.toFieldExpressions(negated)).thenReturn(listOf(fieldExpression2))

        val expression = LogicalExpression(logicalOperator, listOf(mockExpression1, mockExpression2))
        val fieldExpressions = expression.toFieldExpressions(negated)

        assertThat(fieldExpressions).containsExactly(fieldExpression1, fieldExpression2)
    }
}