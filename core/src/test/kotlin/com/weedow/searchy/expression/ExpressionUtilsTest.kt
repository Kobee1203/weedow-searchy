package com.weedow.searchy.expression

import com.weedow.searchy.common.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ExpressionUtilsTest {

    @Test
    fun testEquals() {
        val fieldValue = "John"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.equals(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue))
    }

    @Test
    fun matches() {
        val fieldValue = "Jo"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.matches(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.MATCHES, fieldInfo, fieldValue))
    }

    @Test
    fun imatches() {
        val fieldValue = "jo"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.imatches(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.IMATCHES, fieldInfo, fieldValue))
    }

    @Test
    fun greaterThan() {
        val fieldValue = 170
        val fieldInfo = FieldInfo("height", "height", Person::class.java)

        val expression = ExpressionUtils.greaterThan(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue))
    }

    @Test
    fun greaterThanOrEquals() {
        val fieldValue = 170
        val fieldInfo = FieldInfo("height", "height", Person::class.java)

        val expression = ExpressionUtils.greaterThanOrEquals(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, fieldInfo, fieldValue))
    }

    @Test
    fun lessThan() {
        val fieldValue = 170
        val fieldInfo = FieldInfo("height", "height", Person::class.java)

        val expression = ExpressionUtils.lessThan(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue))
    }

    @Test
    fun lessThanOrEquals() {
        val fieldValue = 170
        val fieldInfo = FieldInfo("height", "height", Person::class.java)

        val expression = ExpressionUtils.lessThanOrEquals(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.LESS_THAN_OR_EQUALS, fieldInfo, fieldValue))
    }

    @Test
    fun between() {
        val fieldValue1 = 160
        val fieldValue2 = 180
        val fieldInfo = FieldInfo("height", "height", Person::class.java)

        val expression = ExpressionUtils.between(fieldInfo, fieldValue1, fieldValue2)

        val expectedExpression = LogicalExpression(
            LogicalOperator.AND,
            listOf(SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue1), SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue2))
        )
        assertThat(expression).isEqualTo(expectedExpression)
    }

    @Test
    fun `in`() {
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.`in`(fieldInfo, listOf(fieldValue1, fieldValue2))

        assertThat(expression).isEqualTo(SimpleExpression(Operator.IN, fieldInfo, listOf(fieldValue1, fieldValue2)))
    }

    @Test
    fun not() {
        val fieldValue = "John"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.not(ExpressionUtils.equals(fieldInfo, fieldValue))

        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)))
    }

    @Test
    fun and() {
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.and(ExpressionUtils.equals(fieldInfo, fieldValue1), ExpressionUtils.equals(fieldInfo, fieldValue2))

        val expectedExpression = LogicalExpression(
            LogicalOperator.AND,
            listOf(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue1), SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue2))
        )
        assertThat(expression).isEqualTo(expectedExpression)
    }

    @Test
    fun or() {
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val fieldInfo = FieldInfo("firstName", "firstName", Person::class.java)

        val expression = ExpressionUtils.or(ExpressionUtils.equals(fieldInfo, fieldValue1), ExpressionUtils.equals(fieldInfo, fieldValue2))

        val expectedExpression = LogicalExpression(
            LogicalOperator.OR,
            listOf(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue1), SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue2))
        )
        assertThat(expression).isEqualTo(expectedExpression)
    }
}