package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.expression.Expression
import org.antlr.v4.runtime.tree.ParseTreeVisitor

/**
 * Interface to defines the basic notion of a Expression parser visitor.
 */
interface ExpressionParserVisitor : ParseTreeVisitor<Expression>
