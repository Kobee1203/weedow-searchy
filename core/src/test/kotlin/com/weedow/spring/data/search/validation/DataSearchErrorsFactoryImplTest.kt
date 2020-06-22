package com.weedow.spring.data.search.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DataSearchErrorsFactoryImplTest {

    @Test
    fun getDataSearchErrors() {
        val dataSearchErrors = DataSearchErrorsFactoryImpl().getDataSearchErrors()

        assertThat(dataSearchErrors).isNotNull()
        assertThat(dataSearchErrors).isInstanceOf(DefaultDataSearchErrors::class.java)

        assertThat(DataSearchErrorsFactoryImpl().getDataSearchErrors()).isNotSameAs(DataSearchErrorsFactoryImpl().getDataSearchErrors())
    }
}