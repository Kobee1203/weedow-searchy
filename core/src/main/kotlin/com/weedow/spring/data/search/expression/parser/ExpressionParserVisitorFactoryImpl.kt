package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.expression.ExpressionResolver

class ExpressionParserVisitorFactoryImpl(
        private val expressionResolver: ExpressionResolver
) : ExpressionParserVisitorFactory {

    override fun getExpressionParserVisitor(rootClass: Class<*>): ExpressionParserVisitor {
        return ExpressionParserVisitorImpl(expressionResolver, rootClass)
    }

}