package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.join.EntityJoins
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@ExtendWith(MockitoExtension::class)
internal class JpaNotExpressionTest {

    @Test
    fun to_specification() {
        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Any>>()
        val criteriaQuery = mock<CriteriaQuery<*>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val mockExpression = mock<Expression>()
        val mockSpecification = mock<Specification<Any>>()
        whenever(mockExpression.toSpecification<Any>(entityJoins)).thenReturn(mockSpecification)

        val mockPredicate = mock<Predicate>()
        whenever(mockSpecification.toPredicate(root, criteriaQuery, criteriaBuilder)).thenReturn(mockPredicate)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.not(mockPredicate)).thenReturn(predicate)

        val expression = NotExpression(mockExpression)
        val specification = expression.toSpecification<Any>(entityJoins)

        val result = specification.toPredicate(root, criteriaQuery, criteriaBuilder)

        Assertions.assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_field_expressions() {
        assertToFieldExpressions(false)
        assertToFieldExpressions(true)
    }

    private fun assertToFieldExpressions(negated: Boolean) {
        val fieldExpression = mock<FieldExpression>()
        val mockExpression = mock<Expression> {
            on { this.toFieldExpressions(!negated) }.doReturn(listOf(fieldExpression))
        }

        val expression = NotExpression(mockExpression)

        val fieldExpressions = expression.toFieldExpressions(negated)

        Assertions.assertThat(fieldExpressions).containsExactly(fieldExpression)
    }
}