package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.criteria.*
import javax.persistence.criteria.Expression

@ExtendWith(MockitoExtension::class)
internal class SimpleExpressionTest {

    @Test
    fun to_specification_with_equals_operator() {
        val fieldPath = "firstName"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "John"

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<*>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.equal(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_equals_operator_for_ElementCollection_field() {
        val fieldPath = "nickNames"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("nickNames"), String::class.java)
        val fieldValue = "Johnny"

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Collection<*>>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.isMember(fieldValue, path)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_equals_operator_and_null_value() {
        val fieldPath = "firstName"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = NullValue

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<*>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.isNull(path)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_equals_operator_for_ElementCollection_field_and_null_value() {
        val fieldPath = "nickNames"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("nickNames"), String::class.java)
        val fieldValue = NullValue

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Collection<*>>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.isEmpty(path)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_contains_operator() {
        val fieldPath = "firstName"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "Jo"

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<String>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literal = mock<Expression<String>>()
        whenever(criteriaBuilder.literal("%$fieldValue%")).thenReturn(literal)
        whenever(criteriaBuilder.like(path, literal)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.CONTAINS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_icontains_operator() {
        val fieldPath = "firstName"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue = "jo"

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<String>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val lowerPath = mock<Path<String>>()
        whenever(criteriaBuilder.lower(path)).thenReturn(lowerPath)
        val literal = mock<Expression<String>>()
        whenever(criteriaBuilder.literal("%$fieldValue%")).thenReturn(literal)
        val lowerExpression = mock<Expression<String>>()
        whenever(criteriaBuilder.lower(literal)).thenReturn(lowerExpression)
        whenever(criteriaBuilder.like(lowerPath, lowerExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.ICONTAINS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_less_than_operator() {
        val fieldPath = "height"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170.0

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.lessThan(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_less_than_or_equals_operator() {
        val fieldPath = "height"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170.0

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.lessThanOrEqualTo(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.LESS_THAN_OR_EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_greater_than_operator() {
        val fieldPath = "height"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170.0

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.greaterThan(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_greater_than_or_equals_operator() {
        val fieldPath = "height"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("height"), Double::class.java)
        val fieldValue = 170.0

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.greaterThanOrEqualTo(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_in_operator() {
        val fieldPath = "firstName"
        val fieldInfo = FieldInfo(fieldPath, Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java)
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<*>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val inClause = mock<CriteriaBuilder.In<Any>>()
        whenever(criteriaBuilder.`in`(path)).thenReturn(inClause)

        val expression = SimpleExpression(Operator.IN, fieldInfo, listOf(fieldValue1, fieldValue2))
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(inClause)

        verify(inClause).value(fieldValue1)
        verify(inClause).value(fieldValue2)
    }
}