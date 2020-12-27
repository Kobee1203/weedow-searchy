package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.expression.parser.ExpressionParser
import com.weedow.spring.data.search.utils.klogger
import java.util.*

/**
 * Default [ExpressionMapper] implementation.
 *
 * @param expressionResolver [ExpressionResolver]
 * @param expressionParser [ExpressionParser]
 */
class ExpressionMapperImpl(
    private val expressionResolver: ExpressionResolver,
    private val expressionParser: ExpressionParser
) : ExpressionMapper {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized ExpressionMapper: {}", this::class.qualifiedName)
    }

    override fun <T> toExpression(params: Map<String, List<String>>, rootClass: Class<T>): RootExpression<T> {
        val expressions: MutableList<Expression> = ArrayList()
        params.forEach { (paramName, paramValues) ->
            if (paramName != "query") {
                val operator = if (paramValues.size == 1) Operator.EQUALS else Operator.IN
                val expression = expressionResolver.resolveExpression(rootClass, paramName, paramValues, operator, false)
                expressions.add(expression)
            } else {
                // Processing special 'query' parameter
                paramValues.forEach { expressions.add(expressionParser.parse(it, rootClass)) }
            }
        }
        return RootExpressionImpl(*expressions.toTypedArray())
    }

}