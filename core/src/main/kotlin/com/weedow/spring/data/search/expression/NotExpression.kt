package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
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

}