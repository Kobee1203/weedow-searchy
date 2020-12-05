package com.weedow.spring.data.search.querydsl.specification

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Visitor
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationComposition.composed

fun interface QueryDslSpecification<T> {

    companion object {

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
         * @return
         * @since 2.0
         */
        fun <T> not(spec: QueryDslSpecification<T>?): QueryDslSpecification<T> {
            return if (spec == null) QueryDslSpecification { NO_PREDICATE } else QueryDslSpecification { builder: QueryDslBuilder<T> -> builder.not(spec.toPredicate(builder)) }
        }

        /**
         * Simple static factory method to add some syntactic sugar around a [QueryDslSpecification].
         *
         * @param <T>
         * @param spec can be null.
         * @return
         * @since 2.0
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
     * @since 2.0
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
     * @since 2.0
     */
    @JvmDefault
    fun or(other: QueryDslSpecification<T>): QueryDslSpecification<T> {
        return composed(this, other) { builder: QueryDslBuilder<T>, left: Predicate, rhs: Predicate -> builder.or(left, rhs) }
    }

    fun toPredicate(queryDslBuilder: QueryDslBuilder<T>): Predicate

}