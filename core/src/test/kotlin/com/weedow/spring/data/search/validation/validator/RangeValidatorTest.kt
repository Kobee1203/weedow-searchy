package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.expression.Operator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class RangeValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("myfield1", "myfield1", Operator.EQUALS, 9.5)
        val fieldExpression2 = mockFieldExpression("myfield2", "myfield2", Operator.EQUALS, 7.3)
        val dataSearchErrors = mockDataSearchErrors()

        val validator = RangeValidator(7.2, 9.6, "myfield1", "myfield2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("myfield1", "myfield1", Operator.EQUALS, 9.7)
        val fieldExpression2 = mockFieldExpression("myfield2", "myfield2", Operator.EQUALS, 7.1)
        val dataSearchErrors = mockDataSearchErrors()

        val minValue = 7.2
        val maxValue = 9.6
        val validator = RangeValidator(minValue, maxValue, "myfield1", "myfield2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        argumentCaptor<Any> {
            verify(dataSearchErrors, times(2)).reject(eq("range"), eq("Invalid number value for expression ''{0}''. Must between ''{1}'' and ''{2}''."), capture())

            Assertions.assertThat(allValues).containsExactly(
                    "myfield1 EQUALS 9.7",
                    minValue,
                    maxValue,

                    "myfield2 EQUALS 7.1",
                    minValue,
                    maxValue
            )
        }
    }

}