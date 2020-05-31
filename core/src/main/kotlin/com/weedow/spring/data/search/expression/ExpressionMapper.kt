package com.weedow.spring.data.search.expression

interface ExpressionMapper {

    fun <T> toExpression(params: Map<String, List<String>>, rootClass: Class<T>): RootExpression<T>

}