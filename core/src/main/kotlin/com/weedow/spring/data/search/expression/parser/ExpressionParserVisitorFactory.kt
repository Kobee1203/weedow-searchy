package com.weedow.spring.data.search.expression.parser

interface ExpressionParserVisitorFactory {

    fun getExpressionParserVisitor(rootClass: Class<*>): ExpressionParserVisitor

}