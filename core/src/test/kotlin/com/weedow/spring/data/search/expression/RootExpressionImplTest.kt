package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.join.EntityJoin
import com.weedow.spring.data.search.join.EntityJoins
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.*

@ExtendWith(MockitoExtension::class)
internal class RootExpressionImplTest {

    @Test
    fun to_specification_with_no_expressions_and_no_entity_joins() {
        val entityJoins = mock<EntityJoins>()
        whenever(entityJoins.getJoins(RootExpressionImpl.FILTER_FETCH_JOINS)).thenReturn(emptyMap())

        val root = mock<Root<Any>>()
        val criteriaQuery = mock<CriteriaQuery<*>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val rootExpression = RootExpressionImpl<Any>()
        val specification = rootExpression.toSpecification<Any>(entityJoins)

        val predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder)

        assertThat(predicate).isNull()

        verify(entityJoins, times(0)).getPath(any(), eq(root))
        verify(criteriaQuery).distinct(true)
        verifyZeroInteractions(root)
        verifyNoMoreInteractions(criteriaQuery)
        verifyZeroInteractions(criteriaBuilder)
    }

    @Test
    fun to_specification_with_expressions_and_no_entity_joins() {
        val entityJoins = mock<EntityJoins>()
        whenever(entityJoins.getJoins(RootExpressionImpl.FILTER_FETCH_JOINS)).thenReturn(emptyMap())

        val root = mock<Root<Any>>()
        val criteriaQuery = mock<CriteriaQuery<*>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val expression1 = mock<Expression>()
        val specification1 = mock<Specification<Any>>()
        whenever(expression1.toSpecification<Any>(entityJoins)).thenReturn(specification1)
        val predicate1 = mock<Predicate>()
        whenever(specification1.toPredicate(root, criteriaQuery, criteriaBuilder)).thenReturn(predicate1)
        val expression2 = mock<Expression>()
        val specification2 = mock<Specification<Any>>()
        whenever(expression2.toSpecification<Any>(entityJoins)).thenReturn(specification2)
        val predicate2 = mock<Predicate>()
        whenever(specification2.toPredicate(root, criteriaQuery, criteriaBuilder)).thenReturn(predicate2)

        val predicate = mock<Predicate>()
        val mockPredicateExpression1 = predicate1 as javax.persistence.criteria.Expression<Boolean>
        val mockPredicateExpression2 = predicate2 as javax.persistence.criteria.Expression<Boolean>
        whenever(criteriaBuilder.and(mockPredicateExpression2, mockPredicateExpression1)).thenReturn(predicate)

        val rootExpression = RootExpressionImpl<Any>(expression1, expression2)
        val specification = rootExpression.toSpecification<Any>(entityJoins)

        val result = specification.toPredicate(root, criteriaQuery, criteriaBuilder)

        assertThat(result).isEqualTo(predicate)

        verify(entityJoins, never()).getPath(any(), eq(root))
        verify(criteriaQuery).distinct(true)
        verifyZeroInteractions(root)
        verifyNoMoreInteractions(criteriaQuery)
    }

    @Test
    fun to_specification_with_no_expressions_and_entity_joins() {
        val entityJoins = mock<EntityJoins>()
        val fieldPath1 = "entity.myJoin1"
        val fieldPath2 = "entity.myJoin2"
        val fetchJoins = mapOf(
                "myJoin1" to EntityJoin(fieldPath1, "myJoin1", "myJoin1", JoinType.LEFT, true),
                "myJoin2" to EntityJoin(fieldPath2, "myJoin2", "myJoin2", JoinType.LEFT, true)
        )
        whenever(entityJoins.getJoins(RootExpressionImpl.FILTER_FETCH_JOINS)).thenReturn(fetchJoins)

        val root = mock<Root<Any>>()
        val criteriaQuery = mock<CriteriaQuery<*>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val rootExpression = RootExpressionImpl<Any>()
        val specification = rootExpression.toSpecification<Any>(entityJoins)

        val predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder)

        assertThat(predicate).isNull()

        verify(entityJoins).getPath(fieldPath1, root)
        verify(entityJoins).getPath(fieldPath2, root)
        verify(criteriaQuery).distinct(true)
        verifyZeroInteractions(root)
        verifyNoMoreInteractions(criteriaQuery)
        verifyZeroInteractions(criteriaBuilder)
    }
}