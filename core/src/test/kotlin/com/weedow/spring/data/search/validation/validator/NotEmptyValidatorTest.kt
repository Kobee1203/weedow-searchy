package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.spring.data.search.expression.Operator
import com.weedow.spring.data.search.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_CODE
import com.weedow.spring.data.search.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_MESSAGE
import org.junit.jupiter.api.Test

internal class NotEmptyValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression = mockFieldExpression("fieldpath", "fieldpath", Operator.EQUALS, "value")
        val dataSearchErrors = mockDataSearchErrors()

        val validator = NotEmptyValidator()
        validator.validate(listOf(fieldExpression), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val dataSearchErrors = mockDataSearchErrors()

        val validator = NotEmptyValidator()
        validator.validate(listOf(), dataSearchErrors)

        verify(dataSearchErrors).reject(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE)
    }

}