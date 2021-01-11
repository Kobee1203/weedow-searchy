package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.expression.Operator
import com.weedow.searchy.validation.SearchyErrors
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

internal class AbstractNumberValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val number = 9.6

        val fieldExpressions = mutableListOf<FieldExpression>()
        fieldExpressions.add(fieldExpression(number) { BigDecimal(it.toString()) })
        fieldExpressions.add(fieldExpression(number) { BigInteger(it.toInt().toString()) })
        @Suppress("DEPRECATION")
        fieldExpressions.add(fieldExpression(number, Double::toByte))
        @Suppress("DEPRECATION")
        fieldExpressions.add(fieldExpression(number, Double::toShort))
        fieldExpressions.add(fieldExpression(number, Double::toInt))
        fieldExpressions.add(fieldExpression(number, Double::toLong))
        fieldExpressions.add(fieldExpression(number, Double::toDouble))
        fieldExpressions.add(fieldExpression(number, Double::toFloat))
        fieldExpressions.add(fieldExpression(number) { AtomicInteger(number.toInt()) })

        val searchyErrors = mockSearchyErrors()

        val validator = MyNumberValidator("myfield")
        validator.validate(fieldExpressions, searchyErrors)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_number_without_parsable_string_representation() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, Fraction.getFraction(15, 2))

        val searchyErrors = mockSearchyErrors()

        assertThatThrownBy { MyNumberValidator("myfield").validate(listOf(fieldExpression), searchyErrors) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("The given number (\"15/2\" of class com.weedow.searchy.validation.validator.Fraction) does not have a parsable string representation")
            .hasCauseInstanceOf(NumberFormatException::class.java)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_with_value_not_number() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, "10.1")

        val searchyErrors = mockSearchyErrors()

        val validator = MyNumberValidator("myfield")
        validator.validate(listOf(fieldExpression), searchyErrors)

        argumentCaptor<String> {
            verify(searchyErrors).reject(
                eq("not-a-number"),
                eq("Invalid value for expression ''{0}''. ''{1}'' is not a Number instance."),
                capture()
            )

            Assertions.assertThat(allValues).containsExactly(
                "myfield EQUALS 10.1",
                "10.1"
            )
        }
    }

    private fun fieldExpression(number: Double, function: (Double) -> Number): FieldExpression {
        return mockFieldExpression("myfield", "myfield", Operator.EQUALS, function(number))
    }

    class MyNumberValidator(
        vararg fieldPaths: String
    ) : AbstractNumberValidator(*fieldPaths) {
        override fun doValidate(value: Number, fieldExpression: FieldExpression, errors: SearchyErrors) {
            if (compare(value, 10.1) > 1) {
                errors.reject("my-number", "Value must be less or equals to: {0}", value)
            }
        }

    }
}