package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.*
import com.weedow.searchy.expression.Operator
import com.weedow.searchy.utils.NullValue
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class NotNullValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "Value 1")
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.IN, listOf(15, 30))
        val searchyErrors = mockSearchyErrors()

        val validator = NotNullValidator("field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), searchyErrors)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, NullValue)
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.IN, listOf(NullValue.NULL_VALUE, "not null value"))
        val searchyErrors = mockSearchyErrors()

        val validator = NotNullValidator("field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), searchyErrors)

        argumentCaptor<String> {
            verify(searchyErrors, times(2)).reject(eq("not-null"), eq("Invalid value for expression ''{0}''. Must not be null."), capture())

            Assertions.assertThat(allValues).containsExactly(
                "field1 EQUALS NullValue",
                "field2 IN [null, not null value]"
            )
        }
    }

}