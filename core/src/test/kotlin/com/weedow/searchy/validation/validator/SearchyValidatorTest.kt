package com.weedow.searchy.validation.validator

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.validation.SearchyValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// Not much to test, but exercise to prevent code coverage tool from showing red for default methods
internal class SearchyValidatorTest {

    @Test
    fun test_default_method_validate_single() {
        val validator = object : SearchyValidator {}

        assertThat(validator.supports(mock())).isTrue

        validator.validateSingle(mock(), mock(), mock())
    }
}