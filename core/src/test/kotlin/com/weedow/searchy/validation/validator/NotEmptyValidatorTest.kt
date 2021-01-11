package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.searchy.expression.Operator
import com.weedow.searchy.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_CODE
import com.weedow.searchy.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_MESSAGE
import org.junit.jupiter.api.Test

internal class NotEmptyValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression = mockFieldExpression("fieldpath", "fieldpath", Operator.EQUALS, "value")
        val searchyErrors = mockSearchyErrors()

        val validator = NotEmptyValidator()
        validator.validate(listOf(fieldExpression), searchyErrors)

        verifyZeroInteractions(searchyErrors)
    }

    @Test
    fun validate_without_success() {
        val searchyErrors = mockSearchyErrors()

        val validator = NotEmptyValidator()
        validator.validate(listOf(), searchyErrors)

        verify(searchyErrors).reject(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE)
    }

}