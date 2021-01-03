package com.weedow.spring.data.search.query.specification

import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.query.QueryBuilder

/**
 * Helper class to support specification compositions.
 *
 * @see Specification
 */
internal object SpecificationComposition {

    /**
     * Composes a new [Specification] from the given [Specification]s ([lhs], [rhs]) in parameter.
     *
     * If one of the given [Specification]s returns [Specification.NO_PREDICATE], the returned [Specification] is the other given [Specification].
     *
     * @param lhs left-hand side
     * @param rhs right-hand side
     * @param combiner combines the resulted [Predicate]s of the given [Specification]
     * @return a new [Specification]
     */
    fun <T> composed(
        lhs: Specification<T>,
        rhs: Specification<T>,
        combiner: (builder: QueryBuilder<T>, lhs: Predicate, rhs: Predicate) -> Predicate,
    ): Specification<T> {
        return Specification { builder: QueryBuilder<T> ->
            val leftPredicate = toPredicate(lhs, builder)
            val rightPredicate = toPredicate(rhs, builder) // ?: return otherPredicate

            when {
                leftPredicate == Specification.NO_PREDICATE -> rightPredicate
                rightPredicate == Specification.NO_PREDICATE -> leftPredicate
                else -> combiner(builder, leftPredicate, rightPredicate)
            }
        }
    }

    private fun <T> toPredicate(specification: Specification<T>, builder: QueryBuilder<T>): Predicate {
        return specification.toPredicate(builder)
    }

}