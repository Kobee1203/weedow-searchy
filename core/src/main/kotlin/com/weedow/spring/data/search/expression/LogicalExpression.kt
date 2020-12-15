package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
import org.springframework.data.jpa.domain.Specification

/**
 * Logical Expression represented by the logical operators [AND][LogicalOperator.AND] and [OR][LogicalOperator.OR] that specify the relationship between [expressions].
 */
internal data class LogicalExpression(
    private val logicalOperator: LogicalOperator,
    private val expressions: List<Expression>,
) : Expression {

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return expressions.flatMap { it.toFieldExpressions(negated).toList() }
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        var lastSpecification = Specification.where<T>(null)!!
        expressions.forEach { expression ->
            val specification = expression.toSpecification<T>(entityJoins)
            lastSpecification = if (logicalOperator == LogicalOperator.OR) lastSpecification.or(specification)!! else lastSpecification.and(specification)!!
        }
        return Specification.where(lastSpecification)!!
    }

    override fun <T> toQueryDslSpecification(entityJoins: EntityJoins): QueryDslSpecification<T> {
        var lastSpecification = QueryDslSpecification.where<T>(null)
        expressions.forEach { expression ->
            val specification = expression.toQueryDslSpecification<T>(entityJoins)
            lastSpecification = if (logicalOperator == LogicalOperator.OR) lastSpecification.or(specification) else lastSpecification.and(specification)
        }
        return QueryDslSpecification.where(lastSpecification)
    }

}
