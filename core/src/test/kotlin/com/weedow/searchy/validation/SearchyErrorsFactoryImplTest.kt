package com.weedow.searchy.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SearchyErrorsFactoryImplTest {

    @Test
    fun getSearchyErrors() {
        val searchyErrors = SearchyErrorsFactoryImpl().getSearchyErrors()

        assertThat(searchyErrors).isNotNull
        assertThat(searchyErrors).isInstanceOf(DefaultSearchyErrors::class.java)

        assertThat(SearchyErrorsFactoryImpl().getSearchyErrors()).isNotSameAs(SearchyErrorsFactoryImpl().getSearchyErrors())
    }
}