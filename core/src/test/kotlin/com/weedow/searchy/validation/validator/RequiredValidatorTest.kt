package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.searchy.expression.Operator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class RequiredValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "value 1")
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.EQUALS, "value 2")
        val fieldExpression3 = mockFieldExpression("field3", "field3", Operator.EQUALS, "value 3")
        val searchyErrors = mockSearchyErrors()

        val validator = RequiredValidator("field1", "field2")
        validator.validate(listOf(fieldExpression1, fieldExpression2, fieldExpression3), searchyErrors)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_successfully_when_empty_field_paths() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "value 1")
        val searchyErrors = mockSearchyErrors()

        val validator = RequiredValidator()

        validator.validate(listOf(fieldExpression1), searchyErrors)
        validator.validate(emptyList(), searchyErrors)

        verifyZeroInteractions(fieldExpression1)
        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("field1", "field1", Operator.EQUALS, "value 1")
        val fieldExpression2 = mockFieldExpression("field2", "field2", Operator.EQUALS, "value 2")
        val searchyErrors = mockSearchyErrors()

        val validator = RequiredValidator("field1", "field3")
        validator.validate(listOf(fieldExpression1, fieldExpression2), searchyErrors)

        argumentCaptor<Any> {
            verify(searchyErrors).reject(eq("required"), eq("Missing required field: {0}."), capture())

            Assertions.assertThat(allValues).containsExactly(
                "field3"
            )
        }
    }

    @Test
    fun validate_without_success_when_empty_field_expression() {
        val searchyErrors = mockSearchyErrors()

        val validator = RequiredValidator("field1", "field3")
        validator.validate(emptyList(), searchyErrors)

        argumentCaptor<Any> {
            verify(searchyErrors).reject(eq("required"), eq("Missing required fields: {0}."), capture())

            Assertions.assertThat(allValues).containsExactly(
                arrayOf("field1", "field3")
            )
        }
    }

}