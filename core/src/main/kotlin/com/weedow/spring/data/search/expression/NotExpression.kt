package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
import org.springframework.data.jpa.domain.Specification

/**
 * Expression to negate the given [expression].
 */
internal data class NotExpression(private val expression: Expression) : Expression {

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return expression.toFieldExpressions(!negated)
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        return Specification.not(expression.toSpecification<T>(entityJoins))
    }

    override fun <T> toQueryDslSpecification(entityJoins: EntityJoins): QueryDslSpecification<T> {
        return QueryDslSpecification.not(expression.toQueryDslSpecification<T>(entityJoins))
    }

}