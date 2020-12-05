package com.weedow.spring.data.search.expression

import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Address
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.fieldpath.FieldPathInfo
import com.weedow.spring.data.search.fieldpath.FieldPathResolver
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.convert.ConversionService
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class ExpressionResolverImplTest {

    @Mock
    private lateinit var fieldPathResolver: FieldPathResolver

    @Mock
    private lateinit var conversionService: ConversionService

    @InjectMocks
    private lateinit var expressionResolver: ExpressionResolverImpl

    @Test
    fun resolve_expression_with_with_null_value() {
        val rootClass = Person::class.java
        val fieldPath = "addressEntities"
        val fieldName = "addressEntities"
        val fieldClass = Address::class.java
        val fieldValue = NullValue.NULL_VALUE
        val operator = Operator.EQUALS

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue), operator, false)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(SimpleExpression(Operator.EQUALS, fieldInfo, NullValue))

        verifyZeroInteractions(conversionService)
    }

    @ParameterizedTest
    @MethodSource("single_value_with_operator_parameters")
    fun resolve_expression_with_single_value(field: String, fieldValue: String, operator: Operator) {
        val rootClass = Person::class.java
        val fieldPath = field
        val fieldName = field
        val fieldClass = String::class.java

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))
        whenever(conversionService.convert(fieldValue, fieldClass))
                .thenReturn(fieldValue)

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue), operator, false)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(SimpleExpression(operator, fieldInfo, fieldValue))
    }

    @ParameterizedTest
    @MethodSource("single_date_value_with_operator_parameters")
    fun resolve_expression_with_single_date_value(field: String, fieldValue: String, operator: Operator) {
        val rootClass = Person::class.java
        val fieldPath = field
        val fieldName = field
        val fieldClass = String::class.java

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue), operator, false)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(SimpleExpression(operator, fieldInfo, fieldValue))
    }

    @Test
    fun resolve_expression_with_multiple_values_and_in_operator() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldClass = String::class.java
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val operator = Operator.IN

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))
        whenever(conversionService.convert(fieldValue1, fieldClass))
                .thenReturn(fieldValue1)
        whenever(conversionService.convert(fieldValue2, fieldClass))
                .thenReturn(fieldValue2)

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue1, fieldValue2), operator, false)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(SimpleExpression(operator, fieldInfo, listOf(fieldValue1, fieldValue2)))
    }

    @Test
    fun resolve_expression_with_negative_operator() {
        val rootClass = Person::class.java
        val fieldPath = "addressEntities"
        val fieldName = "addressEntities"
        val fieldClass = Address::class.java
        val fieldValue = NullValue.NULL_VALUE
        val operator = Operator.EQUALS
        val negated = true

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue), operator, negated)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(operator, fieldInfo, NullValue)))

        verifyZeroInteractions(conversionService)
    }

    @ParameterizedTest
    @MethodSource("single_value_with_operator_parameters")
    fun resolve_expression_with_single_value_and_negative_operator(field: String, fieldValue: String, operator: Operator) {
        val rootClass = Person::class.java
        val fieldPath = field
        val fieldName = field
        val fieldClass = String::class.java
        val negated = true

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))
        whenever(conversionService.convert(fieldValue, fieldClass))
                .thenReturn(fieldValue)

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue), operator, negated)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(operator, fieldInfo, fieldValue)))
    }

    @ParameterizedTest
    @MethodSource("single_date_value_with_operator_parameters")
    fun resolve_expression_with_single_date_value_and_negative_operator(field: String, fieldValue: String, operator: Operator) {
        val rootClass = Person::class.java
        val fieldPath = field
        val fieldName = field
        val fieldClass = String::class.java
        val negated = true

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue), operator, negated)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(operator, fieldInfo, fieldValue)))
    }

    @Test
    fun resolve_expression_with_multiple_values_and_not_in_operator() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val fieldClass = String::class.java
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val operator = Operator.IN
        val negated = true

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, fieldName, fieldClass, rootClass))
        whenever(conversionService.convert(fieldValue1, fieldClass))
                .thenReturn(fieldValue1)
        whenever(conversionService.convert(fieldValue2, fieldClass))
                .thenReturn(fieldValue2)

        val expression = expressionResolver.resolveExpression(rootClass, fieldPath, listOf(fieldValue1, fieldValue2), operator, negated)

        val fieldInfo = FieldInfo(fieldPath, fieldName, rootClass)
        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(operator, fieldInfo, listOf(fieldValue1, fieldValue2))))
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun single_value_with_operator_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("firstName", "John", Operator.EQUALS),
                    Arguments.of("firstName", "Jo*", Operator.MATCHES),
                    Arguments.of("firstName", "JO*", Operator.IMATCHES),
                    Arguments.of("height", "174", Operator.LESS_THAN),
                    Arguments.of("height", "174", Operator.LESS_THAN_OR_EQUALS),
                    Arguments.of("height", "180", Operator.GREATER_THAN),
                    Arguments.of("height", "180", Operator.GREATER_THAN_OR_EQUALS)
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun single_date_value_with_operator_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("birthday", "CURRENT_DATE", Operator.EQUALS),
                    Arguments.of("birthday", "CURRENT_DATE", Operator.LESS_THAN),
                    Arguments.of("birthday", "CURRENT_DATE", Operator.LESS_THAN_OR_EQUALS),
                    Arguments.of("birthday", "CURRENT_DATE", Operator.GREATER_THAN),
                    Arguments.of("birthday", "CURRENT_DATE", Operator.GREATER_THAN_OR_EQUALS),
                    Arguments.of("birthday", "CURRENT_TIME", Operator.EQUALS),
                    Arguments.of("birthday", "CURRENT_TIME", Operator.LESS_THAN),
                    Arguments.of("birthday", "CURRENT_TIME", Operator.LESS_THAN_OR_EQUALS),
                    Arguments.of("birthday", "CURRENT_TIME", Operator.GREATER_THAN),
                    Arguments.of("birthday", "CURRENT_TIME", Operator.GREATER_THAN_OR_EQUALS),
                    Arguments.of("birthday", "CURRENT_DATE_TIME", Operator.EQUALS),
                    Arguments.of("birthday", "CURRENT_DATE_TIME", Operator.LESS_THAN),
                    Arguments.of("birthday", "CURRENT_DATE_TIME", Operator.LESS_THAN_OR_EQUALS),
                    Arguments.of("birthday", "CURRENT_DATE_TIME", Operator.GREATER_THAN),
                    Arguments.of("birthday", "CURRENT_DATE_TIME", Operator.GREATER_THAN_OR_EQUALS)
            )
        }
    }

}
