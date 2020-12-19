package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.querytype.QEntityRoot
import com.weedow.spring.data.search.querydsl.querytype.QPath
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class SimpleExpressionTest {

    @Test
    fun to_specification_with_equals_operator() {
        val fieldPath = "firstName"
        val fieldValue = "John"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.equal(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_equals_operator_and_null_value() {
        val fieldPath = "firstName"
        val fieldValue = NullValue
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.isNull(path)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_matches_operator() {
        val fieldPath = "firstName"
        val fieldValue = "Jo*"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<String>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.like(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.MATCHES, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_imatches_operator() {
        val fieldPath = "firstName"
        val fieldValue = "*JO*"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<String>>()
        val qPath = mock<QPath<String>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.ilike(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.IMATCHES, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_less_than_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.lessThan(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_less_than_or_equals_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.lessThanOrEquals(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.LESS_THAN_OR_EQUALS, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_greater_than_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.greaterThan(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_greater_than_or_equals_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.greaterThanOrEquals(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, fieldInfo, fieldValue)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_in_operator() {
        val fieldPath = "firstName"
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val fieldValues = listOf(fieldValue1, fieldValue2)
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val qEntityRoot = mock<QEntityRoot<Person>>()
        val queryDslBuilder = mock<QueryDslBuilder<Person>> {
            on { this.qEntityRoot }.thenReturn(qEntityRoot)
        }

        val path = mock<Path<*>>()
        val qPath = mock<QPath<*>> {
            on { this.path }.thenReturn(path)
        }
        whenever(entityJoins.getQPath(fieldInfo.fieldPath, qEntityRoot, queryDslBuilder)).thenReturn(qPath)

        val predicate = mock<Predicate>()
        whenever(queryDslBuilder.`in`(path, fieldValues)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.IN, fieldInfo, fieldValues)
        val specification = expression.toQueryDslSpecification<Person>(entityJoins)

        val result = specification.toPredicate(queryDslBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @ParameterizedTest
    @EnumSource(Operator::class)
    fun to_field_expressions(operator: Operator) {
        assertToFieldExpressions(false, operator)
        assertToFieldExpressions(true, operator)
    }

    private fun assertToFieldExpressions(negated: Boolean, operator: Operator) {
        val fieldPath = "firstName"
        val fieldValue = "John"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val expression = SimpleExpression(operator, fieldInfo, fieldValue)
        val fieldExpressions = expression.toFieldExpressions(negated)

        assertThat(fieldExpressions)
            .extracting("fieldInfo", "value", "operator", "negated")
            .containsExactly(Tuple.tuple(fieldInfo, fieldValue, operator, negated))
    }
}
