package com.weedow.searchy.expression.parser

import com.weedow.searchy.exception.UnsupportedOperatorException
import com.weedow.searchy.expression.Expression
import com.weedow.searchy.expression.ExpressionResolver
import com.weedow.searchy.expression.ExpressionUtils
import com.weedow.searchy.expression.Operator
import com.weedow.searchy.utils.NullValue
import com.weedow.searchy.utils.klogger
import org.antlr.v4.runtime.ParserRuleContext

/**
 * Default [ExpressionParserVisitor] implementation.
 *
 * @param expressionResolver [ExpressionResolver] used by the parser to resolve the Expressions from the query
 * @param rootClass Root Entity Class
 */
class ExpressionParserVisitorImpl(
    private val expressionResolver: ExpressionResolver,
    private val rootClass: Class<*>
) : ExpressionParserVisitor, com.weedow.searchy.expression.parser.QueryBaseVisitor<Expression>() {

    companion object {

        private val log by klogger()

        private const val SINGLE_QUOTE = "'"
        private const val DOUBLE_QUOTE = "\""
        private const val ESCAPED_SINGLE_QUOTE = "\\'"
        private const val ESCAPED_DOUBLE_QUOTE = "\\\""
    }

    override fun visitStart(ctx: QueryParser.StartContext): Expression {
        if (log.isDebugEnabled) log.debug("visitStart: ${ctx.text}")

        // skip ctx.EOF()
        return visit(ctx.expression())
    }

    override fun visitParenthesizedExpression(ctx: QueryParser.ParenthesizedExpressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitParenthesizedExpression: ${ctx.text}")

        return visit(ctx.expression())
    }

    override fun visitNegativeExpression(ctx: QueryParser.NegativeExpressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitNegativeExpression: ${ctx.text}")

        return ExpressionUtils.not(visit(ctx.expression()))
    }

    override fun visitAndExpression(ctx: QueryParser.AndExpressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitAndExpression: ${ctx.text}")

        val expressions = ctx.expression().map { visit(it) }.toTypedArray()
        return ExpressionUtils.and(*expressions)
    }

    override fun visitOrExpression(ctx: QueryParser.OrExpressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitOrExpression: ${ctx.text}")

        val expressions = ctx.expression().map { visit(it) }.toTypedArray()
        return ExpressionUtils.or(*expressions)
    }

    override fun visitComparison_expression(ctx: QueryParser.Comparison_expressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitComparison_expression: ${ctx.text}")

        val operatorInfo = getOperatorInfo(ctx)

        val fieldPath = ctx.field_path().text
        val valueRuleContext = ctx.string_value() ?: ctx.number_value() ?: ctx.date_value() ?: ctx.boolean_value()
        val value = cleanValue(valueRuleContext)
        val fieldValues = listOf(value)

        return expressionResolver.resolveExpression(rootClass, fieldPath, fieldValues, operatorInfo.operator, operatorInfo.negated)
    }

    override fun visitNull_comparison_expression(ctx: QueryParser.Null_comparison_expressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitNull_comparison_expression: ${ctx.text}")

        val fieldPath = ctx.field_path().text
        val negated = ctx.K_NOT() != null

        return expressionResolver.resolveExpression(rootClass, fieldPath, listOf(NullValue.NULL_VALUE), Operator.EQUALS, negated)
    }

    override fun visitIn_expression(ctx: QueryParser.In_expressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitIn_expression: ${ctx.text}")

        val fieldPath = ctx.field_path().text
        val negated = ctx.K_NOT() != null
        val fieldValues = ctx.value().map {
            val valueRuleContext = it.string_value() ?: it.number_value() ?: it.date_value() ?: it.boolean_value()
            cleanValue(valueRuleContext)
        }.toList()

        return expressionResolver.resolveExpression(rootClass, fieldPath, fieldValues, Operator.IN, negated)
    }

    override fun visitMatches_expression(ctx: QueryParser.Matches_expressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitMatches_expression: ${ctx.text}")

        val fieldPath = ctx.field_path().text
        val valueRuleContext = ctx.string_value()
        val value = cleanValue(valueRuleContext)
        val fieldValues = listOf(value)

        return expressionResolver.resolveExpression(rootClass, fieldPath, fieldValues, Operator.MATCHES, false)
    }

    override fun visitImatches_expression(ctx: QueryParser.Imatches_expressionContext): Expression {
        if (log.isDebugEnabled) log.debug("visitImatches_expression: ${ctx.text}")

        val fieldPath = ctx.field_path().text
        val valueRuleContext = ctx.string_value()
        val value = cleanValue(valueRuleContext)
        val fieldValues = listOf(value)

        return expressionResolver.resolveExpression(rootClass, fieldPath, fieldValues, Operator.IMATCHES, false)
    }

    override fun visitBetween_expression(ctx: QueryParser.Between_expressionContext?): Expression {
        throw NotImplementedError("BETWEEN OPERATOR IS NOT YET IMPLEMENTED")
    }

    private fun getOperatorInfo(ctx: QueryParser.Comparison_expressionContext): OperatorInfo {
        val operatorRuleContext = ctx.comparison_operator() ?: ctx.boolean_comparison_operator()
        return when (val operator = operatorRuleContext.text) {
            "=" -> OperatorInfo(Operator.EQUALS)
            "!=" -> OperatorInfo(Operator.EQUALS, true)
            "<" -> OperatorInfo(Operator.LESS_THAN)
            "<=" -> OperatorInfo(Operator.LESS_THAN_OR_EQUALS)
            ">" -> OperatorInfo(Operator.GREATER_THAN)
            ">=" -> OperatorInfo(Operator.GREATER_THAN_OR_EQUALS)
            else -> throw UnsupportedOperatorException(operator)
        }
    }

    private fun cleanValue(valueRuleContext: ParserRuleContext): String {
        return valueRuleContext.text
            .removeSurrounding(SINGLE_QUOTE)
            .removeSurrounding(DOUBLE_QUOTE)
            .replace(ESCAPED_SINGLE_QUOTE, SINGLE_QUOTE)
            .replace(ESCAPED_DOUBLE_QUOTE, DOUBLE_QUOTE)
    }

    internal data class OperatorInfo(val operator: Operator, val negated: Boolean = false)

}