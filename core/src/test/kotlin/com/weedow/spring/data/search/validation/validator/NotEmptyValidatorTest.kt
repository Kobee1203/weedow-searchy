package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors
import com.weedow.spring.data.search.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_CODE
import com.weedow.spring.data.search.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_MESSAGE
import org.junit.jupiter.api.Test

internal class NotEmptyValidatorTest {

    @Test
    fun validate_successfully() {
        val fieldExpression = mock<FieldExpression>()
        val dataSearchErrors = mock<DataSearchErrors>()

        val validator = NotEmptyValidator()
        validator.validate(listOf(fieldExpression), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val dataSearchErrors = mock<DataSearchErrors>()

        val validator = NotEmptyValidator()
        validator.validate(listOf(), dataSearchErrors)

        verify(dataSearchErrors).reject(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE)
    }
}