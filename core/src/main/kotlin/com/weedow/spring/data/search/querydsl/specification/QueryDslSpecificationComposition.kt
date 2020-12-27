package com.weedow.spring.data.search.querydsl.specification

import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.querydsl.QueryDslBuilder

/**
 * Helper class to support specification compositions.
 *
 * @see QueryDslSpecification
 */
internal object QueryDslSpecificationComposition {

    /**
     * Composes a new [QueryDslSpecification] from the given [QueryDslSpecification]s ([lhs], [rhs]) in parameter.
     *
     * If one of the given [QueryDslSpecification]s returns [QueryDslSpecification.NO_PREDICATE], the returned [QueryDslSpecification] is the other given [QueryDslSpecification].
     *
     * @param lhs left-hand side
     * @param rhs right-hand side
     * @param combiner combines the resulted [Predicate]s of the given [QueryDslSpecification]
     * @return a new [QueryDslSpecification]
     */
    fun <T> composed(
        lhs: QueryDslSpecification<T>,
        rhs: QueryDslSpecification<T>,
        combiner: (builder: QueryDslBuilder<T>, lhs: Predicate, rhs: Predicate) -> Predicate,
    ): QueryDslSpecification<T> {
        return QueryDslSpecification { builder: QueryDslBuilder<T> ->
            val leftPredicate = toPredicate(lhs, builder)
            val rightPredicate = toPredicate(rhs, builder) // ?: return otherPredicate

            when {
                leftPredicate == QueryDslSpecification.NO_PREDICATE -> rightPredicate
                rightPredicate == QueryDslSpecification.NO_PREDICATE -> leftPredicate
                else -> combiner(builder, leftPredicate, rightPredicate)
            }
        }
    }

    private fun <T> toPredicate(specification: QueryDslSpecification<T>, builder: QueryDslBuilder<T>): Predicate {
        return specification.toPredicate(builder)
    }

}