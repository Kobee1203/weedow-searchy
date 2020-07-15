package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.expression.Expression
import org.antlr.v4.runtime.tree.ParseTreeVisitor

interface ExpressionParserVisitor : ParseTreeVisitor<Expression>
