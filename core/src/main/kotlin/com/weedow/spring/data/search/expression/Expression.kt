package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification

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
     * Converts this Expression to a [QueryDslSpecification].
     *
     * @param entityJoins [EntityJoins] instance
     * @return [QueryDslSpecification] instance
     */
    fun <T> toQueryDslSpecification(entityJoins: EntityJoins): QueryDslSpecification<T>

}
