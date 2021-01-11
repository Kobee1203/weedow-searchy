package com.weedow.searchy.expression

/**
 * Default implementation of [FieldExpression].
 *
 * @param fieldInfo Field Information
 * @param value Value to be compared
 * @param operator [Operator] to be used by the expression
 * @param negated whether the expression is negated
 */
data class FieldExpressionImpl(
    override val fieldInfo: FieldInfo,
    override val value: Any,
    override val operator: Operator,
    override val negated: Boolean
) : FieldExpression