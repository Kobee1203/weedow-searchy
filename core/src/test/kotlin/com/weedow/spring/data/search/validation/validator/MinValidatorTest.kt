package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.spring.data.search.expression.Operator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MinValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, 9.6)
        val dataSearchErrors = mockDataSearchErrors()

        val validator = MinValidator(9.5, "myfield")
        validator.validate(listOf(fieldExpression), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, 9.6)
        val dataSearchErrors = mockDataSearchErrors()

        val minValue = 9.7
        val validator = MinValidator(minValue, "myfield")
        validator.validate(listOf(fieldExpression), dataSearchErrors)

        argumentCaptor<Any> {
            verify(dataSearchErrors).reject(
                eq("min"),
                eq("Invalid number value for expression ''{0}''. Must greater or equals to ''{1}''."),
                capture()
            )

            assertThat(allValues).containsExactly(
                "myfield EQUALS 9.6",
                minValue
            )
        }
    }
}