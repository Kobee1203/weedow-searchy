package com.weedow.spring.data.search.querydsl

import com.querydsl.core.JoinType
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.querydsl.querytype.QEntityJoin
import com.weedow.spring.data.search.querydsl.querytype.QEntityRoot
import com.weedow.spring.data.search.querydsl.querytype.QPath

/**
 * Interface to construct criteria queries, compound selections, expressions, predicates, orderings, joins.
 *
 * @param T The root type in the from clause
 */
interface QueryDslBuilder<T> {

    /**
     * Query Entity related to the root type in the from clause.
     */
    val qEntityRoot: QEntityRoot<T>

    /**
     * Specify whether duplicate query results will be eliminated.
     */
    fun distinct()

    /**
     * Create a join or a fetch join to the specified [QPath] using the given [join type][JoinType].
     *
     * @param qPath target of the join
     * @param joinType join type
     * @param fetched whether the join is fetched
     * @return [QEntityJoin] instance
     */
    fun join(qPath: QPath<*>, joinType: JoinType, fetched: Boolean): QEntityJoin<*>

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

    /**
     * Create a predicate for testing the arguments for equality.
     *
     * @param x Expression to check
     * @param value Any
     * @return equality predicate
     */
    fun equal(x: Expression<*>, value: Any): Predicate

    /**
     * Create a predicate to test whether the expression is null.
     *
     * @param x Expression to check
     * @return is-null predicate
     */
    fun isNull(x: Expression<*>): Predicate

    /**
     * Create a predicate for testing whether the expression satisfies the given pattern.
     *
     * @param x Expression to check
     * @param value pattern - string
     * @return like predicate
     */
    fun like(x: Expression<String>, value: String): Predicate

    /**
     * Create a predicate for testing whether the expression satisfies the given pattern, ignoring case.
     *
     * @param x Expression to check
     * @param value pattern - string
     * @return ilike predicate
     */
    fun ilike(x: Expression<String>, value: String): Predicate

    /**
     * Create a predicate for testing whether the first argument is less than the second.
     *
     * @param x Expression to check
     * @param value Any
     * @return less-than predicate
     */
    fun lessThan(x: Expression<*>, value: Any): Predicate

    /**
     * Create a predicate for testing whether the first argument is less than or equal to the second.
     *
     * @param x Expression to check
     * @param value Any
     * @return less-than-or-equal predicate
     */
    fun lessThanOrEquals(x: Expression<*>, value: Any): Predicate

    /**
     * Create a predicate for testing whether the first argument is greater than the second.
     *
     * @param x Expression to check
     * @param value Any
     * @return greater-than predicate
     */
    fun greaterThan(x: Expression<*>, value: Any): Predicate

    /**
     * Create a predicate for testing whether the first argument is greater than or equal to the second.
     *
     * @param x Expression to check
     * @param value Any
     * @return greater-than-or-equal predicate
     */
    fun greaterThanOrEquals(x: Expression<*>, value: Any): Predicate

    /**
     * Create a predicate for testing the arguments for equality.
     *
     * @param x Expression to check
     * @param values Collection of values
     * @return in predicate
     */
    fun `in`(x: Expression<*>, values: Collection<*>): Predicate

}