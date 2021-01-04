package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.query.specification.Specification

/**
 * Expression to negate the given [expression].
 *
 * @param expression Expression to be negated
 */
internal data class NotExpression(private val expression: Expression) : Expression {

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return expression.toFieldExpressions(!negated)
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        return Specification.not(expression.toSpecification(entityJoins))
    }

}