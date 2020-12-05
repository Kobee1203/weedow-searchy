package com.weedow.spring.data.search.querydsl

import com.querydsl.core.JoinType
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.querydsl.querytype.QEntity
import com.weedow.spring.data.search.querydsl.querytype.QPath

interface QueryDslBuilder<T> {

    val qRootEntity: QEntity<out T>

    fun distinct()

    fun join(qPath: QPath<*>, joinType: JoinType, fetched: Boolean): QEntity<*>

    /**
     * Create a conjunction of the given boolean expressions.
     *
     * @param x boolean expression
     * @param y boolean expression
     * @return and predicate
     */
    fun and(x: Expression<Boolean>, y: Expression<Boolean>): Predicate

    /**
     * Create a conjunction of the given restriction predicates.
     *
     * A conjunction of zero predicates is true.
     *
     * @param restrictions zero or more restriction predicates
     * @return and predicate
     */
    fun and(vararg restrictions: Predicate): Predicate

    /**
     * Create a disjunction of the given boolean expressions.
     *
     * @param x boolean expression
     * @param y boolean expression
     * @return or predicate
     */
    fun or(x: Expression<Boolean>, y: Expression<Boolean>): Predicate

    /**
     * Create a disjunction of the given restriction predicates.
     *
     * A disjunction of zero predicates is false.
     *
     * @param restrictions zero or more restriction predicates
     * @return or predicate
     */
    fun or(vararg restrictions: Predicate): Predicate

    /**
     * Create a negation of the given restriction.
     *
     * @param restriction restriction expression
     * @return not predicate
     */
    fun not(restriction: Expression<Boolean>): Predicate
    fun equal(path: Path<*>, value: Any): Predicate
    fun isNull(path: Path<*>): Predicate
}