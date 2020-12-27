package com.weedow.spring.data.search.querydsl.jpa

import com.querydsl.core.JoinType
import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.DateExpression
import com.querydsl.core.types.dsl.DateTimeExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.TimeExpression
import com.querydsl.jpa.JPAQueryMixin
import com.querydsl.jpa.JPQLOps
import com.querydsl.jpa.impl.AbstractJPAQuery
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.querytype.*
import com.weedow.spring.data.search.utils.Keyword

/**
 * JPA [QueryDslBuilder] implementation.
 *
 * @param dataSearchContext [DataSearchContext]
 * @param query [AbstractJPAQuery]
 * @param qEntityRoot [QEntityRoot]
 */
class JpaQueryDslBuilder<T>(
    private val dataSearchContext: DataSearchContext,
    private val query: AbstractJPAQuery<T, *>,
    override val qEntityRoot: QEntityRoot<T>
) : QueryDslBuilder<T> {

    override fun distinct() {
        query.distinct()
    }

    override fun join(qPath: QPath<*>, joinType: JoinType, fetched: Boolean): QEntityJoin<*> {
        val propertyInfos = qPath.propertyInfos
        val elementType = propertyInfos.elementType

        if (elementType == ElementType.MAP_KEY || elementType == ElementType.MAP_VALUE) {
            return QEntityJoinImpl(QEntityImpl(dataSearchContext, propertyInfos.type, qPath.path.metadata), propertyInfos)
        }

        val joinExpression = query.metadata.joins
            .firstOrNull {
                val target = it.target
                target is Operation
                        && target.operator === Ops.ALIAS
                        && target.getArg(0).toString() == qPath.path.toString()
            }

        if (joinExpression != null) {
            if (fetched && !joinExpression.hasFlag(JPAQueryMixin.FETCH)) {
                joinExpression.flags.add(JPAQueryMixin.FETCH)
            }
            val alias = (joinExpression.target as Operation).getArg(1) as QEntity<*>
            return QEntityJoinImpl(alias, propertyInfos)
        }

        val aliasType = when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> {
                propertyInfos.parameterizedTypes[0]
            }
            ElementType.MAP -> propertyInfos.parameterizedTypes[1]
            ElementType.ENTITY -> propertyInfos.type
            else -> throw IllegalArgumentException("Could not identify the alias type for the QPath of type '$elementType': $qPath")
        }

        val alias = createAlias(aliasType, qPath)

        val path = qPath.path
        val join = when (joinType) {
            JoinType.JOIN -> join(elementType, path, alias)
            JoinType.INNERJOIN -> innerJoin(elementType, path, alias)
            JoinType.LEFTJOIN -> leftJoin(elementType, path, alias)
            JoinType.RIGHTJOIN -> rightJoin(elementType, path, alias)
            JoinType.FULLJOIN -> fullJoin(elementType, path, alias)
            JoinType.DEFAULT -> crossJoin(elementType, path, alias)
        }

        if (fetched) {
            query.fetchJoin()
        }

        return QEntityJoinImpl(join, propertyInfos)
    }

    private fun <E> createAlias(aliasType: Class<E>, qPath: QPath<*>): QEntity<E> {
        return dataSearchContext.get(aliasType) { entityClazz ->
            QEntityAliasImpl(entityClazz, qPath.propertyInfos.fieldName)
        }
    }

    private fun <E> join(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> query.join(path as CollectionExpression<*, E>, alias)
            ElementType.MAP -> query.join(path as MapExpression<*, E>, alias)
            ElementType.ENTITY -> query.join(path as QEntity<E>, alias)
            else -> throw IllegalArgumentException("Could not create a join with the following: element type=$elementType, path=$path, alias: $alias")
        }

        return alias
    }

    private fun <E> innerJoin(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> query.innerJoin(path as CollectionExpression<*, E>, alias)
            ElementType.MAP -> query.innerJoin(path as MapExpression<*, E>, alias)
            ElementType.ENTITY -> query.innerJoin(path as QEntity<E>, alias)
            else -> throw IllegalArgumentException("Could not create an inner join with the following: element type=$elementType, path=$path, alias: $alias")
        }

        return alias
    }

    private fun <E> leftJoin(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> query.leftJoin(path as CollectionExpression<*, E>, alias)
            ElementType.MAP -> query.leftJoin(path as MapExpression<*, E>, alias)
            ElementType.ENTITY -> query.leftJoin(path as QEntity<E>, alias)
            else -> throw IllegalArgumentException("Could not create a left join with the following: element type=$elementType, path=$path, alias: $alias")
        }

        return alias
    }

    private fun <E> rightJoin(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> query.rightJoin(path as CollectionExpression<*, E>, alias)
            ElementType.MAP -> query.rightJoin(path as MapExpression<*, E>, alias)
            ElementType.ENTITY -> query.rightJoin(path as QEntity<E>, alias)
            else -> throw IllegalArgumentException("Could not create a right join with the following: element type=$elementType, path=$path, alias: $alias")
        }

        return alias
    }

    private fun <E> fullJoin(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        throw IllegalArgumentException("full join in JPA is not allowed: element type=$elementType, path=$path, alias=$alias")
    }

    private fun <E> crossJoin(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        return when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> {
                query.from(path as CollectionExpression<*, E>, alias)
                alias
            }
            ElementType.ENTITY -> {
                query.from(path as QEntity<E>)
                path
            }
            else -> throw IllegalArgumentException("Could not create a cross join (from) with the following: element type=$elementType, path=$path, alias: $alias")
        }
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
                Expressions.predicate(JPQLOps.MEMBER_OF, expressionValue, x)
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