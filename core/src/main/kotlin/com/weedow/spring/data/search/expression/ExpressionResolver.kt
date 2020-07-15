package com.weedow.spring.data.search.expression

interface ExpressionResolver {

    fun resolveExpression(rootClass: Class<*>, fieldPath: String, fieldValues: List<String>, operator: Operator, negated: Boolean): Expression

}