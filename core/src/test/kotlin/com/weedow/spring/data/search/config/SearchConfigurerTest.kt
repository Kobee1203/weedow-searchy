package com.weedow.spring.data.search.config

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.convert.converter.ConverterRegistry

// Not much to test, but exercise to prevent code coverage tool from showing red for default methods
@ExtendWith(MockitoExtension::class)
internal class SearchConfigurerTest {

    @Test
    fun test_default_method() {
        val searchConfigurer = object : SearchConfigurer {
        }

        val searchDescriptorRegistry = mock<SearchDescriptorRegistry>()
        searchConfigurer.addSearchDescriptors(searchDescriptorRegistry)

        val aliasResolverRegistry = mock<AliasResolverRegistry>()
        searchConfigurer.addAliasResolvers(aliasResolverRegistry)

        val converterRegistry = mock<ConverterRegistry>()
        searchConfigurer.addConverters(converterRegistry)

        verifyZeroInteractions(searchDescriptorRegistry)
        verifyZeroInteractions(aliasResolverRegistry)
        verifyZeroInteractions(converterRegistry)
    }

}