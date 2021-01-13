package com.weedow.searchy.expression.parser

import com.weedow.searchy.expression.ExpressionResolver

/**
 * Default [ExpressionParserVisitorFactory] implementation.
 *
 * @param expressionResolver [ExpressionResolver] used by the parser to resolve the Expressions from the query
 */
class ExpressionParserVisitorFactoryImpl(
    private val expressionResolver: ExpressionResolver
) : ExpressionParserVisitorFactory {

    override fun getExpressionParserVisitor(rootClass: Class<*>): ExpressionParserVisitor {
        return ExpressionParserVisitorImpl(expressionResolver, rootClass)
    }

}