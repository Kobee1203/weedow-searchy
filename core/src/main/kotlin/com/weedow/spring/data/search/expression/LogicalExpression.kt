package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.query.specification.Specification

/**
 * Logical Expression represented by the logical operators [AND][LogicalOperator.AND] and [OR][LogicalOperator.OR] that specify the relationship between [expressions].
 *
 * @param logicalOperator Logical operator
 * @param expressions List of [Expression]
 */
internal data class LogicalExpression(
    private val logicalOperator: LogicalOperator,
    private val expressions: List<Expression>
) : Expression {

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return expressions.flatMap { it.toFieldExpressions(negated).toList() }
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        var lastSpecification = Specification.where<T>(null)
        expressions.forEach { expression ->
            val specification = expression.toSpecification<T>(entityJoins)
            lastSpecification =
                if (logicalOperator == LogicalOperator.OR) lastSpecification.or(specification) else lastSpecification.and(specification)
        }
        return Specification.where(lastSpecification)
    }

}
