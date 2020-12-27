package com.weedow.spring.data.search.querydsl.specification

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Visitor
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationComposition.composed

/**
 * Specification in the sense of Domain Driven Design.
 */
fun interface QueryDslSpecification<T> {

    companion object {

        /**
         * [Predicate] which does nothing. It's useful when a [QueryDslSpecification] is `null` or when the [QueryDslSpecification]'s processing doesn't return a [Predicate].
         */
        val NO_PREDICATE: Predicate = object : Predicate {
            override fun <R : Any?, C : Any?> accept(v: Visitor<R, C>?, context: C?): R? = null

            override fun getType(): Class<out Boolean> = Boolean::class.java

            override fun not(): Predicate = this
        }

        /**
         * Negates the given [QueryDslSpecification].
         *
         * @param <T>
         * @param spec can be null.
         * @return The negation of the specification
         */
        fun <T> not(spec: QueryDslSpecification<T>?): QueryDslSpecification<T> {
            return if (spec == null) QueryDslSpecification { NO_PREDICATE } else QueryDslSpecification { builder: QueryDslBuilder<T> ->
                builder.not(
                    spec.toPredicate(
                        builder
                    )
                )
            }
        }

        /**
         * Simple static factory method to add some syntactic sugar around a [QueryDslSpecification].
         *
         * @param <T>
         * @param spec can be null.
         * @return the given specification or a new [QueryDslSpecification] that contains [NO_PREDICATE]
         */
        fun <T> where(spec: QueryDslSpecification<T>?): QueryDslSpecification<T> {
            return spec ?: QueryDslSpecification { NO_PREDICATE }
        }
    }

    /**
     * ANDs the given [QueryDslSpecification] to the current one.
     *
     * @param other can be null.
     * @return The conjunction of the specifications
     */
    @JvmDefault
    fun and(other: QueryDslSpecification<T>): QueryDslSpecification<T> {
        return composed(this, other) { builder: QueryDslBuilder<T>, left: Predicate, rhs: Predicate -> builder.and(left, rhs) }
    }

    /**
     * ORs the given specification to the current one.
     *
     * @param other can be null.
     * @return The disjunction of the specifications
     */
    @JvmDefault
    fun or(other: QueryDslSpecification<T>): QueryDslSpecification<T> {
        return composed(this, other) { builder: QueryDslBuilder<T>, left: Predicate, rhs: Predicate -> builder.or(left, rhs) }
    }

    /**
     * Creates a WHERE clause for a query of the referenced entity in form of a [Predicate] for the given [QueryDslBuilder].
     *
     * @param queryDslBuilder [QueryDslBuilder] instance
     * @return a [Predicate]
     */
    fun toPredicate(queryDslBuilder: QueryDslBuilder<T>): Predicate

}