package com.weedow.spring.data.search.validation.validator

import com.nhaarman.mockitokotlin2.*
import com.weedow.spring.data.search.expression.Operator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class UrlValidatorTest : BaseValidatorTest() {

    @Test
    fun validate_successfully() {
        val fieldExpression1 = mockFieldExpression("url", "url", Operator.EQUALS, "http://www.acme.com/")
        val fieldExpression2 = mockFieldExpression("otherUrls", "otherUrls", Operator.IN, listOf("https://github.com/Kobee1203/spring-data-search", "https://github.com/Kobee1203/spring-data-search#build"))
        val dataSearchErrors = mockDataSearchErrors()

        val validator = UrlValidator("url", "otherUrls")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        verifyZeroInteractions(dataSearchErrors)
    }

    @Test
    fun validate_without_success() {
        val fieldExpression1 = mockFieldExpression("url", "url", Operator.EQUALS, "www-acme-com")
        val fieldExpression2 = mockFieldExpression("otherUrls", "otherUrls", Operator.IN, listOf("github.com_Kobee1203_spring-data-search", "github.com/Kobee1203/spring-data-search#build"))
        val dataSearchErrors = mockDataSearchErrors()

        val validator = UrlValidator("url", "otherUrls")
        validator.validate(listOf(fieldExpression1, fieldExpression2), dataSearchErrors)

        argumentCaptor<String> {
            verify(dataSearchErrors, times(3)).reject(eq("url"), eq("Invalid URL value for expression ''{0}''"), capture())

            Assertions.assertThat(allValues).containsExactly(
                    "url EQUALS www-acme-com",
                    "otherUrls IN [github.com_Kobee1203_spring-data-search, github.com/Kobee1203/spring-data-search#build]",
                    "otherUrls IN [github.com_Kobee1203_spring-data-search, github.com/Kobee1203/spring-data-search#build]"
            )
        }
    }

}