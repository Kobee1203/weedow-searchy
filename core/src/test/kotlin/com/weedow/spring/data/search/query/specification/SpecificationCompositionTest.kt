package com.weedow.spring.data.search.query.specification

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.query.QueryBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class SpecificationCompositionTest {

    @Test
    fun composed() {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val predicate1 = mock<Predicate>()
        val spec1 = mock<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate1)
        }

        val predicate2 = mock<Predicate>()
        val spec2 = mock<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate2)
        }

        val predicate = mock<Predicate>()
        val combiner = mock<(builder: QueryBuilder<Any>, lhs: Predicate, rhs: Predicate) -> Predicate> {
            on { this.invoke(queryBuilder, predicate1, predicate2) }.thenReturn(predicate)
        }

        val result = SpecificationComposition.composed(spec1, spec2, combiner).toPredicate(queryBuilder)

        assertThat(result).isSameAs(predicate)
    }

    @Test
    fun composed_with_left_predicate_equals_to_no_predicate() {
        val predicate1 = Specification.NO_PREDICATE
        val predicate2 = mock<Predicate>()

        verify_composed(predicate1, predicate2, predicate2)
    }

    @Test
    fun composed_with_right_predicate_equals_to_no_predicate() {
        val predicate1 = mock<Predicate>()
        val predicate2 = Specification.NO_PREDICATE

        verify_composed(predicate1, predicate2, predicate1)
    }

    @Test
    fun composed_with_both_predicates_equal_to_no_predicate() {
        val predicate1 = Specification.NO_PREDICATE
        val predicate2 = Specification.NO_PREDICATE

        verify_composed(predicate1, predicate2, predicate2)
    }

    private fun verify_composed(
        predicate1: Predicate,
        predicate2: Predicate,
        expectedPredicate: Predicate
    ) {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val spec1 = mock<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate1)
        }

        val spec2 = mock<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate2)
        }

        val combiner = mock<(builder: QueryBuilder<Any>, lhs: Predicate, rhs: Predicate) -> Predicate>()

        val result = SpecificationComposition.composed(spec1, spec2, combiner).toPredicate(queryBuilder)

        assertThat(result).isSameAs(expectedPredicate)

        verifyZeroInteractions(combiner)
    }
}