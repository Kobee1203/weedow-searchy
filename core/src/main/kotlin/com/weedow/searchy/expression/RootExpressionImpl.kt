package com.weedow.searchy.expression

import com.weedow.searchy.join.EntityJoin
import com.weedow.searchy.join.EntityJoins
import com.weedow.searchy.query.QueryBuilder
import com.weedow.searchy.query.specification.Specification

/**
 * Default [RootExpression] implementation.
 *
 * @param expressions [Expression]s
 */
class RootExpressionImpl<T>(
    vararg val expressions: Expression
) : RootExpression<T> {

    companion object {
        /** Filter the joins and return the fetched joins */
        val FILTER_FETCH_JOINS = { entityJoin: EntityJoin -> entityJoin.fetched }
    }

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return expressions.flatMap { it.toFieldExpressions(negated).toList() }
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        var specification = Specification { builder: QueryBuilder<T> ->
            builder.distinct()

            val fetchJoins = entityJoins.getJoins(FILTER_FETCH_JOINS)
            fetchJoins.values.forEach {
                entityJoins.getQPath(it.fieldPath, builder.qEntityRoot, builder)
            }

            Specification.NO_PREDICATE
        }

        expressions.forEach { specification = specification.and(it.toSpecification(entityJoins)) }

        return specification
    }

}