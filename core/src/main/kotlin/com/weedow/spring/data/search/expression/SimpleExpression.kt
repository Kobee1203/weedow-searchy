package com.weedow.spring.data.search.expression

import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.expression.Operator.*
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
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

    override fun <T> toQueryDslSpecification(entityJoins: EntityJoins): QueryDslSpecification<T> {
        return QueryDslSpecification { builder: QueryDslBuilder<T> ->
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

    private fun equals(queryDslBuilder: QueryDslBuilder<*>, path: Path<*>, value: Any): Predicate {
        return if (value === NullValue) {
            queryDslBuilder.isNull(path)
        } else {
            queryDslBuilder.equal(path, value)
        }
    }

    private fun like(queryDslBuilder: QueryDslBuilder<*>, path: Path<String>, value: String): Predicate {
        return queryDslBuilder.like(path, value)
    }

    private fun ilike(queryDslBuilder: QueryDslBuilder<*>, path: Path<String>, value: String): Predicate {
        return queryDslBuilder.ilike(path, value)
    }

    private fun lessThan(queryDslBuilder: QueryDslBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryDslBuilder.lessThan(path, value)
    }

    private fun lessThanOrEquals(queryDslBuilder: QueryDslBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryDslBuilder.lessThanOrEquals(path, value)
    }

    private fun greaterThan(queryDslBuilder: QueryDslBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryDslBuilder.greaterThan(path, value)
    }

    private fun greaterThanOrEquals(queryDslBuilder: QueryDslBuilder<*>, path: Path<*>, value: Any): Predicate {
        return queryDslBuilder.greaterThanOrEquals(path, value)
    }

    private fun inPredicate(queryDslBuilder: QueryDslBuilder<*>, path: Path<*>, value: Collection<*>): Predicate {
        return queryDslBuilder.`in`(path, value)
    }

}
