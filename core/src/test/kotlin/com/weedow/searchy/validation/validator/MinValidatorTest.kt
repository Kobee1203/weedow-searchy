package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.searchy.expression.Operator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MinValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, 9.6)
        val searchyErrors = mockSearchyErrors()

        val validator = MinValidator(9.5, "myfield")
        validator.validate(listOf(fieldExpression), searchyErrors)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression = mockFieldExpression("myfield", "myfield", Operator.EQUALS, 9.6)
        val searchyErrors = mockSearchyErrors()

        val minValue = 9.7
        val validator = MinValidator(minValue, "myfield")
        validator.validate(listOf(fieldExpression), searchyErrors)

        argumentCaptor<Any> {
            verify(searchyErrors).reject(
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