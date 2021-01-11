package com.weedow.searchy.query.specification

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Visitor
import com.weedow.searchy.query.QueryBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class SpecificationTest {

    @Test
    fun test_no_predicate() {
        val noPredicate = Specification.NO_PREDICATE

        val visitor = mock<Visitor<Any, in Any>>()
        val context = mock<Any>()

        assertThat(noPredicate.accept(visitor, context)).isNull()
        assertThat(noPredicate.type).isEqualTo(Boolean::class.java)
        assertThat(noPredicate.not()).isSameAs(noPredicate)

        verifyZeroInteractions(visitor)
        verifyZeroInteractions(context)
    }

    @Test
    fun test_not_with_null_specification() {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val result = Specification.not<Any>(null).toPredicate(queryBuilder)

        assertThat(result).isSameAs(Specification.NO_PREDICATE)

        verifyZeroInteractions(queryBuilder)
    }

    @Test
    fun test_not() {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val predicate = mock<Predicate>()
        val spec = mock<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate)
        }

        val notPredicate = mock<Predicate>()
        whenever(queryBuilder.not(predicate)).thenReturn(notPredicate)

        val result = Specification.not(spec).toPredicate(queryBuilder)

        assertThat(result).isSameAs(notPredicate)

        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun test_where_with_null_specification() {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val result = Specification.where<Any>(null).toPredicate(queryBuilder)

        assertThat(result).isSameAs(Specification.NO_PREDICATE)

        verifyZeroInteractions(queryBuilder)
    }

    @Test
    fun test_where() {
        val spec = mock<Specification<Any>>()

        val result = Specification.where(spec)

        assertThat(result).isSameAs(spec)
    }

    @Test
    fun test_and() {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val predicate1 = mock<Predicate>()
        val specification1 = spy<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate1)
        }

        val predicate2 = mock<Predicate>()
        val specification2 = spy<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate2)
        }

        val predicate = mock<Predicate>()
        whenever(queryBuilder.and(predicate1, predicate2)).thenReturn(predicate)

        val result = specification1.and(specification2).toPredicate(queryBuilder)

        assertThat(result).isSameAs(predicate)

        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun test_or() {
        val queryBuilder = mock<QueryBuilder<Any>>()

        val predicate1 = mock<Predicate>()
        val specification1 = spy<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate1)
        }

        val predicate2 = mock<Predicate>()
        val specification2 = spy<Specification<Any>> {
            on { this.toPredicate(queryBuilder) }.thenReturn(predicate2)
        }

        val predicate = mock<Predicate>()
        whenever(queryBuilder.or(predicate1, predicate2)).thenReturn(predicate)

        val result = specification1.or(specification2).toPredicate(queryBuilder)

        assertThat(result).isSameAs(predicate)

        verifyNoMoreInteractions(queryBuilder)
    }
}