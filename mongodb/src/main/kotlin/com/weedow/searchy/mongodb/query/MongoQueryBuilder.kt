package com.weedow.searchy.mongodb.query

import com.querydsl.core.JoinType
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.DateExpression
import com.querydsl.core.types.dsl.DateTimeExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.TimeExpression
import com.querydsl.mongodb.MongodbOps
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.QueryBuilder
import com.weedow.searchy.query.querytype.QEntityJoin
import com.weedow.searchy.query.querytype.QEntityJoinImpl
import com.weedow.searchy.query.querytype.QEntityRoot
import com.weedow.searchy.query.querytype.QPath
import com.weedow.searchy.utils.Keyword
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery

/**
 * MongoDB [QueryBuilder] implementation.
 *
 * @param searchyContext [SearchyContext]
 * @param query [SpringDataMongodbQuery]
 * @param qEntityRoot [QEntityRoot]
 */
class MongoQueryBuilder<T>(
    private val searchyContext: SearchyContext,
    private val query: SpringDataMongodbQuery<T>,
    override val qEntityRoot: QEntityRoot<T>
) : QueryBuilder<T> {

    override fun distinct() {
        query.distinct()
    }

    override fun join(qPath: QPath<*>, joinType: JoinType, fetched: Boolean): QEntityJoin<*> {
        val propertyInfos = qPath.propertyInfos
        val elementType = propertyInfos.elementType

        return QEntityJoinImpl(qEntityRoot, propertyInfos)
    }

    override fun and(x: Expression<Boolean>, y: Expression<Boolean>): Predicate {
        return Expressions.predicate(Ops.AND, x, y)
    }

    override fun and(vararg restrictions: Predicate): Predicate {
        return Expressions.predicate(Ops.AND, *restrictions)
    }

    override fun or(x: Expression<Boolean>, y: Expression<Boolean>): Predicate {
        return Expressions.predicate(Ops.OR, x, y)
    }

    override fun or(vararg restrictions: Predicate): Predicate {
        return Expressions.predicate(Ops.OR, *restrictions)
    }

    override fun not(restriction: Expression<Boolean>): Predicate {
        return Expressions.predicate(Ops.NOT, restriction)
    }

    override fun equal(x: Expression<*>, value: Any): Predicate {
        val type = x.type
        val expressionValue = convertValueToExpression(value)
        return when {
            Collection::class.java.isAssignableFrom(type) -> {
                Expressions.predicate(MongodbOps.ELEM_MATCH, expressionValue, x)
            }
            else -> {
                Expressions.predicate(Ops.EQ, x, expressionValue)
            }
        }
    }

    override fun isNull(x: Expression<*>): Predicate {
        val type = x.type
        return when {
            Map::class.java.isAssignableFrom(type) -> {
                Expressions.predicate(Ops.MAP_IS_EMPTY, x)
            }
            Collection::class.java.isAssignableFrom(type) -> {
                Expressions.predicate(Ops.COL_IS_EMPTY, x)
            }
            else -> {
                Expressions.predicate(Ops.IS_NULL, x)
            }
        }
    }

    override fun like(x: Expression<String>, value: String): Predicate {
        val expressionValue = Expressions.constant(value.replace("*", "%"))
        return Expressions.predicate(Ops.LIKE, x, expressionValue)
    }

    override fun ilike(x: Expression<String>, value: String): Predicate {
        val expressionValue = Expressions.constant(value.replace("*", "%").toLowerCase())
        return Expressions.predicate(Ops.LIKE_IC, x, expressionValue)
    }

    override fun lessThan(x: Expression<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.LT, x, expressionValue)
    }

    override fun lessThanOrEquals(x: Expression<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.LOE, x, expressionValue)
    }

    override fun greaterThan(x: Expression<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.GT, x, expressionValue)
    }

    override fun greaterThanOrEquals(x: Expression<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.GOE, x, expressionValue)
    }

    override fun `in`(x: Expression<*>, values: Collection<*>): Predicate {
        return if (values.size == 1) {
            equal(x, values.iterator().next()!!)
        } else {
            val expressionValue = Expressions.constant(values)
            Expressions.predicate(Ops.IN, x, expressionValue)
        }
    }

    private fun convertValueToExpression(value: Any): Expression<*> {
        return when {
            Keyword.CURRENT_DATE === value -> DateExpression.currentDate()
            Keyword.CURRENT_TIME === value -> TimeExpression.currentTime()
            Keyword.CURRENT_DATE_TIME === value -> DateTimeExpression.currentTimestamp()
            else -> Expressions.constant(value)
        }
    }

}