package com.weedow.searchy.expression

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.JoinType
import com.querydsl.core.types.Predicate
import com.weedow.searchy.join.EntityJoin
import com.weedow.searchy.join.EntityJoins
import com.weedow.searchy.query.QueryBuilder
import com.weedow.searchy.query.querytype.QEntityRoot
import com.weedow.searchy.query.specification.Specification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class RootExpressionImplTest {

    @Test
    fun to_specification_with_no_expressions_and_no_entity_joins() {
        val entityJoins = mock<EntityJoins>()
        whenever(entityJoins.getJoins(RootExpressionImpl.FILTER_FETCH_JOINS)).thenReturn(emptyMap())

        val queryBuilder = mock<QueryBuilder<Any>>()

        val rootExpression = RootExpressionImpl<Any>()
        val specification = rootExpression.toSpecification<Any>(entityJoins)

        val predicate = specification.toPredicate(queryBuilder)

        assertThat(predicate).isEqualTo(Specification.NO_PREDICATE)

        verify(entityJoins, never()).getQPath(any(), any(), eq(queryBuilder))
        verify(queryBuilder).distinct()
        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun to_specification_with_expressions_and_no_entity_joins() {
        val entityJoins = mock<EntityJoins>()
        whenever(entityJoins.getJoins(RootExpressionImpl.FILTER_FETCH_JOINS)).thenReturn(emptyMap())

        val queryBuilder = mock<QueryBuilder<Any>>()

        val expression1 = mock<Expression>()
        val specification1 = mock<Specification<Any>>()
        whenever(expression1.toSpecification<Any>(entityJoins)).thenReturn(specification1)
        val predicate1 = mock<Predicate>()
        whenever(specification1.toPredicate(queryBuilder)).thenReturn(predicate1)
        val expression2 = mock<Expression>()
        val specification2 = mock<Specification<Any>>()
        whenever(expression2.toSpecification<Any>(entityJoins)).thenReturn(specification2)
        val predicate2 = mock<Predicate>()
        whenever(specification2.toPredicate(queryBuilder)).thenReturn(predicate2)

        val predicate = mock<Predicate>()
        whenever(queryBuilder.and(predicate1, predicate2)).thenReturn(predicate)

        val rootExpression = RootExpressionImpl<Any>(expression1, expression2)
        val specification = rootExpression.toSpecification<Any>(entityJoins)

        val result = specification.toPredicate(queryBuilder)

        assertThat(result).isEqualTo(predicate)

        verify(entityJoins, never()).getQPath(any(), any(), eq(queryBuilder))
        verify(queryBuilder).distinct()
        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun to_specification_with_no_expressions_and_entity_joins() {
        val entityJoins = mock<EntityJoins>()
        val fieldPath1 = "entity.myJoin1"
        val fieldPath2 = "entity.myJoin2"
        val fetchJoins = mapOf(
            "myJoin1" to EntityJoin(fieldPath1, "myJoin1", JoinType.LEFTJOIN, true),
            "myJoin2" to EntityJoin(fieldPath2, "myJoin2", JoinType.LEFTJOIN, true)
        )
        whenever(entityJoins.getJoins(RootExpressionImpl.FILTER_FETCH_JOINS)).thenReturn(fetchJoins)

        val qEntityRoot = mock<QEntityRoot<Any>>()
        val queryBuilder = mock<QueryBuilder<Any>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val rootExpression = RootExpressionImpl<Any>()
        val specification = rootExpression.toSpecification<Any>(entityJoins)

        val predicate = specification.toPredicate(queryBuilder)

        assertThat(predicate).isEqualTo(Specification.NO_PREDICATE)

        verify(entityJoins).getQPath(fieldPath1, qEntityRoot, queryBuilder)
        verify(entityJoins).getQPath(fieldPath2, qEntityRoot, queryBuilder)
        verify(queryBuilder).distinct()
        verifyNoMoreInteractions(queryBuilder)
    }

    @Test
    fun to_field_expressions() {
        assertToFieldExpressions(false)
        assertToFieldExpressions(true)
    }

    private fun assertToFieldExpressions(negated: Boolean) {
        val fieldExpression1 = mock<FieldExpression>()
        val mockExpression1 = mock<Expression> {
            on { this.toFieldExpressions(negated) }.doReturn(listOf(fieldExpression1))
        }
        val fieldExpression2 = mock<FieldExpression>()
        val mockExpression2 = mock<Expression> {
            on { this.toFieldExpressions(negated) }.doReturn(listOf(fieldExpression2))
        }

        val rootExpression = RootExpressionImpl<Any>(mockExpression1, mockExpression2)
        val fieldExpressions = rootExpression.toFieldExpressions(negated)

        assertThat(fieldExpressions).containsExactly(fieldExpression1, fieldExpression2)
    }
}