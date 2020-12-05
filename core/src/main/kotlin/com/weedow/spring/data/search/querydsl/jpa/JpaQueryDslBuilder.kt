package com.weedow.spring.data.search.querydsl.jpa

import com.querydsl.core.JoinType
import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPQLOps
import com.querydsl.jpa.impl.AbstractJPAQuery
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.querytype.ElementType
import com.weedow.spring.data.search.querydsl.querytype.QEntity
import com.weedow.spring.data.search.querydsl.querytype.QPath

class JpaQueryDslBuilder<T>(
        private val dataSearchContext: DataSearchContext,
        private val query: AbstractJPAQuery<T, *>,
        override val qRootEntity: QEntity<out T>,
) : QueryDslBuilder<T> {

    override fun distinct() {
        query.distinct()
    }

    override fun join(qPath: QPath<*>, joinType: JoinType, fetched: Boolean): QEntity<*> {
        val elementType = qPath.propertyInfos.elementType

        val path = qPath.path

        var aliasType = when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> {
                qPath.propertyInfos.parametrizedTypes[0]
            }
            ElementType.MAP -> qPath.propertyInfos.parametrizedTypes[1]
            ElementType.ENTITY -> qPath.propertyInfos.type
            else -> throw IllegalArgumentException("Could not identify the alias type for the QPath of type '$elementType': $qPath")
        }

        val alias = dataSearchContext.get(aliasType)

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

        return join
    }

    private fun <E> join(elementType: ElementType, path: Path<*>, alias: QEntity<E>): QEntity<*> {
        when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> query.join(path as CollectionExpression<*, E>, alias)
            ElementType.MAP -> query.join(path as MapExpression<*, E>, alias)
            ElementType.ENTITY -> query.join(path as QEntity<E>, alias)
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
        val expressionValue = Expressions.constant(value)
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
}