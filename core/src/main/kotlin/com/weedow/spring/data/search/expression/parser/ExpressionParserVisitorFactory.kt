package com.weedow.spring.data.search.expression.parser

/**
 * Interface to get the [ExpressionParserVisitor] from the given rootClass.
 */
interface ExpressionParserVisitorFactory {

    /**
     * Returns the [ExpressionParserVisitor] from the given rootClass.
     *
     * @param rootClass Root Entity Class
     */
    fun getExpressionParserVisitor(rootClass: Class<*>): ExpressionParserVisitor

}