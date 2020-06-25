package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification

/**
 * Interface to represent a query Expression.
 */
interface Expression {

    /**
     * Converts this Expression to a [FieldExpressions][FieldExpression].
     *
     * @param negated whether the expression is negated
     * @return Collection of [FieldExpressions][FieldExpression]
     */
    fun toFieldExpressions(negated: Boolean): Collection<FieldExpression>

    /**
     * Converts this Expression to a [Specification].
     *
     * @param entityJoins
     * @return [Specification] instance
     */
    fun <T> toSpecification(entityJoins: EntityJoins): Specification<T>

}
