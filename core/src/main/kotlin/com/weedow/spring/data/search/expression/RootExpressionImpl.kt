package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoin
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * Default [RootExpression] implementation.
 *
 * @param expressions [Expression]s
 */
class RootExpressionImpl<T>(
        vararg val expressions: Expression,
) : RootExpression<T> {

    companion object {
        /** Filter the joins and return the fetched joins */
        val FILTER_FETCH_JOINS = { entityJoin: EntityJoin -> entityJoin.fetched }
    }

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return expressions.flatMap { it.toFieldExpressions(negated).toList() }
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        var specification = Specification { root: Root<T>, query: CriteriaQuery<*>, _: CriteriaBuilder ->
            query.distinct(true)

            val fetchJoins = entityJoins.getJoins(FILTER_FETCH_JOINS)
            fetchJoins.values.forEach {
                entityJoins.getPath(it.fieldPath, root)
            }

            null
        }

        expressions.forEach { specification = specification.and(it.toSpecification<T>(entityJoins))!! }

        return specification
    }

    override fun <T> toQueryDslSpecification(entityJoins: EntityJoins): QueryDslSpecification<T> {
        var specification = QueryDslSpecification { builder: QueryDslBuilder<T> ->
            builder.distinct()

            val fetchJoins = entityJoins.getJoins(FILTER_FETCH_JOINS)
            fetchJoins.values.forEach {
                entityJoins.getQPath(it.fieldPath, builder)
            }

            QueryDslSpecification.NO_PREDICATE
        }

        expressions.forEach { specification = specification.and(it.toQueryDslSpecification(entityJoins)) }

        return specification
    }

}