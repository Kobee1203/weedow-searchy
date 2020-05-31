package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.field.FieldInfo

object ExpressionUtils {
    fun equals(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.EQUALS, fieldInfo, value)
    }

    fun contains(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.CONTAINS, fieldInfo, value)
    }

    fun icontains(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.ICONTAINS, fieldInfo, value)
    }

    fun greaterThan(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.GREATER_THAN, fieldInfo, value)
    }

    fun greaterThanOrEquals(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, fieldInfo, value)
    }

    fun lessThan(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.LESS_THAN, fieldInfo, value)
    }

    fun lessThanOrEquals(fieldInfo: FieldInfo, value: Any): Expression {
        return SimpleExpression(Operator.LESS_THAN_OR_EQUALS, fieldInfo, value)
    }

    fun between(fieldInfo: FieldInfo, lowValue: Any, highValue: Any): Expression {
        return and(greaterThan(fieldInfo, lowValue), lessThan(fieldInfo, highValue))
    }

    fun `in`(fieldInfo: FieldInfo, values: Collection<*>): Expression {
        return SimpleExpression(Operator.IN, fieldInfo, values)
    }

    fun not(expression: Expression): Expression {
        return NotExpression(expression)
    }

    fun and(vararg expressions: Expression): Expression {
        return LogicalExpression(LogicalOperator.AND, expressions)
    }

    fun or(vararg expressions: Expression): Expression {
        return LogicalExpression(LogicalOperator.OR, expressions)
    }
}