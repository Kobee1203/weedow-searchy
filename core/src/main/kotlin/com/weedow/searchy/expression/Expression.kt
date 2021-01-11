package com.weedow.searchy.expression

import com.weedow.searchy.join.EntityJoins
import com.weedow.searchy.query.specification.Specification

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
     * @param entityJoins [EntityJoins] instance
     * @return [Specification] instance
     */
    fun <T> toSpecification(entityJoins: EntityJoins): Specification<T>

}
