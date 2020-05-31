package com.weedow.spring.data.search.expression

import com.neovisionaries.i18n.CountryCode
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.example.model.Address
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.field.FieldPathInfo
import com.weedow.spring.data.search.field.FieldPathResolver
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.convert.ConversionService

@ExtendWith(MockitoExtension::class)
internal class ExpressionMapperImplTest {

    @Mock
    private lateinit var fieldPathResolver: FieldPathResolver

    @Mock
    private lateinit var conversionService: ConversionService

    @InjectMocks
    private lateinit var expressionMapper: ExpressionMapperImpl

    @Test
    fun to_root_expression_without_params() {
        val rootClass = Person::class.java
        val params = mapOf<String, List<String>>()

        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull()
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).isEmpty()

        verifyZeroInteractions(fieldPathResolver)
        verifyZeroInteractions(conversionService)
    }

    @Test
    fun to_root_expression_with_null_param_value() {
        val rootClass = Person::class.java
        val fieldPath = "addressEntities"
        val fieldValue = NullValue.NULL_VALUE
        val field = rootClass.getDeclaredField("addressEntities")
        val fieldClass = Address::class.java

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, rootClass, field, fieldClass))

        val params = mapOf(fieldPath to listOf(fieldValue))
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull()
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(
                ExpressionUtils.equals(FieldInfo(fieldPath, rootClass, field, fieldClass), NullValue)
        )

        verifyZeroInteractions(conversionService)
    }

    @Test
    fun to_root_expression_with_single_param_value() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldValue = "John"
        val field = rootClass.getDeclaredField("firstName")
        val fieldClass = String::class.java

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, rootClass, field, fieldClass))
        whenever(conversionService.convert(fieldValue, fieldClass))
                .thenReturn("John")

        val params = mapOf(fieldPath to listOf(fieldValue))
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull()
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(
                ExpressionUtils.equals(FieldInfo(fieldPath, rootClass, field, fieldClass), "John")
        )
    }

    @Test
    fun to_root_expression_with_multiple_param_values() {
        val rootClass = Person::class.java
        val fieldPath = "firstName"
        val fieldValue1 = "John"
        val fieldValue2 = "Jane"
        val field = rootClass.getDeclaredField("firstName")
        val fieldClass = String::class.java

        whenever(fieldPathResolver.resolveFieldPath(rootClass, fieldPath))
                .thenReturn(FieldPathInfo(fieldPath, rootClass, field, fieldClass))
        whenever(conversionService.convert(fieldValue1, fieldClass))
                .thenReturn("John")
        whenever(conversionService.convert(fieldValue2, fieldClass))
                .thenReturn("Jane")

        val params = mapOf(fieldPath to listOf(fieldValue1, fieldValue2))
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull()
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(
                ExpressionUtils.`in`(FieldInfo(fieldPath, rootClass, field, fieldClass), listOf("John", "Jane"))
        )
    }

    @Test
    fun to_root_expression_with_multiple_params() {
        val rootClass = Person::class.java
        val parentClass1 = Person::class.java
        val fieldPath1 = "firstName"
        val fieldValue11 = "John"
        val fieldValue12 = "Jane"
        val field1 = parentClass1.getDeclaredField("firstName")
        val fieldClass1 = String::class.java
        val parentClass2 = Address::class.java
        val fieldPath2 = "addressEntities.country"
        val fieldValue21 = "FR"
        val field2 = Address::class.java.getDeclaredField("country")
        val fieldClass2 = CountryCode::class.java

        whenever(fieldPathResolver.resolveFieldPath(parentClass1, fieldPath1))
                .thenReturn(FieldPathInfo(fieldPath1, parentClass1, field1, fieldClass1))
        whenever(fieldPathResolver.resolveFieldPath(parentClass1, fieldPath2))
                .thenReturn(FieldPathInfo(fieldPath2, parentClass2, field2, fieldClass2))
        whenever(conversionService.convert(fieldValue11, fieldClass1))
                .thenReturn("John")
        whenever(conversionService.convert(fieldValue12, fieldClass1))
                .thenReturn("Jane")
        whenever(conversionService.convert(fieldValue21, fieldClass2))
                .thenReturn(CountryCode.FR)

        val params = mapOf(
                fieldPath1 to listOf(fieldValue11, fieldValue12),
                fieldPath2 to listOf(fieldValue21)
        )
        val rootExpression = expressionMapper.toExpression(params, rootClass)

        assertThat(rootExpression).isNotNull()
        val expressions = (rootExpression as RootExpressionImpl<Person>).expressions
        assertThat(expressions).containsExactly(
                ExpressionUtils.`in`(FieldInfo(fieldPath1, parentClass1, field1, fieldClass1), listOf("John", "Jane")),
                ExpressionUtils.equals(FieldInfo(fieldPath2, parentClass2, field2, fieldClass2), CountryCode.FR)
        )
    }

}