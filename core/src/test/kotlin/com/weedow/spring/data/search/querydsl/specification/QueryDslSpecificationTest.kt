package com.weedow.spring.data.search.querydsl.specification

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Visitor
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class QueryDslSpecificationTest {

    @Test
    fun test_no_predicate() {
        val noPredicate = QueryDslSpecification.NO_PREDICATE

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
        val queryDslBuilder = mock<QueryDslBuilder<Any>>()

        val result = QueryDslSpecification.not<Any>(null).toPredicate(queryDslBuilder)

        assertThat(result).isSameAs(QueryDslSpecification.NO_PREDICATE)

        verifyZeroInteractions(queryDslBuilder)
    }

    @Test
    fun test_not() {
        val queryDslBuilder = mock<QueryDslBuilder<Any>>()

        val predicate = mock<Predicate>()
        val spec = mock<QueryDslSpecification<Any>> {
            on { this.toPredicate(queryDslBuilder) }.thenReturn(predicate)
        }

        val notPredicate = mock<Predicate>()
        whenever(queryDslBuilder.not(predicate)).thenReturn(notPredicate)

        val result = QueryDslSpecification.not(spec).toPredicate(queryDslBuilder)

        assertThat(result).isSameAs(notPredicate)

        verifyNoMoreInteractions(queryDslBuilder)
    }

    @Test
    fun test_where_with_null_specification() {
        val queryDslBuilder = mock<QueryDslBuilder<Any>>()

        val result = QueryDslSpecification.where<Any>(null).toPredicate(queryDslBuilder)

        assertThat(result).isSameAs(QueryDslSpecification.NO_PREDICATE)

        verifyZeroInteractions(queryDslBuilder)
    }

    @Test
    fun test_where() {
        val spec = mock<QueryDslSpecification<Any>>()

        val result = QueryDslSpecification.where(spec)

        assertThat(result).isSameAs(spec)
    }

    @Test
    fun test_and() {
        val queryDslBuilder = mock<QueryDslBuilder<Any>>()

        val predicate1 = mock<Predicate>()
        val queryDslSpecification1 = spy<QueryDslSpecification<Any>> {
            on { this.toPredicate(queryDslBuilder) }.thenReturn(predicate1)
        }

        val predicate2 = mock<Predicate>()
        val queryDslSpecification2 = spy<QueryDslSpecification<Any>> {
            on { this.toPredicate(queryDslBuilder) }.thenReturn(predicate2)
        }

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.and(predicate1, predicate2)).thenReturn(predicate)

        val result = queryDslSpecification1.and(queryDslSpecification2).toPredicate(queryDslBuilder)

        assertThat(result).isSameAs(predicate)

        verifyNoMoreInteractions(queryDslBuilder)
    }

    @Test
    fun test_or() {
        val queryDslBuilder = mock<QueryDslBuilder<Any>>()

        val predicate1 = mock<Predicate>()
        val queryDslSpecification1 = spy<QueryDslSpecification<Any>> {
            on { this.toPredicate(queryDslBuilder) }.thenReturn(predicate1)
        }

        val predicate2 = mock<Predicate>()
        val queryDslSpecification2 = spy<QueryDslSpecification<Any>> {
            on { this.toPredicate(queryDslBuilder) }.thenReturn(predicate2)
        }

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.or(predicate1, predicate2)).thenReturn(predicate)

        val result = queryDslSpecification1.or(queryDslSpecification2).toPredicate(queryDslBuilder)

        assertThat(result).isSameAs(predicate)

        verifyNoMoreInteractions(queryDslBuilder)
    }
}