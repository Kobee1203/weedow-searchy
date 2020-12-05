package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.utils.Keyword.CURRENT_DATE
import com.weedow.spring.data.search.utils.MAP_KEY
import com.weedow.spring.data.search.utils.MAP_VALUE
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.junit.jupiter.MockitoExtension
import java.sql.Date
import javax.persistence.criteria.*
import javax.persistence.criteria.Expression

@ExtendWith(MockitoExtension::class)
internal class SimpleExpressionTest {

    @Test
    fun to_specification_with_equals_operator() {
        val fieldPath = "firstName"
        val fieldValue = "John"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<*>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literalExpression = mock<Expression<String>>()
        whenever(criteriaBuilder.literal(fieldValue)).thenReturn(literalExpression)
        whenever(criteriaBuilder.equal(path, literalExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_equals_operator_for_field_as_collection_type() {
        val fieldPath = "nickNames"
        val fieldValue = "Johnny"
        val fieldInfo = FieldInfo(fieldPath, "nickNames", Person::class.java)

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

    @CsvSource("$MAP_KEY,eyes", "$MAP_VALUE,blue")
    @ParameterizedTest
    fun to_specification_with_equals_operator_for_map_field_special_keys(specialKey: String, fieldValue: String) {
        val fieldPath = "characteristics.$specialKey"
        val fieldInfo = FieldInfo(fieldPath, specialKey, Map::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<MapJoin<Person, String, String>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.equal(path, fieldValue)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_equals_operator_and_null_value() {
        val fieldPath = "firstName"
        val fieldValue = NullValue
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

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
    fun to_specification_with_equals_operator_for_field_as_collection_type_and_null_value() {
        val fieldPath = "nickNames"
        val fieldValue = NullValue
        val fieldInfo = FieldInfo(fieldPath, "nickNames", Person::class.java)

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
    fun to_specification_with_equals_operator_for_map_field__and_null_value() {
        val fieldPath = "characteristics"
        val fieldValue = NullValue
        val fieldInfo = FieldInfo(fieldPath, "characteristics", Person::class.java)

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

    @ValueSource(strings = [MAP_KEY, MAP_VALUE])
    @ParameterizedTest
    fun to_specification_with_equals_operator_for_map_field_special_keys_and_null_value(specialKey: String) {
        val fieldPath = "characteristics.$specialKey"
        val fieldValue = NullValue
        val fieldInfo = FieldInfo(fieldPath, specialKey, Map::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<MapJoin<Person, String, String>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        whenever(criteriaBuilder.isNull(path)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_matches_operator() {
        val fieldPath = "firstName"
        val fieldValue = "Jo*"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<String>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literal = mock<Expression<String>>()
        whenever(criteriaBuilder.literal("Jo%")).thenReturn(literal)
        whenever(criteriaBuilder.like(path, literal)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.MATCHES, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_imatches_operator() {
        val fieldPath = "firstName"
        val fieldValue = "*JO*"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<String>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val lowerPath = mock<Path<String>>()
        whenever(criteriaBuilder.lower(path)).thenReturn(lowerPath)
        val literal = mock<Expression<String>>()
        whenever(criteriaBuilder.literal("%JO%")).thenReturn(literal)
        val lowerExpression = mock<Expression<String>>()
        whenever(criteriaBuilder.lower(literal)).thenReturn(lowerExpression)
        whenever(criteriaBuilder.like(lowerPath, lowerExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.IMATCHES, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_less_than_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literalExpression = mock<Expression<Double>>()
        whenever(criteriaBuilder.literal(fieldValue)).thenReturn(literalExpression)
        whenever(criteriaBuilder.lessThan(path, literalExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.LESS_THAN, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_less_than_or_equals_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literalExpression = mock<Expression<Double>>()
        whenever(criteriaBuilder.literal(fieldValue)).thenReturn(literalExpression)
        whenever(criteriaBuilder.lessThanOrEqualTo(path, literalExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.LESS_THAN_OR_EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_greater_than_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literalExpression = mock<Expression<Double>>()
        whenever(criteriaBuilder.literal(fieldValue)).thenReturn(literalExpression)
        whenever(criteriaBuilder.greaterThan(path, literalExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.GREATER_THAN, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_greater_than_or_equals_operator() {
        val fieldPath = "height"
        val fieldValue = 170.0
        val fieldInfo = FieldInfo(fieldPath, "height", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<Double>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val literalExpression = mock<Expression<Double>>()
        whenever(criteriaBuilder.literal(fieldValue)).thenReturn(literalExpression)
        whenever(criteriaBuilder.greaterThanOrEqualTo(path, literalExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
    }

    @Test
    fun to_specification_with_in_operator() {
        val fieldPath = "firstName"
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

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

    @ParameterizedTest
    @EnumSource(Operator::class)
    fun to_field_expressions(operator: Operator) {
        assertToFieldExpressions(false, operator)
        assertToFieldExpressions(true, operator)
    }

    @Test
    fun to_specification_with_equals_operator_and_current_date_value() {
        val fieldPath = "birthday"
        val fieldValue = CURRENT_DATE
        val fieldInfo = FieldInfo(fieldPath, "firstName", Person::class.java)

        val entityJoins = mock<EntityJoins>()

        val root = mock<Root<Person>>()
        val criteriaBuilder = mock<CriteriaBuilder>()

        val path = mock<Path<*>>()
        whenever(entityJoins.getPath(fieldInfo.fieldPath, root)).thenReturn(path)

        val predicate = mock<Predicate>()
        val dateExpression = mock<Expression<Date>>()
        whenever(criteriaBuilder.currentDate()).thenReturn(dateExpression)
        whenever(criteriaBuilder.equal(path, dateExpression)).thenReturn(predicate)

        val expression = SimpleExpression(Operator.EQUALS, fieldInfo, fieldValue)
        val specification = expression.toSpecification<Person>(entityJoins)

        val result = specification.toPredicate(root, mock(), criteriaBuilder)

        assertThat(result).isEqualTo(predicate)
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
