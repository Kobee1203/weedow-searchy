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


class JpaQueryDslBuilder<T>(
    private val dataSearchContext: DataSearchContext,
    private val query: AbstractJPAQuery<T, *>,
    override val qEntityRoot: QEntityRoot<T>,
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


        var aliasType = when (elementType) {
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

    override fun equal(path: Path<*>, value: Any): Predicate {
        val type = path.type
        val expressionValue = convertValueToExpression(value)
        return when {
            Collection::class.java.isAssignableFrom(type) -> {
                Expressions.predicate(JPQLOps.MEMBER_OF, expressionValue, path)
            }
            else -> {
                Expressions.predicate(Ops.EQ, path, expressionValue)
            }
        }
    }

    override fun isNull(path: Path<*>): Predicate {
        val type = path.type
        return when {
            Map::class.java.isAssignableFrom(type) -> {
                Expressions.predicate(Ops.MAP_IS_EMPTY, path)
            }
            Collection::class.java.isAssignableFrom(type) -> {
                Expressions.predicate(Ops.COL_IS_EMPTY, path)
            }
            else -> {
                Expressions.predicate(Ops.IS_NULL, path)
            }
        }
    }

    override fun like(path: Path<String>, value: String): Predicate {
        val expressionValue = Expressions.constant(value.replace("*", "%"))
        return Expressions.predicate(Ops.LIKE, path, expressionValue)
    }

    override fun ilike(path: Path<String>, value: String): Predicate {
        val expressionValue = Expressions.constant(value.replace("*", "%").toLowerCase())
        return Expressions.predicate(Ops.LIKE_IC, path, expressionValue)
    }

    override fun lessThan(path: Path<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.LT, path, expressionValue)
    }

    override fun lessThanOrEquals(path: Path<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.LOE, path, expressionValue)
    }

    override fun greaterThan(path: Path<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.GT, path, expressionValue)
    }

    override fun greaterThanOrEquals(path: Path<*>, value: Any): Predicate {
        val expressionValue = convertValueToExpression(value)
        return Expressions.predicate(Ops.GOE, path, expressionValue)
    }

    override fun `in`(path: Path<*>, values: Collection<*>): Predicate {
        return if (values.size == 1) {
            equal(path, values.iterator().next()!!)
        } else {
            val expressionValue = Expressions.constant(values)
            Expressions.predicate(Ops.IN, path, expressionValue)
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