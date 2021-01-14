package com.weedow.searchy.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// Not much to test, but exercise to prevent code coverage tool from showing red
internal class SearchyErrorTest {

    @Test
    fun testEquals() {
        val searchyError1 = SearchyError("123", "An error occurred: {0}", arrayOf("Invalid Value"))
        val searchyError2 = SearchyError("123", "An error occurred: {0}", arrayOf("Invalid Value"))
        val searchyError3 = SearchyError("321", "An error occurred: {0}", arrayOf("Invalid Value"))
        val searchyError4 = SearchyError("123", "Unexpected error: {0}", arrayOf("Invalid Value"))
        val searchyError5 = SearchyError("123", "An error occurred: {0}", arrayOf("Missing Value"))
        val searchyError6 = SearchyError("123", "An error occurred: {0}")
        val searchyError7 = SearchyError("123", "An error occurred: {0}")

        assertThat(searchyError1).isEqualTo(searchyError2)
        assertThat(searchyError1).isNotEqualTo(searchyError3)
        assertThat(searchyError1).isNotEqualTo(searchyError4)
        assertThat(searchyError1).isNotEqualTo(searchyError5)
        assertThat(searchyError1).isNotEqualTo(searchyError6)
        assertThat(searchyError6).isEqualTo(searchyError7)
    }

    @Test
    fun testHashCode() {
        val searchyError1 = SearchyError("123", "An error occurred: {0}", arrayOf("Invalid Value"))
        val searchyError2 = SearchyError("123", "An error occurred: {0}", arrayOf("Invalid Value"))
        val searchyError3 = SearchyError("321", "An error occurred: {0}", arrayOf("Invalid Value"))
        val searchyError4 = SearchyError("123", "Unexpected error: {0}", arrayOf("Invalid Value"))
        val searchyError5 = SearchyError("123", "An error occurred: {0}", arrayOf("Missing Value"))
        val searchyError6 = SearchyError("123", "An error occurred: {0}")
        val searchyError7 = SearchyError("123", "An error occurred: {0}")

        assertThat(searchyError1).hasSameHashCodeAs(searchyError2)
        assertThat(searchyError1.hashCode()).isNotEqualTo(searchyError3.hashCode())
        assertThat(searchyError1.hashCode()).isNotEqualTo(searchyError4.hashCode())
        assertThat(searchyError1.hashCode()).isNotEqualTo(searchyError5.hashCode())
        assertThat(searchyError1.hashCode()).isNotEqualTo(searchyError6.hashCode())
        assertThat(searchyError6).hasSameHashCodeAs(searchyError7)
    }
}