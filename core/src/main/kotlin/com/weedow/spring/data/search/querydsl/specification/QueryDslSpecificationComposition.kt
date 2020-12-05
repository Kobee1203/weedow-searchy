package com.weedow.spring.data.search.querydsl.specification

import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.querydsl.QueryDslBuilder

/**
 * Helper class to support specification compositions.
 *
 * @see QueryDslSpecification
 */
internal object QueryDslSpecificationComposition {

    fun <T> composed(
            lhs: QueryDslSpecification<T>,
            rhs: QueryDslSpecification<T>,
            combiner: (builder: QueryDslBuilder<T>, lhs: Predicate, rhs: Predicate) -> Predicate,
    ): QueryDslSpecification<T> {
        return QueryDslSpecification { builder: QueryDslBuilder<T> ->
            val otherPredicate = toPredicate(lhs, builder)
            val thisPredicate = toPredicate(rhs, builder) // ?: return otherPredicate

            when {
                thisPredicate == QueryDslSpecification.NO_PREDICATE -> otherPredicate
                otherPredicate == QueryDslSpecification.NO_PREDICATE -> thisPredicate
                else -> combiner(builder, thisPredicate, otherPredicate)
            }
        }
    }

    private fun <T> toPredicate(specification: QueryDslSpecification<T>, builder: QueryDslBuilder<T>): Predicate {
        return specification.toPredicate(builder)
    }

}