package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.expression.Operator
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class NotNullValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "Value 1")
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.IN, listOf(15, 30))
        val dataSearchErrors = mockDataSearchErrors()

        val validator = NotNullValidator("field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, NullValue)
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.IN, listOf(NullValue.NULL_VALUE, "not null value"))
        val dataSearchErrors = mockDataSearchErrors()

        val validator = NotNullValidator("field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        argumentCaptor<String> {
            verify(dataSearchErrors, times(2)).reject(eq("not-null"), eq("Invalid value for expression ''{0}''. Must not be null."), capture())

            Assertions.assertThat(allValues).containsExactly(
                "field1 EQUALS NullValue",
                "field2 IN [null, not null value]"
            )
        }
    }

}