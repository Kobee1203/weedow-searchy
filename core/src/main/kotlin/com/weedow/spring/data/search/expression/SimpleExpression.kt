package com.weedow.spring.data.search.expression

import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.expression.Operator.*
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.query.QueryBuilder
import com.weedow.spring.data.search.query.specification.Specification
import com.weedow.spring.data.search.utils.NullValue

/**
 * Expression to compare the field value with the given [value] according to the given [Operator].
 *
 * @param operator [Operator] to be used by the expression
 * @param fieldInfo Field Information
 * @param value Value to be compared
 */
internal data class SimpleExpression(
    private val operator: Operator,
    private val fieldInfo: FieldInfo,
    private val value: Any
) : Expression {

    override fun toFieldExpressions(negated: Boolean): Collection<FieldExpression> {
        return listOf(FieldExpressionImpl(fieldInfo, value, operator, negated))
    }

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        return Specification { builder: QueryBuilder<T> ->
            val qpath = entityJoins.getQPath(fieldInfo.fieldPath, builder.qEntityRoot, builder)

            when (operator) {
                EQUALS -> equals(builder, qpath.path, value)
                MATCHES -> @Suppress("UNCHECKED_CAST") like(builder, qpath.path as Path<String>, value as String)
                IMATCHES -> @Suppress("UNCHECKED_CAST") ilike(builder, qpath.path as Path<String>, value as String)
                LESS_THAN -> lessThan(builder, qpath.path, value)
                LESS_THAN_OR_EQUALS -> lessThanOrEquals(builder, qpath.path, value)
                GREATER_THAN -> greaterThan(builder, qpath.path, value)
                GREATER_THAN_OR_EQUALS -> greaterThanOrEquals(builder, qpath.path, value)
                IN -> inPredicate(builder, qpath.path, value as List<*>)
            }
        }
    }

    private fun equals(queryBuilder: QueryBuilder<*>, path: Path<*>, value: Any): Predicate {
        return if (value === NullValue) {
            queryBuilder.isNull(path)
        } else {
            queryBuilder.equal(path, value)
        }
    }

    private fun like(queryBuilder: QueryBuilder<*>, path: Path<String>, value: String): Predicate {
        return queryBuilder.like(path, value)
    }

    private fun ilike(queryBuilder: QueryBuilder<*>, path: Path<String>, value: String): Predicate {
        return queryBuilder.ilike(path, value)
    }

    private fun lessThan(queryBuilder: QueryBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryBuilder.lessThan(path, value)
    }

    private fun lessThanOrEquals(queryBuilder: QueryBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryBuilder.lessThanOrEquals(path, value)
    }

    private fun greaterThan(queryBuilder: QueryBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryBuilder.greaterThan(path, value)
    }

    private fun greaterThanOrEquals(queryBuilder: QueryBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryBuilder.greaterThanOrEquals(path, value)
    }

    private fun inPredicate(queryBuilder: QueryBuilder<*>, path: Path<*>, value: Collection<*>): Predicate {
        return queryBuilder.`in`(path, value)
    }

}
