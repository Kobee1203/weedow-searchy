package com.weedow.spring.data.search.expression

import com.neovisionaries.i18n.CountryCode
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Address
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.expression.parser.ExpressionParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class ExpressionMapperImplTest {

    @Mock
    private lateinit var expressionResolver: ExpressionResolver

    @Mock
    private lateinit var expressionParser: ExpressionParser

    @InjectMocks
    private lateinit var expressionMapper: ExpressionMapperImpl

    @Test
    fun to_root_expression_without_params() {
        val rootClass = Person::class.java
        val params = mapOf<String, List<String>>()

        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).isEmpty()

        verifyZeroInteractions(expressionResolver)
        verifyZeroInteractions(expressionParser)
    }

    @Test
    fun to_root_expression_with_single_param_value() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldValue = "John"
        val fieldValues = listOf(fieldValue)
        val operator = Operator.EQUALS

        val expression = ExpressionUtils.equals(FieldInfo(fieldPath, fieldName, rootClass), fieldValue)
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, fieldValues, operator, false)).thenReturn(expression)

        val params = mapOf(fieldPath to fieldValues)
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(expression)

        verifyZeroInteractions(expressionParser)
    }

    @Test
    fun to_root_expression_with_multiple_param_values() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldValues = listOf("John", "Jane")
        val operator = Operator.IN

        val expression = ExpressionUtils.`in`(FieldInfo(fieldPath, fieldName, rootClass), fieldValues)
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, fieldValues, operator, false)).thenReturn(expression)

        val params = mapOf(fieldPath to fieldValues)
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(expression)

        verifyZeroInteractions(expressionParser)
    }

    @Test
    fun to_root_expression_with_multiple_params() {
        val rootClass = Person::class.java

        val parentClass1 = Person::class.java
        val fieldPath1 = "firstName"
        val fieldName1 = "firstName"
        val fieldValues1 = listOf("John", "Jane")
        val operator1 = Operator.IN

        val parentClass2 = Address::class.java
        val fieldPath2 = "addressEntities.country"
        val fieldName2 = "country"
        val fieldValues2 = listOf("FR")
        val operator2 = Operator.EQUALS

        val expression1 = ExpressionUtils.`in`(FieldInfo(fieldPath1, fieldName1, parentClass1), fieldValues1)
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath1, fieldValues1, operator1, false)).thenReturn(expression1)

        val expression2 = ExpressionUtils.equals(FieldInfo(fieldPath2, fieldName2, parentClass2), CountryCode.FR)
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath2, fieldValues2, operator2, false)).thenReturn(expression2)

        val params = mapOf(
            fieldPath1 to fieldValues1,
            fieldPath2 to fieldValues2
        )
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(
            expression1,
            expression2
        )

        verifyZeroInteractions(expressionParser)
    }

    @Test
    fun to_root_expression_with_special_query_param() {
        val rootClass = Person::class.java

        val parentClass1 = Person::class.java
        val fieldPath1 = "firstName"
        val fieldName1 = "firstName"
        val fieldValues1 = listOf("John")

        val parentClass2 = Address::class.java
        val fieldPath2 = "addressEntities.country"
        val fieldName2 = "country"
        val fieldValues2 = listOf(CountryCode.FR)

        val query = "$fieldPath1='${fieldValues1[0]}' AND $fieldPath2='${fieldValues2[0]}'"
        val fieldValues = listOf(query)

        val expression = ExpressionUtils.and(
            ExpressionUtils.equals(FieldInfo(fieldPath1, fieldName1, parentClass1), fieldValues1),
            ExpressionUtils.equals(FieldInfo(fieldPath2, fieldName2, parentClass2), fieldValues2)
        )
        whenever(expressionParser.parse(query, rootClass)).thenReturn(expression)

        val params = mapOf("query" to fieldValues)
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(expression)

        verifyZeroInteractions(expressionResolver)
    }

}