package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
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
     * @param entityJoins [EntityJoins] instance
     * @return [Specification] instance
     */
    @Deprecated(
        message = "Legacy method for old JPA implementation to be removed",
        replaceWith = ReplaceWith("this.toQueryDslSpecification(entityJoins)")
    )
    fun <T> toSpecification(entityJoins: EntityJoins): Specification<T>

    /**
     * Converts this Expression to a [QueryDslSpecification].
     *
     * @param entityJoins [EntityJoins] instance
     * @return [QueryDslSpecification] instance
     */
    fun <T> toQueryDslSpecification(entityJoins: EntityJoins): QueryDslSpecification<T>

}
