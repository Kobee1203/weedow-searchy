package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.field.FieldInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ExpressionUtilsTest {

    @Test
    fun testEquals() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "John"

        val expression = ExpressionUtils.equals(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue))
    }

    @Test
    fun contains() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "Jo"

        val expression = ExpressionUtils.contains(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.CONTAINS, fieldInfo, fieldValue))
    }

    @Test
    fun icontains() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "jo"

        val expression = ExpressionUtils.icontains(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.ICONTAINS, fieldInfo, fieldValue))
    }

    @Test
    fun greaterThan() {
        val fieldInfo = FieldInfo("height", Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170

        val expression = ExpressionUtils.greaterThan(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue))
    }

    @Test
    fun greaterThanOrEquals() {
        val fieldInfo = FieldInfo("height", Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170

        val expression = ExpressionUtils.greaterThanOrEquals(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, fieldInfo, fieldValue))
    }

    @Test
    fun lessThan() {
        val fieldInfo = FieldInfo("height", Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170

        val expression = ExpressionUtils.lessThan(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue))
    }

    @Test
    fun lessThanOrEquals() {
        val fieldInfo = FieldInfo("height", Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170

        val expression = ExpressionUtils.lessThanOrEquals(fieldInfo, fieldValue)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.LESS_THAN_OR_EQUALS, fieldInfo, fieldValue))
    }

    @Test
    fun between() {
        val fieldInfo = FieldInfo("height", Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue1 = 160
        val fieldValue2 = 180

        val expression = ExpressionUtils.between(fieldInfo, fieldValue1, fieldValue2)

        val expectedExpression = LogicalExpression(LogicalOperator.AND, arrayOf(SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue1), SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue2)))
        assertThat(expression).isEqualTo(expectedExpression)
    }

    @Test
    fun `in`() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"

        val expression = ExpressionUtils.`in`(fieldInfo, listOf(fieldValue1, fieldValue2))

        assertThat(expression).isEqualTo(SimpleExpression(Operator.IN, fieldInfo, listOf(fieldValue1, fieldValue2)))
    }

    @Test
    operator fun not() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "John"

        val expression = ExpressionUtils.not(ExpressionUtils.equals(fieldInfo, fieldValue))

        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)))
    }

    @Test
    fun and() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"

        val expression = ExpressionUtils.and(ExpressionUtils.equals(fieldInfo, fieldValue1), ExpressionUtils.equals(fieldInfo, fieldValue2))

        val expectedExpression = LogicalExpression(LogicalOperator.AND, arrayOf(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue1), SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue2)))
        assertThat(expression).isEqualTo(expectedExpression)
    }

    @Test
    fun or() {
        val fieldInfo = FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"

        val expression = ExpressionUtils.or(ExpressionUtils.equals(fieldInfo, fieldValue1), ExpressionUtils.equals(fieldInfo, fieldValue2))

        val expectedExpression = LogicalExpression(LogicalOperator.OR, arrayOf(SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue1), SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue2)))
        assertThat(expression).isEqualTo(expectedExpression)
    }
}