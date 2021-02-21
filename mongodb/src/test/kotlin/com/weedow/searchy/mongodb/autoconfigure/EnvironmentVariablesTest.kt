package com.weedow.searchy.mongodb.autoconfigure

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.Test
import java.util.UUID.randomUUID


internal class EnvironmentVariablesTest {

    @Test
    fun environment_variables_that_are_set_are_available_in_system_getenv() {
        val variableName1 = "myvariable1"
        val value1 = randomValue()
        val variableName2 = "myvariable2"
        val value2 = randomValue()

        val env = EnvironmentVariables()
        env.set(variableName1, value1)
        env.set(variableName2, value2)

        assertThat(System.getenv(variableName1)).isEqualTo(value1)
        assertThat(System.getenv(variableName2)).isEqualTo(value2)
        assertThat(System.getenv()).contains(
            entry(variableName1, value1),
            entry(variableName2, value2)
        )
    }

    @Test
    fun environment_variable_that_is_set_to_null_is_null_is_not_stored_in_system_getenv() {
        val variableName = "myvariable"
        val value = randomValue()

        val env = EnvironmentVariables()
        env.set(variableName, value)
        env.set(variableName, null)

        assertThat(System.getenv(variableName)).isNull()
        assertThat(System.getenv()).doesNotContainKey(variableName)
    }

    @Test
    fun environment_variables_that_are_cleared_are_null_and_are_not_stored_in_system_getenv() {
        val variableName1 = "myvariable1"
        val value1 = randomValue()
        val variableName2 = "myvariable2"
        val value2 = randomValue()

        val env = EnvironmentVariables()
        env.set(variableName1, value1)
        env.set(variableName2, value2)
        env.clear(variableName1, variableName2)

        assertThat(System.getenv(variableName1)).isNull()
        assertThat(System.getenv(variableName2)).isNull()
        assertThat(System.getenv()).doesNotContainKeys(variableName1, variableName2)
    }

    private fun randomValue(): String {
        return randomUUID().toString()
    }

}