package com.weedow.spring.data.search.expression

/**
 * Default implementation of [FieldExpression].
 */
data class FieldExpressionImpl(
        override val fieldInfo: FieldInfo,
        override val value: Any,
        override val operator: Operator,
        override val negated: Boolean
) : FieldExpression {
}