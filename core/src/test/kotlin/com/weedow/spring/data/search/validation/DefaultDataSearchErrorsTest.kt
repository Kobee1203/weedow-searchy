package com.weedow.spring.data.search.validation

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test

internal class DefaultDataSearchErrorsTest {

    @Test
    fun reject() {
        val errors = DefaultDataSearchErrors()

        assertThat(errors.hasErrors()).isFalse
        assertThat(errors.getAllErrors()).isEmpty()

        errors.reject("1001", "Invalid Value")
        assertThat(errors.hasErrors()).isTrue
        assertThat(errors.getAllErrors())
            .extracting("errorCode", "errorMessage")
            .containsExactly(Tuple.tuple("1001", "Invalid Value"))

        errors.reject("1002", "Illegal Value")
        assertThat(errors.hasErrors()).isTrue
        assertThat(errors.getAllErrors())
            .extracting("errorCode", "errorMessage")
            .containsExactly(
                Tuple.tuple("1001", "Invalid Value"),
                Tuple.tuple("1002", "Illegal Value")
            )
    }
}