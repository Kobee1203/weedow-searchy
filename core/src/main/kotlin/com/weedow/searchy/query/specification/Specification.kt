package com.weedow.searchy.query.specification

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Visitor
import com.weedow.searchy.query.QueryBuilder
import com.weedow.searchy.query.specification.SpecificationComposition.composed

/**
 * Specification in the sense of Domain Driven Design.
 */
fun interface Specification<T> {

    companion object {

        /**
         * [Predicate] which does nothing. It's useful when a [Specification] is `null` or when the [Specification]'s processing doesn't return a [Predicate].
         */
        val NO_PREDICATE: Predicate = object : Predicate {
            override fun <R : Any?, C : Any?> accept(v: Visitor<R, C>?, context: C?): R? = null

            override fun getType(): Class<out Boolean> = Boolean::class.java

            override fun not(): Predicate = this
        }

        /**
         * Negates the given [Specification].
         *
         * @param <T>
         * @param spec can be null.
         * @return The negation of the specification
         */
        fun <T> not(spec: Specification<T>?): Specification<T> {
            return if (spec == null) Specification { NO_PREDICATE } else Specification { builder: QueryBuilder<T> ->
                builder.not(
                    spec.toPredicate(
                        builder
                    )
                )
            }
        }

        /**
         * Simple static factory method to add some syntactic sugar around a [Specification].
         *
         * @param <T>
         * @param spec can be null.
         * @return the given specification or a new [Specification] that contains [NO_PREDICATE]
         */
        fun <T> where(spec: Specification<T>?): Specification<T> {
            return spec ?: Specification { NO_PREDICATE }
        }
    }

    /**
     * ANDs the given [Specification] to the current one.
     *
     * @param other can be null.
     * @return The conjunction of the specifications
     */
    @JvmDefault
    fun and(other: Specification<T>): Specification<T> {
        return composed(this, other) { builder: QueryBuilder<T>, left: Predicate, rhs: Predicate -> builder.and(left, rhs) }
    }

    /**
     * ORs the given specification to the current one.
     *
     * @param other can be null.
     * @return The disjunction of the specifications
     */
    @JvmDefault
    fun or(other: Specification<T>): Specification<T> {
        return composed(this, other) { builder: QueryBuilder<T>, left: Predicate, rhs: Predicate -> builder.or(left, rhs) }
    }

    /**
     * Creates a WHERE clause for a query of the referenced entity in form of a [Predicate] for the given [QueryBuilder].
     *
     * @param queryBuilder [QueryBuilder] instance
     * @return a [Predicate]
     */
    fun toPredicate(queryBuilder: QueryBuilder<T>): Predicate

}