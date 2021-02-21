package com.weedow.searchy.mongodb.converter

import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MongoConvertersTest {

    @Test
    fun getConverters_with_vararg_parameter() {
        val converter1 = mock<MongoConverter<*, *>>()
        val converter2 = mock<MongoConverter<*, *>>()
        val mongoConverters = MongoConverters.of(converter1, converter2)
        assertThat(mongoConverters.converters).containsExactly(converter1, converter2)
    }

    @Test
    fun getConverters_with_list_parameter() {
        val converter1 = mock<MongoConverter<*, *>>()
        val converter2 = mock<MongoConverter<*, *>>()
        val mongoConverters = MongoConverters.of(listOf(converter1, converter2))
        assertThat(mongoConverters.converters).containsExactly(converter1, converter2)
    }
}