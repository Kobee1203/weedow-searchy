package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoin
import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

class RootExpressionImpl<T>(
        vararg val expressions: Expression
) : RootExpression<T> {

    companion object {
        val FILTER_FETCH_JOINS = { entityJoin: EntityJoin -> entityJoin.fetched }
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        var specification = Specification { root: Root<T>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
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

}