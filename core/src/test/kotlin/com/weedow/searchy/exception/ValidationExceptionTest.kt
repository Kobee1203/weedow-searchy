package com.weedow.searchy.exception

import com.weedow.searchy.validation.SearchyError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class ValidationExceptionTest {

    @Test
    fun test_with_default_arguments() {
        val error1 = SearchyError("4001", "This is the first error")
        val error2 = SearchyError("4002", "This is the second error")

        val validationException = ValidationException(listOf(error1, error2))

        assertThat(validationException.errors).containsExactly(error1, error2)
        assertThat(validationException.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(validationException.reason).isEqualTo("Validation Errors: [${error1.errorCode}: ${error1.errorMessage}, ${error2.errorCode}: ${error2.errorMessage}]")
        assertThat(validationException.message).isEqualTo("${HttpStatus.BAD_REQUEST} \"Validation Errors: [${error1.errorCode}: ${error1.errorMessage}, ${error2.errorCode}: ${error2.errorMessage}]\"")
    }

    @Test
    fun test_with_custom_arguments() {
        val error1 = SearchyError("5001", "This is the first error")
        val error2 = SearchyError("5002", "This is the second error")

        val validationException = ValidationException(listOf(error1, error2), HttpStatus.INTERNAL_SERVER_ERROR, "Custom Reason")

        assertThat(validationException.errors).containsExactly(error1, error2)
        assertThat(validationException.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(validationException.reason).isEqualTo("Custom Reason")
        assertThat(validationException.message).isEqualTo("${HttpStatus.INTERNAL_SERVER_ERROR} \"Custom Reason\"")
    }
}