package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.expression.Expression
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class ExpressionParserImpl(
        private val expressionParserVisitorFactory: ExpressionParserVisitorFactory
) : ExpressionParser {

    override fun parse(query: String, rootClass: Class<*>): Expression {
        val parser = getQueryParser(query)

        val startContext = parser.start()

        parser.errorListeners.forEach {
            if (it is SyntaxErrorListener) {
                val syntaxErrors = it.syntaxErrors
                if (syntaxErrors.isNotEmpty()) {
                    throw ExpressionParserException(syntaxErrors)
                }
            }
        }

        val expressionParserVisitor = expressionParserVisitorFactory.getExpressionParserVisitor(rootClass)

        return expressionParserVisitor.visit(startContext)
    }

    private fun getQueryParser(query: String): QueryParser {
        val errorListener = SyntaxErrorListenerImpl()

        val lexer = QueryLexer(CharStreams.fromString(query))
        lexer.addErrorListener(errorListener)

        val tokens = CommonTokenStream(lexer)

        val queryParser = QueryParser(tokens)
        queryParser.addErrorListener(errorListener)

        return queryParser
    }

}