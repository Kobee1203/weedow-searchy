package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.expression.Operator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PatternValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "john_12345")
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.IN, listOf("JOHN_13245", "Jane_2"))
        val dataSearchErrors = mockDataSearchErrors()

        val validator = PatternValidator("[a-zA-Z_0-9]+", "field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "john.12345")
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.IN, listOf("JOHN.13245", "Jane.2"))
        val dataSearchErrors = mockDataSearchErrors()

        val validator = PatternValidator("[a-zA-Z_0-9]+", "field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        argumentCaptor<String> {
            verify(dataSearchErrors, times(3)).reject(eq("pattern"), eq("Invalid value for expression ''{0}''. Must match ''{1}''."), capture())

            assertThat(allValues).containsExactly(
                "field1 EQUALS john.12345",
                "[a-zA-Z_0-9]+",

                "field2 IN [JOHN.13245, Jane.2]",
                "[a-zA-Z_0-9]+",

                "field2 IN [JOHN.13245, Jane.2]",
                "[a-zA-Z_0-9]+"
            )
        }
    }

}