package com.weedow.searchy.config

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.searchy.alias.AliasResolverRegistry
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.convert.converter.ConverterRegistry

// Not much to test, but exercise to prevent code coverage tool from showing red for default methods
@ExtendWith(MockitoExtension::class)
internal class SearchyConfigurerTest {

    @Test
    fun test_default_method() {
        val searchyConfigurer = object : SearchyConfigurer {
        }

        val searchyDescriptorRegistry = mock<SearchyDescriptorRegistry>()
        searchyConfigurer.addSearchyDescriptors(searchyDescriptorRegistry)

        val aliasResolverRegistry = mock<AliasResolverRegistry>()
        searchyConfigurer.addAliasResolvers(aliasResolverRegistry)

        val converterRegistry = mock<ConverterRegistry>()
        searchyConfigurer.addConverters(converterRegistry)

        verifyZeroInteractions(searchyDescriptorRegistry)
        verifyZeroInteractions(aliasResolverRegistry)
        verifyZeroInteractions(converterRegistry)
    }

}